package protocol;

import java.util.Optional;

import protocol.unit.ProtocolUnit;

public interface ProtocolHandler {
    Optional<ProtocolUnit> handle(ProtocolUnit unit);
}
