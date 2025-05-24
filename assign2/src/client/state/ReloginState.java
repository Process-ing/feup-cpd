package client.state;

import client.Cli;

import client.BaseClient;
import client.storage.SessionStore;
import protocol.ProtocolErrorIdentifier;
import protocol.unit.ProtocolUnit;
import protocol.unit.TokenLoginUnit;

public class ReloginState extends WaitConfirmState {
    private ClientState oldState;

    public ReloginState(BaseClient client, ClientState oldState) {
        super(client);

        this.oldState = oldState;
    }

    @Override
    protected ProtocolUnit buildUnitToSend() {
        BaseClient client = getClient();
        SessionStore session = client.getSession();
        return new TokenLoginUnit(session.getToken());
    }

    @Override
    protected ClientState getStateOnConfirm() {
        BaseClient client = getClient();
        SessionStore session = client.getSession();
        ClientState newState;

        if (session.getRoom() == null) {
            newState = new AuthState(client, session.getUsername());
        } else {
            newState = new ReenterState(client, oldState);
        }

        Cli.printResponse("Login successful: " + session.getUsername());
        return newState;
    }

    @Override
    protected ClientState getStateOnError() {
        BaseClient client = getClient();
        return new GuestState(client);
    }

    @Override
    protected ProtocolErrorIdentifier getErrorIdentifier() {
        return ProtocolErrorIdentifier.LOGIN;
    }
}
