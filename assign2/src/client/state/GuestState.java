package client.state;

import client.Cli;
import client.Client;
import client.storage.SessionStore;
import java.util.Optional;
import protocol.ProtocolErrorIdentifier;
import protocol.unit.AuthTokenUnit;
import protocol.unit.ErrUnit;
import protocol.unit.LoginUnit;
import protocol.unit.OkUnit;
import protocol.unit.ProtocolUnit;
import protocol.unit.RegisterUnit;

public class GuestState extends ClientState {
    public GuestState(Client client) {
        super(client);
    }

    @Override
    public Optional<ProtocolUnit> visit(OkUnit unit) {
        Client client = this.getClient();
        SessionStore session = client.getSession();
        String token = unit.data();
        ProtocolUnit previousUnit = client.getPreviousUnit();

        switch (previousUnit) {
            case LoginUnit loginUnit -> {
                Cli.printResponse("Login successful: " + loginUnit.user(), true);
                client.setState(new AuthenticatedState(client, loginUnit.user()));
                session.setToken(token);
                session.setUsername(loginUnit.user());
            }
            case RegisterUnit registerUnit -> {
                Cli.printResponse("Registration successful: " + registerUnit.user(), true);
                client.setState(new AuthenticatedState(client, registerUnit.user()));
                session.setToken(token);
                session.setUsername(registerUnit.user());
            }
            case AuthTokenUnit authTokenUnit -> {
                Cli.printResponse("Login successful: " + session.getUsername(), true);
                client.setState(new AuthenticatedState(client, session.getUsername()));
                session.setToken(token);
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
    public Optional<ProtocolUnit> visit(ErrUnit unit) {
        // TODO: isto se calhar vai ser alterado pelo CLI
        if (!unit.id().equals(ProtocolErrorIdentifier.LOGIN))
            Cli.printError("Authentication error: " + unit.id());
        return Optional.empty();
    }
}
