package protocol.unit;

import java.util.Optional;

import protocol.ProtocolUtils;
import protocol.ProtocolVisitor;

public record EnterUnit(String roomName) implements ProtocolUnit {
    @Override
    public String serialize() {
        return String.format("enter %s", ProtocolUtils.escapeToken(roomName));
    }

    @Override
    public Optional<ProtocolUnit> accept(ProtocolVisitor visitor) {
        return visitor.visit(this);
    }
}
