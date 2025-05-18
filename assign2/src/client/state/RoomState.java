package client.state;

import client.Cli;
import client.Client;
import client.storage.SessionStore;
import java.util.Optional;
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
                Cli.printResponse("Left room: " + roomName);
                client.setState(new AuthenticatedState(client, username));
                session.setRoom(null);
            }
            case LogoutUnit logoutUnit -> {
                Cli.printResponse("Logged out: " + username);
                client.setState(new GuestState(client));
                session.clear();
            }
            default -> {
                // No other actions should be possible in this state
            }
        }

        try {
            session.save();
        } catch (Exception e) {
            Cli.printError("Failed to save session: " + e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public Optional<ProtocolUnit> visit(RecvUnit unit) {
        if (unit.id() == lastId + 1 || lastId == -1) {
            Cli.printMessage(unit.username(), unit.message(), unit.username().equals(username));
            lastId = unit.id();

            return Optional.empty();
        }

        if (unit.id() > lastId + 1) { // Missing messages
            return Optional.of(getSync());
        }

        // Messages already received, ignore
        return Optional.empty();
    }

    public ProtocolUnit getSync() {
        return new SyncUnit(lastId);
    }
}
