package server;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import protocol.ProtocolParser;
import protocol.ProtocolParserImpl;
import server.room.Room;
import server.room.RoomImpl;
import structs.AuthDb;
import structs.SyncAuthDb;

public class Server {
    private final AuthDb authDb;
    private final Map<String, Room> roomMap;
    private final ProtocolParser parser;

    public Server(AuthDb authDb, ProtocolParser parser) {
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

    public static void main(String[] args) {
        // TODO(Process-ing): Convert to real code
        // Example usage
        AuthDb authDb = new SyncAuthDb();
        ProtocolParser parser = new ProtocolParserImpl();
        Server server = new Server(authDb, parser);

        // Add a room
        Room room = new RoomImpl("Lobby");
        server.addRoom(room);

        
    }
}
