package client;

import client.state.ClientState;
import client.state.GuestState;
import client.state.RoomState;
import client.storage.SessionStore;
import exception.EndpointUnreachableException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import protocol.ProtocolParser;
import protocol.ProtocolParserImpl;
import protocol.ProtocolPort;
import protocol.SocketProtocolPort;
import protocol.unit.AuthTokenUnit;
import protocol.unit.EnterUnit;
import protocol.unit.EofUnit;
import protocol.unit.InvalidUnit;
import protocol.unit.OkUnit;
import protocol.unit.ProtocolUnit;
import protocol.unit.SendUnit;
import structs.Message;
import utils.ConfigUtils;
import utils.SSLSocketUtils;

public class Client {
    private final ProtocolPort port;
    private ClientState state;
    private final List<Message> messages;
    private final ProtocolParser parser;
    private ProtocolUnit previousUnit;
    private final SessionStore session;
    private boolean done = false;


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
        done = false;
        setState(new GuestState(this));

        restoreSession();

        Thread.ofVirtual().start(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                while (!done) {
                    try {
                        String input = scanner.nextLine();
                        ProtocolUnit request = null;

                        if (input.startsWith("/")) {
                            request = parser.parse(input.substring(1));
                        } else if (state instanceof RoomState) {
                            request = new SendUnit(input);
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

    private void restoreSession() {
        if (!session.hasSession()) return;

        ProtocolUnit request, unit;

        try {
            // login-token
            request = new AuthTokenUnit(session.getToken());
            port.send(request);
            previousUnit = request;

            // respond
            unit = port.receive();
            if (unit instanceof EofUnit) {
                System.out.println("Server closed connection");
                port.close();
            }
            unit.accept(state);

            if (session.getRoom() != null && unit instanceof OkUnit) {
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

    public static void main(String[] args) {
        // TODO(Process-ing): Replace with real code
        String configFilepath = "client.properties";

        Properties config;
        try {
            config = ConfigUtils.loadConfig(configFilepath);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        List<String> missingKeys = ConfigUtils.getMissing(config, List.of("host", "port", "truststore-password", "truststore"));
        if (!missingKeys.isEmpty()) {
            System.err.println("Missing configuration keys: " + missingKeys);
            return;
        }

        String host = config.getProperty("host");
        InetAddress address;
        try {
            address = InetAddress.getByName(host);
        } catch (IOException e) {
            System.err.printf("Invalid host name: %s%n", host);
            return;
        }

        int port = ConfigUtils.getIntProperty(config, "port");
        if (port < 1024 || port > 65535) {
            System.err.printf("Port number must be between 1024 and 65535, port %d provided.%n", port);
            return;
        }

        String truststorePath = config.getProperty("truststore");
        String password = config.getProperty("truststore-password");

        ProtocolParser parser = new ProtocolParserImpl();
        Socket socket;
        ProtocolPort protocolPort;

        try {
            socket = SSLSocketUtils.newSocket(address, port, password, truststorePath);
            protocolPort = new SocketProtocolPort(socket, parser);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ClientState initState = new GuestState(null);
        Client client = new Client(protocolPort, initState, parser);

        client.run();
    }
}
