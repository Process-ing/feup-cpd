package protocol.unit;

import java.util.Optional;

import protocol.ProtocolVisitor;

public record EofUnit() implements ProtocolUnit {
    @Override
    public String serialize() {
        throw new UnsupportedOperationException("Cannot serialize EOF protocol unit");
    }

    @Override
    public Optional<ProtocolUnit> accept(ProtocolVisitor visitor) {
        return visitor.visit(this);
    }
}
