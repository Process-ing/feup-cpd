package protocol.unit;

import protocol.ProtocolErrorIdentifier;
import protocol.ProtocolVisitor;

public record ErrUnit(ProtocolErrorIdentifier id) implements ProtocolUnit {
    @Override
    public String serialize() {
        return String.format("err %s", id.getName());
    }

    @Override
    public <T> T accept(ProtocolVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
