package protocol.unit;

import protocol.ProtocolErrorIdentifier;

public record ErrUnit(ProtocolErrorIdentifier id) implements ProtocolUnit {
    @Override
    public String serialize() {
        return String.format("err %s", id.getName());
    }
}
