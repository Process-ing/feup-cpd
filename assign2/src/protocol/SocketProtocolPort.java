package protocol;

import exception.EndpointUnreachableException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import protocol.unit.EofUnit;
import protocol.unit.PingUnit;
import protocol.unit.PongUnit;
import protocol.unit.ProtocolUnit;

public class SocketProtocolPort implements ProtocolPort {
    private static final int INITIAL_BACKOFF = 1000; // ms
    private static final int MAX_RETRIES = 5;

    private final Supplier<Socket> socketFactory;
    private final ProtocolParser parser;

    private Optional<Socket> socket;
    private Optional<BufferedReader> reader;
    private Optional<PrintWriter> writer;

    private final ReentrantReadWriteLock thisLock;
    private final ReentrantLock writerLock;
    private final ReentrantLock readerLock;

    public SocketProtocolPort(Supplier<Socket> socketFactory, ProtocolParser parser) {
        this.socketFactory = socketFactory;
        this.parser = parser;

        this.socket = Optional.empty();
        this.reader = Optional.empty();
        this.writer = Optional.empty();

        this.thisLock = new ReentrantReadWriteLock();
        this.readerLock = new ReentrantLock();
        this.writerLock = new ReentrantLock();
    }

    public Optional<Socket> getSocket() {
        thisLock.readLock().lock();
        try {
            return socket;
        } finally {
            thisLock.readLock().unlock();
        }
    }

    @Override
    public void send(ProtocolUnit unit) throws IOException {
        PrintWriter writer;

        thisLock.readLock().lock();
        try {
            if (this.writer.isEmpty())  // If connection closed, just act as message lost
                return;

            writer = this.writer.get();

        } finally {
            thisLock.readLock().unlock();
        }

        String serialized = unit.serialize();

        writerLock.lock();
        try {
            writer.println(serialized);
            writer.flush();
        } finally {
            writerLock.unlock();
        }
    }

    @Override
    public ProtocolUnit receive() throws IOException {
        BufferedReader reader;
        String line;
        ProtocolUnit unit;

        thisLock.readLock().lock();
        try {
            if (this.reader.isEmpty())
                return new EofUnit();
                
            reader = this.reader.get();
        } finally {
            thisLock.readLock().unlock();
        }

        while (true) {
            readerLock.lock();
            try {
                line = reader.readLine();
            } catch (SocketTimeoutException e) {
                readerLock.unlock();
                send(new PingUnit());

                readerLock.lock();
                try {
                    line = reader.readLine();
                } catch (SocketTimeoutException e2) { // On ping timeout, assume connection is lost
                    close();
                    return new EofUnit();
                } catch (IOException e2) { // Connection reset by peer
                    close();
                    return new EofUnit();
                }

            } catch (IOException e) { // Connection reset by peer
                close();
                return new EofUnit();
            } finally {
                readerLock.unlock();
            }

            if (line == null) { // Connection closed by peer
                close();
                return new EofUnit();
            }

            unit = parser.parse(line);

            if (unit instanceof PingUnit) {  // Respond to ping
                send(new PongUnit());
                continue;

            } else if (unit instanceof PongUnit) {  // Ignore pong
                continue;
            }

            return unit;
        }
    }

    @Override
    public void connect() throws EndpointUnreachableException, IOException {
        thisLock.writeLock().lock();
        try {
            if (socket.isPresent()) {
                return;
            }

            long backoff = INITIAL_BACKOFF;
            for (int tries = 0; tries < MAX_RETRIES; tries++) {
                socket = Optional.ofNullable(socketFactory.get());
                if (socket.isPresent())
                    break;

                System.out.println("Connection to server failed, retrying...");
                backoff *= 2;

                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    break;
                }
            }

            if (socket.isEmpty()) {
                throw new EndpointUnreachableException("Could not establish a connection to the server");
            }

            Socket newSocket = socket.get();

            var input = newSocket.getInputStream();
            reader = Optional.of(new BufferedReader(new InputStreamReader(input)));

            var output = newSocket.getOutputStream();
            writer = Optional.of(new PrintWriter(output, true));
        } finally {
            thisLock.writeLock().unlock();
        }
    }

    @Override
    public boolean isConnected() {
        thisLock.readLock().lock();
        try {
            return socket.isPresent();
        } finally {
            thisLock.readLock().unlock();
        }
    }

    @Override
    public void close() throws IOException {
        thisLock.writeLock().lock();
        try {
            if (socket.isEmpty()) {
                return;
            }

            socket.get().close();
            reader = Optional.empty();
            writer = Optional.empty();
            socket = Optional.empty();

        } finally {
            thisLock.writeLock().unlock();
        }
    }
}
