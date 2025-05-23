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
import structs.security.PasswordHasher;
import structs.security.TokenManager;
import structs.storage.AuthFileStore;
import utils.ConfigUtils;
import utils.SocketUtils;

public class Server {
    private static final String CONFIG_PATH = "server.properties";
    private static final String USERS_DB_PATH = "users.db";

    private final ServerSocket serverSocket;
    private final AuthDb authDb;
    private final Map<String, Room> roomMap;
    private final ProtocolParser parser;

    public Server(ServerSocket serverSocket, AuthDb authDb, ProtocolParser parser) {
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
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                SocketUtils.configureSocket(socket);

                ProtocolPort port = new SocketProtocolPort(() -> socket, parser);
                port.connect();

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
        Properties config;
        try {
            config = ConfigUtils.loadConfig(CONFIG_PATH);
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
            serverSocket = SocketUtils.newSSLServerSocket(port, password, keystorePath);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        AuthDb authDb;
        TokenManager tokens = new TokenManager();
        PasswordHasher hasher = new PasswordHasher();

        try {
            AuthFileStore store = new AuthFileStore(Path.of(USERS_DB_PATH));
            authDb = new SyncAuthDb(store, tokens, hasher);

        } catch (IOException e) {
            System.err.println("Failed to load user DB: " + e.getMessage());
            return;
        }

        ProtocolParser parser = new ProtocolParserImpl();

        Server server = new Server(serverSocket, authDb, parser);
        System.out.printf("Server started on port %d%n", port);

        server.run();
    }
}
