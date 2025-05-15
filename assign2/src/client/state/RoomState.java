package client.state;

import java.util.Optional;

import client.Client;
import client.storage.SessionStore;
import protocol.unit.LeaveUnit;
import protocol.unit.LogoutUnit;
import protocol.unit.OkUnit;
import protocol.unit.ProtocolUnit;
import protocol.unit.RecvUnit;
import protocol.unit.SyncUnit;

public class RoomState extends ClientState {
    private final String username;
    private final String roomName;
    private int lastId;

    public RoomState(Client client, String username, String roomName) {
        super(client);

        this.username = username;
        this.roomName = roomName;
        this.lastId = -1;
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
        SessionStore session = client.getSession();
        ProtocolUnit previousUnit = client.getPreviousUnit();

        switch (previousUnit) {
            case LeaveUnit leaveUnit -> {
                System.out.println("Left room: " + roomName);
                client.setState(new AuthenticatedState(client, username));
                session.setRoom(null);
            }
            case LogoutUnit logoutUnit -> {
                System.out.println("Logged out");
                client.setState(new GuestState(client));
                session.clear();
            }
            default -> {
                // No other actions should be possible in this state
            }
        }

        session.save();
        return Optional.empty();
    }

    @Override
    public Optional<ProtocolUnit> visit(RecvUnit unit) {
        if (unit.id() == lastId + 1 || lastId == -1) {
            System.out.printf("%s# %s\n", unit.username() == username ? "You" : unit.username(), unit.message());
            lastId = unit.id();

            return Optional.empty();
        }

        if (unit.id() > lastId + 1) { // Missing messages
            return Optional.of(new SyncUnit(lastId));
        }

        // Messages already received, ignore
        return Optional.empty();
    }
}
