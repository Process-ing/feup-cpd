package protocol.unit;

import protocol.ArgedProtocolVisitor;
import protocol.ProtocolVisitor;

public record OkUnit(String data) implements ProtocolUnit {
    @Override
    public String serialize() {
        return String.format("ok %s", data);
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
