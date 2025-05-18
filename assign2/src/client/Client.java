package client;

import client.state.ClientState;
import client.state.GuestState;
import client.state.RoomState;
import client.storage.SessionStore;
import exception.EndpointUnreachableException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;
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
import utils.ConfigUtils;
import utils.SocketUtils;

public class Client {
    private static final String SESSION_PATH_FORMAT = "session%s.properties";
    private static final String CONFIG_PATH = "client.properties";

    private final ProtocolPort port;
    private ClientState state;
    private final ProtocolParser parser;
    private ProtocolUnit previousUnit;
    private final SessionStore session;
    private boolean done = false;


    public Client(ProtocolPort port, ClientState initState, ProtocolParser parser, SessionStore session) {
        this.port = port;
        this.state = initState;
        this.state.setClient(this);
        this.parser = parser;
        this.previousUnit = null;
        this.session = session;
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

    public void run() {
        done = false;
        setState(new GuestState(this));
        restoreSession();

        Thread.ofVirtual().start(() -> {
            try {
                while (!done) {
                    try {
                        String input = Cli.getInput();
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

                        if (port.isConnected())
                            port.send(request);

                        previousUnit = request;

                    } catch (Exception e) {
                        System.out.println("Unexpected error: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.out.println("Unrecoverable error: " + e.getMessage());
            } finally {
                try {
                    port.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                done = true;
            }
        });

        try {
            while (!done) {
                ProtocolUnit unit = port.receive();
                if (unit instanceof EofUnit) {
                    port.connect();

                    ClientState oldState = state;
                    state = new GuestState(this);
                    restoreSession();
                    this.setState(oldState);

                    if (state instanceof RoomState roomState && port.isConnected())
                        port.send(roomState.getSync());

                    continue;
                }

                Optional<ProtocolUnit> response = unit.accept(state);
                if (response.isPresent()) {
                    port.send(response.get());
                }
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
        if (!session.hasSession())
            return;

        ProtocolUnit request, unit;

        try {
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


    private static Socket createSocket(InetAddress address, int port, String password, String truststorePath) {
        try {
            Socket socket = SocketUtils.newSSLSocket(address, port, password, truststorePath);
            SocketUtils.configureSocket(socket);
            System.out.println(socket.getLocalPort());

            return socket;

        } catch (IOException e) {
            return null;
        }
    }

    private static void printUsage() {
        System.out.println("Usage: java client.Client [<session-suffix>]");
    }

    public static void main(String[] args) {
        if (args.length > 1) {
            printUsage();
            return;
        }

        String sessionSuffix = args.length == 1 ? "-" + args[0] : "";
        Properties config;
        SessionStore session;

        try {
            config = ConfigUtils.loadConfig(CONFIG_PATH);
            session = new SessionStore(String.format(SESSION_PATH_FORMAT, sessionSuffix));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        List<String> missingKeys = ConfigUtils.getMissing(config,
                List.of("host", "port", "truststore-password", "truststore"));
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
        Supplier<Socket> socketFactory = () -> createSocket(address, port, password, truststorePath);
        ProtocolPort protocolPort = new SocketProtocolPort(socketFactory, parser);

        try {
            protocolPort.connect();
        } catch (IOException | EndpointUnreachableException e) {
            System.err.printf("Failed to connect to server at %s:%d: %s%n", host, port, e.getMessage());
            return;
        }

        ClientState initState = new GuestState(null);
        Client client = new Client(protocolPort, initState, parser, session);

        client.run();
    }
}
