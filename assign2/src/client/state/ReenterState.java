package client.state;

import client.Cli;
import client.BaseClient;
import client.storage.SessionStore;
import protocol.ProtocolErrorIdentifier;
import protocol.unit.EnterUnit;
import protocol.unit.ProtocolUnit;

public class ReenterState extends WaitConfirmState {
    private ClientState oldState;

    public ReenterState(BaseClient client, ClientState oldState) {
        super(client);

        this.oldState = oldState;
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
        SessionStore session = client.getSession();
        int lastId = oldState instanceof SynchronizableState syncState ? syncState.getSyncId() : -1;
        ClientState newState = new RoomState(client, session.getUsername(), session.getRoom(), lastId);

        Cli.printResponse("Entered room: " + session.getRoom());
        return newState;
    }

    @Override
    protected ClientState getStateOnError() {
        BaseClient client = getClient();
        SessionStore session = client.getSession();
        return new AuthState(client, session.getUsername());
    }

    @Override
    protected ProtocolErrorIdentifier getErrorIdentifier() {
        return ProtocolErrorIdentifier.UNAUTHORIZED;
    }
}
