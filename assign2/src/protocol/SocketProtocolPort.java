package protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import protocol.unit.ProtocolUnit;

public class SocketProtocolPort implements ProtocolPort {
    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final ProtocolParser parser;

    public SocketProtocolPort(Socket socket, ProtocolParser parser) throws IOException {
        this.socket = socket;
        this.parser = parser;

        InputStream input = socket.getInputStream();
        this.reader = new BufferedReader(new InputStreamReader(input));

        OutputStream output = socket.getOutputStream();
        this.writer = new PrintWriter(output, true);
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
        String line = reader.readLine();
        return parser.parse(line);
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
