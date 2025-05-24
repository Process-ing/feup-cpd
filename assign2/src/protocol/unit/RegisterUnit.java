package protocol.unit;

import protocol.ProtocolVisitor;

public record RegisterUnit(String user, String pass) implements ProtocolUnit {
    @Override
    public String serialize() {
        return String.format("register %s %s", user, pass);
    }

    @Override
    public <T> T accept(ProtocolVisitor<T> visitor) {
        return visitor.visit(this);
    }
}