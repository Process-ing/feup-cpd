package protocol.unit;

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
}
