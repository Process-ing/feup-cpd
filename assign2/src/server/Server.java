package server;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import protocol.ProtocolParser;
import server.room.Room;
import structs.AuthDb;

public class Server {
    private final AuthDb authDb;
    private final Map<String, Room> roomMap;
    private final ProtocolParser parser;

    public Server(AuthDb authDb, ProtocolParser parser) {
        this.authDb = authDb;
        this.parser = parser;
        this.roomMap = new HashMap<>();
    }

    public AuthDb geAuthDb() {
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
}
