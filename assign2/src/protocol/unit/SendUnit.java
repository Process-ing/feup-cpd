package protocol.unit;

import java.util.Optional;

import protocol.ProtocolUtils;
import protocol.ProtocolVisitor;

public record SendUnit(String message) implements ProtocolUnit {
    @Override
    public String serialize() {
        return String.format("send %s", ProtocolUtils.escapeToken(message));
    }

    @Override
    public Optional<ProtocolUnit> accept(ProtocolVisitor visitor) {
        return visitor.visit(this);
    }
}
