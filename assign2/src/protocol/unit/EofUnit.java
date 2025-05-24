package protocol.unit;

import protocol.ProtocolVisitor;

public record EofUnit() implements ProtocolUnit {
    @Override
    public String serialize() {
        return "EOF";
    }

    @Override
    public <T> T accept(ProtocolVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
