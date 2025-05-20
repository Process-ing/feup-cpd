package client.state;

import java.util.Optional;

import client.Cli;
import client.Client;
import client.storage.SessionStore;
import protocol.ProtocolErrorIdentifier;
import protocol.unit.EnterUnit;
import protocol.unit.ErrUnit;
import protocol.unit.OkUnit;
import protocol.unit.ProtocolUnit;

public class ReenterState extends NonInteractiveState {
    private boolean enterSent;

    public ReenterState(Client client) {
        super(client);

        this.enterSent = false;
    }

    @Override
    public Optional<ProtocolUnit> buildNextUnit() {
        if (enterSent)
            return Optional.empty();

        Client client = getClient();
        SessionStore session = client.getSession();
        ProtocolUnit unit = new EnterUnit(session.getRoom());

        enterSent = true;
        return Optional.of(unit);
    }

    @Override
    public Optional<ProtocolUnit> visit(OkUnit unit) {
        Client client = getClient();
        SessionStore session = client.getSession();

        client.setState(new RoomState(client, session.getUsername(), session.getRoom()));
        Cli.printResponse("Entered room: " + session.getRoom());

        return Optional.empty();
    }

    @Override
    public Optional<ProtocolUnit> visit(ErrUnit unit) {
        if (unit.id() != ProtocolErrorIdentifier.UNAUTHORIZED)
            return visitDefault(unit);

        Client client = getClient();
        SessionStore session = client.getSession();
        String username = session.getUsername();

        client.setState(new AuthenticatedState(client, username));

        return Optional.empty();
    }

    @Override
    public Optional<ProtocolUnit> visitDefault(ProtocolUnit unit) {
        Client client = getClient();
        SessionStore session = client.getSession();
        ProtocolUnit response = new EnterUnit(session.getRoom());

        return Optional.of(response);
    }
}
