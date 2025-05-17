package protocol.unit;

import java.util.Optional;

import protocol.ProtocolVisitor;

public record PongUnit() implements ProtocolUnit {
    @Override
    public String serialize() {
        return "pong";
    }

    @Override
    public Optional<ProtocolUnit> accept(ProtocolVisitor visitor) {
        return visitor.visit(this);
    }
}
