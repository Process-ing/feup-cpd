package protocol.unit;

import java.util.Optional;

import protocol.ProtocolVisitor;

public record EofUnit() implements ProtocolUnit {
    @Override
    public String serialize() {
        return "EOF";
    }

    @Override
    public Optional<ProtocolUnit> accept(ProtocolVisitor visitor) {
        return visitor.visit(this);
    }
}
