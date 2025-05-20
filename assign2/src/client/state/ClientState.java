package client.state;

import client.Client;
import java.util.Optional;
import protocol.DefaultProtocolVisitor;
import protocol.unit.ProtocolUnit;

public abstract class ClientState implements DefaultProtocolVisitor {
    private Client client;

    public ClientState(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public Optional<ProtocolUnit> visitDefault(ProtocolUnit unit) {
        return Optional.empty();
    }
}
