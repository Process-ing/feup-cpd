package protocol.unit;

import protocol.ArgedProtocolVisitor;
import protocol.ProtocolErrorIdentifier;
import protocol.ProtocolVisitor;

public record ErrUnit(ProtocolErrorIdentifier id) implements ProtocolUnit {
    @Override
    public String serialize() {
        return String.format("err %s", id.getName());
    }

    @Override
    public <T> T accept(ProtocolVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <R, A> R accept(ArgedProtocolVisitor<R, A> visitor, A arg) {
        return visitor.visit(this, arg);
    }
}
