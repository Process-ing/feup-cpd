package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import protocol.ProtocolParser;
import protocol.ProtocolParserImpl;
import server.client.RoomUser;
import server.room.Room;
import server.room.RoomImpl;
import structs.AuthDb;
import structs.SyncAuthDb;

public class Server {
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
        // TODO(Process-ing): Convert to real code
        Room room = new RoomImpl("Lobby");
        addRoom(room);

        try {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientThread clientThread = new ClientThread(socket, null, null);

                RoomUser user = new RoomUser(clientThread, "Guest", room);
                clientThread.setClient(user);

                Thread.ofVirtual().start(clientThread::run);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = 12345; // TODO(Process-ing): Get from args

        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Error creating server socket: " + e.getMessage());
            return;
        }

        AuthDb authDb = new SyncAuthDb();
        ProtocolParser parser = new ProtocolParserImpl();
        Server server = new Server(serverSocket, authDb, parser);

        server.run();
    }
}
