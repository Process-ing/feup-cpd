package protocol.unit;

import protocol.ArgedProtocolVisitor;
import protocol.ProtocolVisitor;

public record InvalidUnit() implements ProtocolUnit {
    @Override
    public String serialize() {
        return "INVALID";
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
