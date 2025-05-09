package client;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import client.state.ClientState;
import client.state.RoomState;
import protocol.ProtocolParser;
import protocol.ProtocolParserImpl;
import protocol.ProtocolPort;
import protocol.SocketProtocolPort;
import protocol.unit.EofUnit;
import protocol.unit.ProtocolUnit;
import protocol.unit.SendUnit;
import structs.Message;
import utils.SSLSocketUtils;

public class Client {
    private final ProtocolPort port;
    private ClientState state;
    private final List<Message> messages;

    public Client(ProtocolPort port, ClientState initState) {
        this(port, initState, List.of());
    }

    public Client(ProtocolPort port, ClientState initState, List<Message> messages) {
        this.port = port;
        this.state = initState;
        this.state.setClient(this);
        this.messages = new ArrayList<>(messages);
    }

    public ClientState getState() {
        return state;
    }

    public void setState(ClientState state) {
        this.state = state;
    }

    public void receiveMessage(Message message) {
        messages.add(message);
    }

    public void run() {
        // TODO(Process-ing): Replace with real code
        setState(new RoomState(this, "JohnDoe" + new Random().nextInt(), "Lobby"));

        Thread.ofVirtual().start(() -> {
            int count = 1;
            try {
                while (true) {
                    String message = String.format("My message #%d", count);
                    ProtocolUnit unit = new SendUnit(((RoomState) state).getUsername(), message);

                    port.send(unit);
                    System.out.printf("You# %s\n", message);
                    Thread.sleep(1000);
                    count++;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        try {
            while (true) {
                ProtocolUnit unit = port.receive();
                if (unit instanceof EofUnit) {
                    System.out.println("Server closed connection");
                    break;
                }

                unit.accept(state);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                port.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static String getFileData(String fileName) {
        try (FileInputStream inputStream = new FileInputStream(fileName)) {
            return new String(inputStream.readAllBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        // TODO(Process-ing): Replace with real code
        String passwordFile = "client-pass.txt";

        int portNumber = 12345;  // TODO(Process-ing): Get from args

        String password = getFileData(passwordFile);

        ProtocolParser parser = new ProtocolParserImpl();

        InetAddress address;
        Socket socket;
        ProtocolPort port;

        try {
            address = InetAddress.getLocalHost();  // TODO(Process-ing): Get from args
            socket = SSLSocketUtils.newSocket(address, portNumber, password);
            port = new SocketProtocolPort(socket, parser);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ClientState initState = new RoomState(null, "JohnDoe", "Lobby");
        Client client = new Client(port, initState);

        client.run();
    }
}