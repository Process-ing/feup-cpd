package client.state;

import java.util.Optional;

import protocol.DefaultProtocolVisitor;
import protocol.unit.ProtocolUnit;

public interface ClientState extends DefaultProtocolVisitor {
    ClientState nextState();

    @Override
    default Optional<ProtocolUnit> visitDefault(ProtocolUnit unit) {
        return Optional.empty();
    }
}
