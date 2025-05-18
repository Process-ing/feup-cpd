package client.state;

import client.Cli;
import client.Client;
import client.storage.SessionStore;
import java.util.Map;
import java.util.Optional;
import protocol.unit.EnterUnit;
import protocol.unit.LogoutUnit;
import protocol.unit.OkUnit;
import protocol.unit.ProtocolUnit;

public class AuthenticatedState extends ClientState {
    private final String username;

    public AuthenticatedState(Client client, String username) {
        super(client);

        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public Map<String, String> getAvailableCommands() {
        return Map.of(
            "help", "/help : Show available commands",
            "info", "/info : Show information about session",
            "enter", "/enter <room> : Enter a room",
            "logout", "/logout : Logout from current account"
        );
    }

    @Override
    public Optional<ProtocolUnit> visit(OkUnit unit) {
        Client client = this.getClient();
        SessionStore session = client.getSession();
        ProtocolUnit previousUnit = client.getPreviousUnit();

        switch (previousUnit) {
            case EnterUnit enterUnit -> {
                Cli.printResponse("Entered room: " + enterUnit.roomName(), true);
                client.setState(new RoomState(client, username, enterUnit.roomName()));
                session.setRoom(enterUnit.roomName());
            }
            case LogoutUnit logoutUnit -> {
                Cli.printResponse("Logged out: " + username, false);
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
}
