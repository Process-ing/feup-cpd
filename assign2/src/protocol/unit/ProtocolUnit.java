package protocol.unit;

import protocol.ProtocolVisitor;

public interface ProtocolUnit {
    String serialize();

    <T> T accept(ProtocolVisitor<T> visitor);
}
