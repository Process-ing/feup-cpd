package protocol.unit;

import protocol.ProtocolVisitor;

public record LoginUnit(String user, String pass) implements ProtocolUnit {
    @Override
    public String serialize() {
        return String.format("login %s %s", user, pass);
    }

    @Override
    public <T> T accept(ProtocolVisitor<T> visitor) {
        return visitor.visit(this);
    }
}