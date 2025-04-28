package protocol.unit;

import java.util.Optional;

import protocol.ProtocolVisitor;

public record LeaveUnit() implements ProtocolUnit {
    @Override
    public String serialize() {
        return "leave";
    }

    @Override
    public Optional<ProtocolUnit> accept(ProtocolVisitor visitor) {
        return visitor.visit(this);
    }
}
