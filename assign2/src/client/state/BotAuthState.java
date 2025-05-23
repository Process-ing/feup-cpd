package client.state;

import client.BaseClient;
import client.Cli;
import client.storage.SessionStore;
import protocol.ProtocolErrorIdentifier;
import protocol.unit.EnterUnit;
import protocol.unit.ProtocolUnit;

public class BotAuthState extends WaitConfirmState {
    private final ClientState targetState;

    public BotAuthState(BaseClient client, ClientState targetState) {
        super(client);

        this.targetState = targetState;
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

        Cli.printResponse("Entered room: " + session.getRoom());
        return targetState;
    }

    @Override
    protected ClientState getStateOnError() {
        String message = String.format("Bot failed to enter room '%s'",
            getClient().getSession().getRoom());
        throw new IllegalStateException(message);
    }

    @Override
    protected ProtocolErrorIdentifier getErrorIdentifier() {
        return ProtocolErrorIdentifier.UNAUTHORIZED;
    }
}
