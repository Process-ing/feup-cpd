package client.state;

import java.util.Optional;

import client.Client;
import protocol.unit.ProtocolUnit;

public abstract class NonInteractiveState extends ClientState {
    public NonInteractiveState(Client client) {
        super(client);
    }

    // This function will be called repeatedly until it returns an empty Optional.
    public abstract Optional<ProtocolUnit> buildNextUnit();
}
