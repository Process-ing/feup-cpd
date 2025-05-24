package protocol.unit;

import protocol.ProtocolVisitor;

public record ListRoomsUnit() implements ProtocolUnit {
    @Override
    public String serialize() {
        return "list-rooms";
    }

    @Override
    public <T> T accept(ProtocolVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
