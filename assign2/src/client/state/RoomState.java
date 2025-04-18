package client.state;

import java.util.Optional;

import client.Client;
import protocol.unit.ProtocolUnit;
import protocol.unit.SendUnit;

public class RoomState extends ClientState {
    private final String username;
    private final String roomName;

    public RoomState(Client client, String username, String roomName) {
        super(client);

        this.username = username;
        this.roomName = roomName;
    }

    public String getUsername() {
        return username;
    }

    public String getRoomName() {
        return roomName;
    }

    @Override
    public Optional<ProtocolUnit> visit(SendUnit unit) {
        System.out.printf("%s# %s\n", unit.username(), unit.message());
        return Optional.empty();
    }
}
