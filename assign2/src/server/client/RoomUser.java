package server.client;

import java.util.Optional;

import protocol.unit.ProtocolUnit;
import server.room.Room;

public class RoomUser implements Client {
    private final String name;
    private final Room room;

    public RoomUser(String name, Room room) {
        this.name = name;
        this.room = room;
    }

    public String getName() {
        return name;
    }

    public Room getRoom() {
        return room;
    }

    @Override
    public Optional<ProtocolUnit> handle(ProtocolUnit unit) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handle'");
    }

}
