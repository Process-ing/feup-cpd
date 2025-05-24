package client.state;

import client.Cli;
import client.BaseClient;
import client.state.confirm.RoomConfirmer;
import client.storage.SessionStore;
import java.util.Map;
import java.util.Optional;

import protocol.ProtocolParser;
import protocol.unit.OkUnit;
import protocol.unit.ProtocolUnit;
import protocol.unit.RecvUnit;
import protocol.unit.SendUnit;
import protocol.unit.SyncUnit;

public class RoomState extends InteractiveState implements SynchronizableState {
    private final RoomConfirmer confirmer;
    private final String username;
    private final String roomName;
    private int lastId;

    public RoomState(BaseClient client, String username, String roomName) {
        this(client, username, roomName, -1);
    }

    public RoomState(BaseClient client, String username, String roomName, int lastId) {
        super(client);

        this.confirmer = new RoomConfirmer(this);
        this.username = username;
        this.roomName = roomName;
        this.lastId = lastId;
    }

    public String getUsername() {
        return username;
    }

    public String getRoomName() {
        return roomName;
    }

    @Override
    public Map<String, String> getAvailableCommands() {
        return Map.of(
                "/help", "/help : Show available commands",
                "/info", "/info : Show information about session",
                "/leave", "/leave : Leave the current room",
                "/logout", "/logout : Logout from current account",
                "", "<message> : Send a message to the room");
    }

    @Override
    public String getInfo() {
        return String.format("Logged in as '%s' in room '%s'.", username, roomName);
    }

    @Override
    public ProtocolUnit buildResponse(String input) {
        if (!input.startsWith("/"))
            return new SendUnit(input);

        ProtocolParser parser = getClient().getParser();
        return parser.parse(input.substring(1));
    }

    @Override
    public Optional<ProtocolUnit> visit(OkUnit unit) {
        BaseClient client = this.getClient();
        SessionStore session = client.getSession();

        confirmer.visit(unit);

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
            return Optional.of(getSyncUnit());
        }

        // Messages already received, ignore
        return Optional.empty();
    }

    @Override
    public int getSyncId() {
        return lastId;
    }

    @Override
    public void setSyncId(int syncId) {
        this.lastId = syncId;
    }

    public SyncUnit getSyncUnit() {
        return new SyncUnit(lastId);
    }
}
