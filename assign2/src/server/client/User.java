package server.client;

import java.util.Optional;

import exception.RoomCreationException;
import protocol.ProtocolErrorIdentifier;
import protocol.unit.EnterUnit;
import protocol.unit.ErrUnit;
import protocol.unit.LogoutUnit;
import protocol.unit.ProtocolUnit;
import server.ClientThread;
import server.room.Room;
import server.room.RoomImpl;

public class User extends Client {
    private final String name;

    public User(ClientThread thread, String name) {
        super(thread);

        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Optional<ProtocolUnit> visit(EnterUnit unit) {
        var thread = getThread();
        var server = thread.getServer();

        var optRoom = server.getRoom(unit.roomName());
        Room room;

        if (optRoom.isPresent()) {
            room = optRoom.get();
        } else {
            room = new RoomImpl(unit.roomName());
            if (!server.addRoom(room))
                throw new RoomCreationException("Failed to assign room to server");
        }

        var newUser = room.connectUser(this);
        if (newUser.isEmpty())
            return Optional.of(new ErrUnit(ProtocolErrorIdentifier.UNAUTHORIZED));

        thread.setClient(newUser.get());
        return Optional.empty();
    }

    @Override
    public Optional<ProtocolUnit> visit(LogoutUnit unit) {
        var thread = getThread();

        thread.setClient(new Guest(thread));

        return Optional.empty();
    }
}
