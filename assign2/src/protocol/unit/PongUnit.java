package protocol.unit;

import protocol.ProtocolVisitor;

public record PongUnit() implements ProtocolUnit {
    @Override
    public String serialize() {
        return "pong";
    }

    @Override
    public <T> T accept(ProtocolVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
