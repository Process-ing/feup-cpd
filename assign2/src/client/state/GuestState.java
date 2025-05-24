package client.state;

import client.Cli;
import client.BaseClient;
import client.state.confirm.GuestConfirmer;
import client.storage.SessionStore;
import java.util.Map;
import java.util.Optional;
import protocol.ProtocolErrorIdentifier;
import protocol.ProtocolParser;
import protocol.unit.ErrUnit;
import protocol.unit.OkUnit;
import protocol.unit.ProtocolUnit;

public class GuestState extends InteractiveState {
    private final GuestConfirmer confirmer;

    public GuestState(BaseClient client) {
        super(client);

        this.confirmer = new GuestConfirmer(this);
    }

    @Override
    public Map<String, String> getAvailableCommands() {
        return Map.of(
                "/help", "/help : Show available commands",
                "/info", "/info : Show information about session",
                "/register", "/register <username> <password> : Register new account",
                "/login", "/login <username> <password> : Login with account");
    }

    @Override
    public String getInfo() {
        return "Not logged in.";
    }

    @Override
    public ProtocolUnit buildResponse(String input) {
        ProtocolParser parser = getClient().getParser();
        return parser.parse(input.substring(1));
    }

    @Override
    public Optional<ProtocolUnit> visit(OkUnit unit) {
        BaseClient client = this.getClient();
        SessionStore session = client.getSession();
        ProtocolUnit previousUnit = client.getPreviousUnit();

        confirmer.visit(unit);

        try {
            session.save();
        } catch (Exception e) {
            Cli.printError("Failed to save session: " + e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public Optional<ProtocolUnit> visit(ErrUnit unit) {
        // TODO: isto se calhar vai ser alterado pelo CLI
        if (!unit.id().equals(ProtocolErrorIdentifier.LOGIN))
            Cli.printError("Authentication error: " + unit.id());
        return Optional.empty();
    }
}
