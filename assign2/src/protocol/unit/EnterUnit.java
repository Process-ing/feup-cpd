package protocol.unit;

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
}
