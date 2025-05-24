package protocol.unit;

import protocol.ProtocolVisitor;

public record LeaveUnit() implements ProtocolUnit {
    @Override
    public String serialize() {
        return "leave";
    }

    @Override
    public <T> T accept(ProtocolVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
