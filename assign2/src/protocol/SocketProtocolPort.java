package protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import exception.EndpointUnreachableException;
import protocol.unit.ProtocolUnit;

public class SocketProtocolPort implements ProtocolPort {
    private static final int INITIAL_BACKOFF = 250; // ms
    private static final int MAX_RETRIES = 5;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private final ProtocolParser parser;
    private boolean closed = true;

    public SocketProtocolPort(Socket socket, ProtocolParser parser) throws IOException {
        this.socket = socket;
        this.parser = parser;

        InputStream input = socket.getInputStream();
        this.reader = new BufferedReader(new InputStreamReader(input));

        OutputStream output = socket.getOutputStream();
        this.writer = new PrintWriter(output, true);

        closed = false;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void send(ProtocolUnit unit) throws IOException {
        String serialized = unit.serialize();
        writer.println(serialized);
        writer.flush();
    }

    @Override
    public ProtocolUnit receive() throws IOException {
        String line;
        try {
            line = reader.readLine();
        } catch (SocketException e) {  // Connection reset by peer
            line = null;
        }

        closed = line == null;
        return parser.parse(line);
    }

    @Override
    public void reconnect() throws EndpointUnreachableException, IOException {
        if (!closed)
            return;

        InetAddress address = socket.getInetAddress();
        int port = socket.getPort();

        boolean reconnected = false;
        long backoff = INITIAL_BACKOFF;
        for (int tries = 0; tries < MAX_RETRIES; tries++) {
            try {
                socket = new Socket(address, port);
                reconnected = true;
                break;

            } catch (IOException e) {
                System.err.println("Connection to server failed, retrying...");
                backoff *= 2;

                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    break;
                }
            }
        }

        if (!reconnected) {
            throw new EndpointUnreachableException("Could not establish a connection to the server");
        }

        var input = socket.getInputStream();
        reader = new BufferedReader(new InputStreamReader(input));

        var output = socket.getOutputStream();
        writer = new PrintWriter(output, true);

        closed = false;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
