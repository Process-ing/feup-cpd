package client.state;

import client.Cli;
import client.Client;
import client.state.confirm.AuthConfirmer;
import client.storage.SessionStore;
import java.util.Map;
import java.util.Optional;

import protocol.ProtocolParser;
import protocol.unit.OkUnit;
import protocol.unit.ProtocolUnit;

public class AuthenticatedState extends InteractiveClientState {
    private final String username;
    private final AuthConfirmer confirmer;

    public AuthenticatedState(Client client, String username) {
        super(client);

        this.username = username;
        this.confirmer = new AuthConfirmer(this);
    }

    public String getUsername() {
        return username;
    }

    @Override
    public Map<String, String> getAvailableCommands() {
        return Map.of(
            "/help", "/help : Show available commands",
            "/info", "/info : Show information about session",
            "/enter", "/enter <room> : Enter a room",
            "/logout", "/logout : Logout from current account"
        );
    }

    @Override
    public ProtocolUnit buildResponse(String input) {
        ProtocolParser parser = getClient().getParser();
        return parser.parse(input.substring(1));
    }

    @Override
    public String getInfo() {
        return String.format("Logged in as '%s'.", username);
    }

    @Override
    public Optional<ProtocolUnit> visit(OkUnit unit) {
        Client client = this.getClient();
        SessionStore session = client.getSession();
        ProtocolUnit previousUnit = client.getPreviousUnit();

        previousUnit.accept(confirmer, unit);

        try {
            session.save();
        } catch (Exception e) {
            Cli.printError("Failed to save session: " + e.getMessage());
        }

        return Optional.empty();
    }
}
