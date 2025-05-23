package client.bot;

import client.BaseClient;
import client.state.BotLoginState;
import client.state.BotState;
import client.state.ClientState;
import client.storage.SessionStore;
import protocol.ProtocolParser;
import protocol.ProtocolPort;

public abstract class BaseBot extends BaseClient {
    private final String password;

    public BaseBot(ProtocolPort protocolPort, ProtocolParser parser, SessionStore session, String password) {
        super(protocolPort, parser, session);

        this.password = password;
    }

    @Override
    protected ClientState getInitialState() {
        int syncId = getState() instanceof BotState botState
                ? botState.getSyncId()
                : -1;
        ClientState targetState = getTargetState(syncId);
        return new BotLoginState(this, password, targetState);
    }

    protected abstract ClientState getTargetState(int syncId);
}
