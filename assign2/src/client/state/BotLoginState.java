package client.state;

import client.BaseClient;
import client.Cli;
import client.storage.SessionStore;
import protocol.ProtocolErrorIdentifier;
import protocol.unit.ErrUnit;
import protocol.unit.LoginUnit;
import protocol.unit.ProtocolUnit;

public class BotLoginState extends WaitConfirmState {
    private final String password;
    private final ClientState targetState;

    public BotLoginState(BaseClient client, String password, ClientState targetState) {
        super(client);

        this.password = password;
        this.targetState = targetState;
    }

    @Override
    protected ProtocolUnit buildUnitToSend() {
        BaseClient client = getClient();
        SessionStore session = client.getSession();
        return new LoginUnit(session.getUsername(), password);
    }

    @Override
    protected ClientState getStateOnConfirm() {
        BaseClient client = getClient();
        SessionStore session = client.getSession();
        ClientState newState = session.getRoom() == null
                ? targetState
                : new BotAuthState(client, targetState);

        Cli.printResponse("Login successful: " + session.getUsername());
        return newState;
    }

    @Override
    protected boolean handleError(ErrUnit unit) {
        if (unit.id() != ProtocolErrorIdentifier.LOGIN) {
            BaseClient client = getClient();
            client.setState(new BotRegisterState(client, password, targetState));
            return true;
        }

        return false;
    }
}
