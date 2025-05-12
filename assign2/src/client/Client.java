package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
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
import utils.ConfigUtils;
import utils.SSLSocketUtils;

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

        ClientState initState = new RoomState(null, "JohnDoe", "Lobby");
        Client client = new Client(protocolPort, initState);

        client.run();
    }
}