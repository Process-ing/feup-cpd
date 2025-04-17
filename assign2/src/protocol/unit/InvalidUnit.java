package protocol.unit;

import java.util.Optional;

import protocol.ProtocolVisitor;

public record InvalidUnit() implements ProtocolUnit {
    @Override
    public String serialize() {
        throw new UnsupportedOperationException("Cannot serialize invalid protocol unit");
    }

    @Override
    public Optional<ProtocolUnit> accept(ProtocolVisitor visitor) {
        throw new UnsupportedOperationException("Cannot visit invalid protocol unit");
    }
}
