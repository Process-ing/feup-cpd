package client.state.confirm;

import client.Cli;
import client.state.AuthenticatedState;
import client.state.GuestState;
import client.storage.SessionStore;
import protocol.unit.LoginUnit;
import protocol.unit.OkUnit;
import protocol.unit.RegisterUnit;
import protocol.unit.TokenLoginUnit;
import client.Client;

public class GuestConfirmer extends Confirmer<GuestState> {
    public GuestConfirmer(GuestState state) {
        super(state);
    }

    public Void visit(LoginUnit unit, OkUnit confirmation) {
        Client client = getState().getClient();
        SessionStore session = client.getSession();

        Cli.printResponse("Login successful: " + unit.user());

        client.setState(new AuthenticatedState(client, unit.user()));

        String token = confirmation.data();
        session.setToken(token);
        session.setUsername(unit.user());

        return null;
    }

    public Void visit(RegisterUnit unit, OkUnit confirmation) {
        Client client = getState().getClient();
        SessionStore session = client.getSession();

        Cli.printResponse("Registration successful: " + unit.user());

        client.setState(new AuthenticatedState(client, unit.user()));


        String token = confirmation.data();
        session.setToken(token);
        session.setUsername(unit.user());

        return null;
    }

    public Void visit(TokenLoginUnit unit, OkUnit confirmation) {
        Client client = getState().getClient();
        SessionStore session = client.getSession();

        Cli.printResponse("Login successful: " + session.getUsername());

        client.setState(new AuthenticatedState(client, session.getUsername()));

        String token = confirmation.data();
        session.setToken(token);

        return null;
    }
}
