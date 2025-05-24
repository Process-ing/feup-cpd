package client.state;

import client.BaseClient;
import client.Cli;
import client.storage.SessionStore;
import protocol.ProtocolErrorIdentifier;
import protocol.unit.ErrUnit;
import protocol.unit.ProtocolUnit;
import protocol.unit.RegisterUnit;

public class BotRegisterState extends WaitConfirmState {
    private final String password;
    private final ClientState targetState;

    public BotRegisterState(BaseClient client, String password, ClientState targetState) {
        super(client);

        this.password = password;
        this.targetState = targetState;
    }

    @Override
    protected ProtocolUnit buildUnitToSend() {
        BaseClient client = getClient();
        SessionStore session = client.getSession();

        return new RegisterUnit(session.getUsername(), password);
    }

    @Override
    protected ClientState getStateOnConfirm() {
        BaseClient client = getClient();
        SessionStore session = client.getSession();
        ClientState newState = session.getRoom() == null
                ? targetState
                : new BotAuthState(client, targetState);

        Cli.printResponse("Register successful: " + session.getUsername());
        return newState;
    }

    @Override
    protected boolean handleError(ErrUnit unit) {
        if (unit.id() != ProtocolErrorIdentifier.REGISTER)
            return false;

        String message = String.format("Bot failed to login with username '%s' and password '%s'",
            getClient().getSession().getUsername(), password);
        throw new IllegalStateException(message);
    }
}
