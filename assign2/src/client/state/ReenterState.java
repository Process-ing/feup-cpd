package client.state;

import client.Cli;
import client.BaseClient;
import client.storage.SessionStore;
import protocol.ProtocolErrorIdentifier;
import protocol.unit.EnterUnit;
import protocol.unit.ProtocolUnit;

public class ReenterState extends WaitConfirmState {
    public ReenterState(BaseClient client) {
        super(client);
    }

    @Override
    protected ProtocolUnit buildUnitToSend() {
        BaseClient client = getClient();
        SessionStore session = client.getSession();
        return new EnterUnit(session.getRoom());
    }

    @Override
    protected ClientState getStateOnConfirm() {
        BaseClient client = getClient();
        SessionStore session =  client.getSession();
        ClientState newState = new RoomState(client, session.getUsername(), session.getRoom());

        Cli.printResponse("Entered room: " + session.getRoom());
        return newState;
    }

    @Override
    protected ClientState getStateOnError() {
        BaseClient client = getClient();
        SessionStore session = client.getSession();
        return new AuthenticatedState(client, session.getUsername());
    }

    @Override
    protected ProtocolErrorIdentifier getErrorIdentifier() {
        return ProtocolErrorIdentifier.UNAUTHORIZED;
    }
}
