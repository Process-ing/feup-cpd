package client.state.confirm;

import client.Cli;
import client.state.AuthState;
import client.state.GuestState;
import client.storage.SessionStore;
import protocol.ProtocolOkIdentifier;
import protocol.unit.OkUnit;

import java.util.Optional;

import client.BaseClient;

record TokenUsername(String token, String username) {}

public class GuestConfirmer extends Confirmer<GuestState> {
    public GuestConfirmer(GuestState state) {
        super(state);
    }

    @Override
    protected void buildVisitor() {
        addVisit(ProtocolOkIdentifier.LOGIN, this::visitLogin);
        addVisit(ProtocolOkIdentifier.REGISTER, this::visitRegister);
    }

    private Optional<TokenUsername> parseTokenUsername(OkUnit confirmation) {
        Optional<String> data = confirmation.data();
        if (data.isEmpty())
            return Optional.empty();

        String[] parts = data.get().split("\n", 2);
        if (parts.length < 2)
            return Optional.empty();

        return Optional.of(new TokenUsername(parts[0], parts[1]));
    }

    private void visitLogin(OkUnit confirmation) {
        BaseClient client = getState().getClient();
        SessionStore session = client.getSession();

        Optional<TokenUsername> tokenUsername = parseTokenUsername(confirmation);
        if (tokenUsername.isEmpty())
            return;

        String token = tokenUsername.get().token();
        String username = tokenUsername.get().username();

        Cli.printResponse("Login successful: " + username);

        client.setState(new AuthState(client, username));

        session.clear();
        session.setToken(token);
        session.setUsername(username);
    }

    private void visitRegister(OkUnit confirmation) {
        BaseClient client = getState().getClient();
        SessionStore session = client.getSession();

        Optional<TokenUsername> tokenUsername = parseTokenUsername(confirmation);
        if (tokenUsername.isEmpty())
            return;

        String token = tokenUsername.get().token();
        String username = tokenUsername.get().username();

        Cli.printResponse("Registration successful: " + username);

        client.setState(new AuthState(client, username));

        session.setToken(token);
        session.setUsername(username);
    }
}
