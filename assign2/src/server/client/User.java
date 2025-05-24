package server.client;

import exception.RoomCreationException;

import java.util.List;
import java.util.Optional;
import protocol.ProtocolErrorIdentifier;
import protocol.ProtocolOkIdentifier;
import protocol.ProtocolUtils;
import protocol.unit.EnterUnit;
import protocol.unit.ErrUnit;
import protocol.unit.ListRoomsUnit;
import protocol.unit.LogoutUnit;
import protocol.unit.OkUnit;
import protocol.unit.ProtocolUnit;
import server.ClientThread;
import server.Server;
import server.room.AiRoom;
import server.room.Room;
import server.room.RoomImpl;
import structs.AuthDb;

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

        List<String> aiRoomNames = rooms.stream()
                .filter(room -> server.isRoomAi(room.getName()))
                .map(Room::getName)
                .toList();

        List<String> roomNames = rooms.stream()
                .filter(room -> !server.isRoomAi(room.getName()))
                .map(Room::getName)
                .toList();

        String data = ProtocolUtils.escapeToken(String.join("\n", roomNames) + "\n\n" + String.join("\n", aiRoomNames));

        return Optional.of(new OkUnit(ProtocolOkIdentifier.LIST_ROOMS, data));
    }

    @Override
    public Optional<ProtocolUnit> visit(EnterUnit unit) {
        ClientThread thread = getThread();
        Server server = thread.getServer();

        Optional<Room> optRoom = server.getRoom(unit.roomName());
        Room room;
        ProtocolOkIdentifier responseId;

        if (optRoom.isPresent()) {
            room = optRoom.get();
            responseId = ProtocolOkIdentifier.ENTER_ROOM;
        } else {
            room = new RoomImpl(unit.roomName());
            if (!server.addRoom(room))
                throw new RoomCreationException("Failed to assign room to server");
            responseId = ProtocolOkIdentifier.CREATE_ROOM;
        }

        Optional<RoomUser> newUser = room.connectUser(this);
        if (newUser.isEmpty())
            return Optional.of(new ErrUnit(ProtocolErrorIdentifier.UNAUTHORIZED));

        thread.setClient(newUser.get());

        String roomName = room.getName();
        boolean aiRoom = room instanceof AiRoom;
        String info = ProtocolUtils.escapeToken(roomName + "\n" + (aiRoom ? "ai" : "normal"));

        return Optional.of(new OkUnit(responseId, info));
    }

    @Override
    public Optional<ProtocolUnit> visit(LogoutUnit unit) {
        ClientThread thread = getThread();

        thread.setClient(new Guest(thread));

        AuthDb authDb = getThread().getServer().getAuthDb();
        authDb.logout(token);

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
