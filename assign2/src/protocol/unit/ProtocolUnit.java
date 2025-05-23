package protocol.unit;

import protocol.ArgedProtocolVisitor;
import protocol.ProtocolVisitor;

public interface ProtocolUnit {
    String serialize();

    <T> T accept(ProtocolVisitor<T> visitor);
    <R, A> R accept(ArgedProtocolVisitor<R, A> visitor, A arg);
}
