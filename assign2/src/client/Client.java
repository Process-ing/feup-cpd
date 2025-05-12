package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import client.state.ClientState;
import client.state.RoomState;
import exception.EndpointUnreachableException;
import protocol.ProtocolParser;
import protocol.ProtocolParserImpl;
import protocol.ProtocolPort;
import protocol.SocketProtocolPort;
import protocol.unit.EofUnit;
import protocol.unit.ProtocolUnit;
import protocol.unit.SendUnit;
import structs.Message;

public class Client {
    private final ProtocolPort port;
    private ClientState state;
    private final List<Message> messages;
    private boolean done = false;

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
        done = false;
        setState(new RoomState(this, "JohnDoe" + new Random().nextInt(), "Lobby"));

        Thread.ofVirtual().start(() -> {
            int count = 1;
            try {
                while (!done) {
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
            } finally {
                done = true;
            }
        });


        try {
            while (!done) {
                ProtocolUnit unit = port.receive();
                if (unit instanceof EofUnit) {
                    port.reconnect();
                    continue;
                }

                unit.accept(state);
            }
        } catch (EndpointUnreachableException e) {
            System.out.println("Connection to server lost, terminating.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                port.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            done = true;
        }
    }

    public static void main(String[] args) {
        // TODO(Process-ing): Replace with real code

        int portNumber = 12345;  // TODO(Process-ing): Get from args
        ProtocolParser parser = new ProtocolParserImpl();

        InetAddress address;
        Socket socket;
        ProtocolPort port;

        try {
            address = InetAddress.getLocalHost();  // TODO(Process-ing): Get from args
            socket = new Socket(address, portNumber);
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