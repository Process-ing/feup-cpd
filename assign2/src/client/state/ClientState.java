package client.state;

import java.util.Optional;

import protocol.DefaultProtocolVisitor;
import protocol.unit.ProtocolUnit;

public abstract class ClientState implements DefaultProtocolVisitor {
    @Override
    public Optional<ProtocolUnit> visitDefault(ProtocolUnit unit) {
        return Optional.empty();
    }
}
