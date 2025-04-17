package server.client;

import java.util.Optional;

import exception.NotInRoomException;
import protocol.unit.LeaveUnit;
import protocol.unit.ProtocolUnit;
import protocol.unit.SendUnit;
import server.ClientThread;
import server.room.Room;

public class RoomUser extends Client {
    private final String name;
    private final Room room;

    public RoomUser(ClientThread thread, String name, Room room) {
        super(thread);

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
    public Optional<ProtocolUnit> visit(SendUnit unit) {
        room.addMessage(unit.message(), this);
        return Optional.empty();
    }

    @Override
    public Optional<ProtocolUnit> visit(LeaveUnit unit) {
        var newUser = room.disconnectUser(this);
        if (newUser.isEmpty())
            throw new NotInRoomException();

        getThread().setClient(newUser.get());
        return Optional.empty();
    }
}
