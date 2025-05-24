package protocol.unit;

import protocol.ProtocolVisitor;

public record PingUnit() implements ProtocolUnit {
    @Override
    public String serialize() {
        return "ping";
    }

    @Override
    public <T> T accept(ProtocolVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
