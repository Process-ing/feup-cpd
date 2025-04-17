package server.client;

import java.util.Optional;

import exception.NotInRoomException;
import protocol.ProtocolErrorIdentifier;
import protocol.unit.ErrUnit;
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

    public Optional<ProtocolUnit> handle(SendUnit unit) {
        room.addMessage(unit.message(), this);
        return Optional.empty();
    }

    public Optional<ProtocolUnit> handle(LeaveUnit unit) {
        var newUser = room.disconnectUser(this);
        if (newUser.isEmpty())
            throw new NotInRoomException();

        getThread().setClient(newUser.get());
        return Optional.empty();
    }

    @Override
    public Optional<ProtocolUnit> handle(ProtocolUnit unit) {
        return Optional.of(new ErrUnit(ProtocolErrorIdentifier.UNEXPECTED));
    }
}
