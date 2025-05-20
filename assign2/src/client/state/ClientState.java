package client.state;

import client.BaseClient;
import java.util.Optional;
import protocol.DefaultProtocolVisitor;
import protocol.unit.ProtocolUnit;

public abstract class ClientState implements DefaultProtocolVisitor<Optional<ProtocolUnit>> {
    private BaseClient client;

    public ClientState(BaseClient client) {
        this.client = client;
    }

    public BaseClient getClient() {
        return client;
    }

    public void setClient(BaseClient client) {
        this.client = client;
    }

    @Override
    public Optional<ProtocolUnit> visitDefault(ProtocolUnit unit) {
        return Optional.empty();
    }
}
