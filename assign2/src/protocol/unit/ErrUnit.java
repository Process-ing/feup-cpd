package protocol.unit;

import java.util.Optional;

import protocol.ProtocolErrorIdentifier;
import protocol.ProtocolVisitor;

public record ErrUnit(ProtocolErrorIdentifier id) implements ProtocolUnit {
    @Override
    public String serialize() {
        return String.format("err %s", id.getName());
    }

    @Override
    public Optional<ProtocolUnit> accept(ProtocolVisitor visitor) {
        return visitor.visit(this);
    }
}
