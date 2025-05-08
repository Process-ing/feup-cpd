package client.state;

import client.Client;
import java.util.Optional;
import protocol.unit.LeaveUnit;
import protocol.unit.LogoutUnit;
import protocol.unit.OkUnit;
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
    public Optional<ProtocolUnit> visit(OkUnit unit) {
        Client client = this.getClient();
        ProtocolUnit previousUnit = client.getPreviousUnit();

        switch (previousUnit) {
            case LeaveUnit leaveUnit -> {
                System.out.println("Left room: " + roomName);
                client.setState(new AuthenticatedState(client, username));
            }
            case LogoutUnit logoutUnit -> {
                System.out.println("Logged out");
                client.setState(new GuestState(client));
            }
            default -> {
                // No other actions should be possible in this state
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<ProtocolUnit> visit(SendUnit unit) {
        System.out.printf("%s# %s\n", unit.username(), unit.message());
        return Optional.empty();
    }
}
