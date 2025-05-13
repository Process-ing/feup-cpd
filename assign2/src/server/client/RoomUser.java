package server.client;

import exception.NotInRoomException;
import java.util.Optional;
import protocol.unit.LeaveUnit;
import protocol.unit.LogoutUnit;
import protocol.unit.OkUnit;
import protocol.unit.ProtocolUnit;
import protocol.unit.SendUnit;
import server.ClientThread;
import server.room.Room;
import structs.Message;
import structs.MessageQueue;

public class RoomUser extends Client {
    private final String name;
    private final Room room;
    private final String token;

    public RoomUser(ClientThread thread, String name, Room room, String token) {
        super(thread);

        this.name = name;
        this.room = room;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public Room getRoom() {
        return room;
    }

    public String getToken() {
        return token;
    }

    @Override
    public Optional<ProtocolUnit> visit(SendUnit unit) {
        Message message = room.addMessage(unit.message(), this);

        for (RoomUser user: room.getOnlineUsers()) {
            if (!name.equals(user.name)) {
                MessageQueue userQueue = user.getThread().getMessageQueue();
                userQueue.push(message);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ProtocolUnit> visit(LeaveUnit unit) {
        Optional<User> newUser = room.disconnectUser(this);
        if (newUser.isEmpty())
            throw new NotInRoomException();

        getThread().setClient(newUser.get());
        return Optional.of(new OkUnit("success"));
    }

    @Override
    public Optional<ProtocolUnit> visit(LogoutUnit unit) {
        Optional<User> newUser = room.disconnectUser(this);
        if (newUser.isEmpty())
            throw new NotInRoomException();
        
        getThread().setClient(new Guest(getThread()));
        return Optional.of(new OkUnit("success"));
    }

    @Override
    public void cleanup() {
        room.disconnectUser(this);
    }
}
