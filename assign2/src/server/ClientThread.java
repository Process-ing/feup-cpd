package server;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

import protocol.ProtocolParser;
import protocol.ProtocolPort;
import protocol.SocketProtocolPort;
import protocol.unit.EofUnit;
import protocol.unit.ProtocolUnit;
import server.client.Client;

public class ClientThread {
    private Socket socket;
    private final Server server;
    private Client client;

    public ClientThread(Socket socket, Server server, Client client) {
        this.socket = socket;
        this.server = server;
        this.client = client;
    }

    public Server getServer() {
        return server;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void run() {
        try {
            ProtocolParser parser = server.getParser();
            ProtocolPort clientPort = new SocketProtocolPort(socket, parser);

            while (true) {
                ProtocolUnit request = clientPort.receive();
                if (request instanceof EofUnit)
                    break;

                Optional<ProtocolUnit> response = request.accept(client);
                if (response.isPresent())
                    clientPort.send(response.get());
            }

        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            client.cleanup();

            try {
                socket.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }
}
