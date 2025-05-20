package client.state;

import java.util.Optional;

import client.Cli;
import client.Client;
import client.storage.SessionStore;
import protocol.ProtocolErrorIdentifier;
import protocol.unit.ErrUnit;
import protocol.unit.OkUnit;
import protocol.unit.ProtocolUnit;
import protocol.unit.TokenLoginUnit;

public class ReloginState extends NonInteractiveState {
    private boolean loginSent;

    public ReloginState(Client client) {
        super(client);

        this.loginSent = false;
    }

    @Override
    public Optional<ProtocolUnit> buildNextUnit() {
        if (loginSent)
            return Optional.empty();

        Client client = getClient();
        SessionStore session = client.getSession();
        ProtocolUnit unit = new TokenLoginUnit(session.getToken());

        loginSent = true;
        return Optional.of(unit);
    }

    @Override
    public Optional<ProtocolUnit> visit(OkUnit unit) {
        Client client = getClient();
        SessionStore session =  client.getSession();
        ClientState newState = session.getRoom() == null
                ? new AuthenticatedState(client, session.getUsername())
                : new ReenterState(client);


        client.setState(newState);
        Cli.printResponse("Login successful: " + session.getUsername());

        return Optional.empty();
    }

    @Override
    public Optional<ProtocolUnit> visit(ErrUnit unit) {
        if (unit.id() != ProtocolErrorIdentifier.LOGIN)
            return visitDefault(unit);

        Client client = getClient();
        client.setState(new GuestState(client));

        return Optional.empty();
    }

    @Override
    public Optional<ProtocolUnit> visitDefault(ProtocolUnit unit) {
        Client client = getClient();
        SessionStore session = client.getSession();
        ProtocolUnit response = new TokenLoginUnit(session.getToken());

        return Optional.of(response);
    }
}
