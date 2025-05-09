package client;

import client.state.ClientState;
import client.state.GuestState;
import client.state.RoomState;
import client.storage.SessionStore;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import protocol.ProtocolParser;
import protocol.ProtocolParserImpl;
import protocol.ProtocolPort;
import protocol.SocketProtocolPort;
import protocol.unit.AuthTokenUnit;
import protocol.unit.EnterUnit;
import protocol.unit.EofUnit;
import protocol.unit.InvalidUnit;
import protocol.unit.ProtocolUnit;
import protocol.unit.SendUnit;
import structs.Message;

public class Client {
    private final ProtocolPort port;
    private ClientState state;
    private final List<Message> messages;
    private final ProtocolParser parser;
    private ProtocolUnit previousUnit;
    private final SessionStore session;

    public Client(ProtocolPort port, ClientState initState, ProtocolParser parser) {
        this(port, initState, List.of(), parser);
    }

    public Client(ProtocolPort port, ClientState initState, List<Message> messages, ProtocolParser parser) {
        this.port = port;
        this.state = initState;
        this.state.setClient(this);
        this.messages = new ArrayList<>(messages);
        this.parser = parser;
        this.previousUnit = null;
        this.session = new SessionStore(Path.of(System.getProperty("user.dir"),
                             "..", "client", "data", "session.properties"));
    }

    public ProtocolUnit getPreviousUnit() {
        return previousUnit;
    }

    public ClientState getState() {
        return state;
    }

    public SessionStore getSession() {
        return session;
    }

    public void setState(ClientState state) {
        this.state = state;
    }

    public void receiveMessage(Message message) {
        messages.add(message);
    }

    public void run() {
        setState(new GuestState(this));

        if (session.exist()) {
            try {
                // login-token
                ProtocolUnit request = new AuthTokenUnit(session.getToken());
                port.send(request);
                previousUnit = request;

                // respond
                ProtocolUnit unit = port.receive();
                if (unit instanceof EofUnit) {
                    System.out.println("Server closed connection");
                    port.close();
                }
                unit.accept(state);

                if (session.getRoom() != null) {
                    // enter <room-name>
                    request = new EnterUnit(session.getRoom());
                    port.send(request);
                    previousUnit = request;

                    // respond
                    unit = port.receive();
                    if (unit instanceof EofUnit) {
                        System.out.println("Server closed connection");
                        port.close();
                    }
                    unit.accept(state);
                }
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }

        Thread.ofVirtual().start(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                while (true) {
                    try {
                        String input = scanner.nextLine();
                        ProtocolUnit request = null;
        
                        if (input.startsWith("/")) {
                            request = parser.parse(input.substring(1));
                        } else if (state instanceof RoomState roomState) {
                            request = new SendUnit(roomState.getUsername(), input);
                        }
        
                        if (request == null || request instanceof InvalidUnit) {
                            System.out.println("Invalid command");
                            continue;
                        }
        
                        port.send(request);
        
                        if (request instanceof SendUnit sendUnit) {
                            System.out.printf("You# %s\n", sendUnit.message());
                            continue;
                        }
                        previousUnit = request;
                    } catch (Exception e) {
                        System.out.println("Unexpected error: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.out.println("Unrecoverable error: " + e.getMessage());
            }
            /* } catch (InterruptedException e) {
                Thread.currentThread().interrupt();*/
            
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

        ClientState initState = new GuestState(null);
        Client client = new Client(port, initState, parser);

        client.run();
    }
}