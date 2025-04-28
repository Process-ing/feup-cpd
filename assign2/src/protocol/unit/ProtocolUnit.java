package protocol.unit;

import java.util.Optional;

import protocol.ProtocolVisitor;

public interface ProtocolUnit {
    String serialize();
    
    Optional<ProtocolUnit> accept(ProtocolVisitor visitor);
}
