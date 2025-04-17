package server;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import server.room.Room;
import structs.AuthDb;

public class Server {
    private final AuthDb authDb;
    private final Map<String, Room> roomMap;

    public Server(AuthDb authDb) {
        this.authDb = authDb;
        this.roomMap = new HashMap<>();
    }

    public AuthDb geAuthDb() {
        return authDb;
    }

    public Optional<Room> getRoom(String roomName) {
        return Optional.ofNullable(roomMap.get(roomName));
    }

    public boolean addRoom(Room room) {
        return roomMap.putIfAbsent(room.getName(), room) == null;
    }
}
