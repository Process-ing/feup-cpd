package protocol.unit;

import java.util.Optional;

import protocol.ProtocolVisitor;

public record InvalidUnit() implements ProtocolUnit {
    @Override
    public String serialize() {
        return "INVALID";
    }

    @Override
    public Optional<ProtocolUnit> accept(ProtocolVisitor visitor) {
        return visitor.visit(this);
    }
}
