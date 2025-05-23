package protocol.unit;

import protocol.ArgedProtocolVisitor;
import protocol.ProtocolUtils;
import protocol.ProtocolVisitor;

public record EnterUnit(String roomName) implements ProtocolUnit {
    @Override
    public String serialize() {
        return String.format("enter %s", ProtocolUtils.escapeToken(roomName));
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
