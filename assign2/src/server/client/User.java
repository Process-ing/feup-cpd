package server.client;

import exception.RoomCreationException;

import java.util.List;
import java.util.Optional;
import protocol.ProtocolErrorIdentifier;
import protocol.ProtocolUtils;
import protocol.unit.EnterUnit;
import protocol.unit.ErrUnit;
import protocol.unit.ListRoomsUnit;
import protocol.unit.LogoutUnit;
import protocol.unit.OkUnit;
import protocol.unit.ProtocolUnit;
import server.ClientThread;
import server.Server;
import server.room.Room;
import server.room.RoomImpl;

public class User extends Client {
    private final String name;
    private final String token;

    public User(ClientThread thread, String name, String token) {
        super(thread);

        this.name = name;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }

    @Override
    public Optional<ProtocolUnit> visit(ListRoomsUnit unit) {
        ClientThread thread = getThread();
        Server server = thread.getServer();

        List<Room> rooms = server.getRooms();
        List<String> roomNames = rooms.stream()
                .map(Room::getName)
                .toList();
        String data = ProtocolUtils.escapeToken(String.join(",", roomNames));

        return Optional.of(new OkUnit(data));
    }

    @Override
    public Optional<ProtocolUnit> visit(EnterUnit unit) {
        ClientThread thread = getThread();
        Server server = thread.getServer();

        Optional<Room> optRoom = server.getRoom(unit.roomName());
        Room room;

        if (optRoom.isPresent()) {
            room = optRoom.get();
        } else {
            room = new RoomImpl(unit.roomName());
            if (!server.addRoom(room))
                throw new RoomCreationException("Failed to assign room to server");
        }

        Optional<RoomUser> newUser = room.connectUser(this);
        if (newUser.isEmpty())
            return Optional.of(new ErrUnit(ProtocolErrorIdentifier.UNAUTHORIZED));

        thread.setClient(newUser.get());
        return Optional.of(new OkUnit(server.isRoomAi(unit.roomName()) ? "ai" : "room"));
    }

    @Override
    public Optional<ProtocolUnit> visit(LogoutUnit unit) {
        ClientThread thread = getThread();

        thread.setClient(new Guest(thread));

        return Optional.of(new OkUnit(null));
    }

    @Override
    public void cleanup() {
        // No cleanup needed for user
    }

    @Override
    public String toString() {
        return name;
    }
}
