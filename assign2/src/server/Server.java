package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import protocol.ProtocolParser;
import protocol.ProtocolParserImpl;
import protocol.ProtocolPort;
import protocol.SocketProtocolPort;
import server.client.Guest;
import server.room.Room;
import structs.AuthDb;
import structs.MessageQueue;
import structs.SyncAuthDb;
import structs.SyncMessageQueue;
import structs.security.TokenManager;
import utils.ConfigUtils;
import utils.SSLSocketUtils;

public class Server {

    private final ServerSocket serverSocket;
    private final AuthDb authDb;
    private final Map<String, Room> roomMap;
    private final ProtocolParser parser;

    public Server(ServerSocket serverSocket, AuthDb authDb, TokenManager tokens, ProtocolParser parser) {
        this.serverSocket = serverSocket;
        this.authDb = authDb;
        this.parser = parser;
        this.roomMap = new HashMap<>();
    }

    public AuthDb getAuthDb() {
        return authDb;
    }

    public ProtocolParser getParser() {
        return parser;
    }

    public Optional<Room> getRoom(String roomName) {
        return Optional.ofNullable(roomMap.get(roomName));
    }

    public boolean addRoom(Room room) {
        return roomMap.putIfAbsent(room.getName(), room) == null;
    }

    public void run() {
        // TODO(Process-ing): Convert to real code

        // Room room = new RoomImpl("Lobby");
        // addRoom(room);

        try {
            while (true) {
                Socket socket = serverSocket.accept();

                ProtocolPort port = new SocketProtocolPort(socket, parser);
                MessageQueue queue = new SyncMessageQueue();
                ClientThread clientThread = new ClientThread(this, port, queue, null);

                //RoomUser user = room.connectUser(new User(clientThread, "JohnDoe" + new Random().nextInt())).get();
                Guest user = new Guest(clientThread);
                clientThread.setClient(user);

                clientThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Path usersDBPath = Path.of(System.getProperty("user.dir"),
                         "..", "data", "db.cpd").toAbsolutePath();
        String configFilepath = "server.properties";

        Properties config;
        try {
            config = ConfigUtils.loadConfig(configFilepath);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        List<String> missingKeys = ConfigUtils.getMissing(config, List.of("port", "keystore", "keystore-password"));
        if (!missingKeys.isEmpty()) {
            System.err.println("Missing configuration keys: " + missingKeys);
            return;
        }

        int port = ConfigUtils.getIntProperty(config, "port");
        if (port < 1024 || port > 65535) {
            System.err.printf("Port number must be between 1024 and 65535, port %d provided.%n", port);
            return;
        }

        String keystorePath = config.getProperty("keystore");
        char[] password = config.getProperty("keystore-password").toCharArray();

        ServerSocket serverSocket;
        try {
            serverSocket = SSLSocketUtils.newServerSocket(port, password, keystorePath);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        AuthDb authDb;
        try {
            authDb = new SyncAuthDb(usersDBPath);
        } catch (IOException e) {
            System.err.println("Failed to load user DB: " + e.getMessage());
            return;
        }

        ProtocolParser parser = new ProtocolParserImpl();
        TokenManager tokens = new TokenManager();
        Server server = new Server(serverSocket, authDb, tokens, parser);

        server.run();
    }
}
