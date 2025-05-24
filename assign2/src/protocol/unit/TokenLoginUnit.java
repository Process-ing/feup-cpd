package protocol.unit;

import protocol.ProtocolVisitor;

public record TokenLoginUnit(String token) implements ProtocolUnit {
    @Override
    public String serialize() {
        return String.format("login-token %s", token);
    }

    @Override
    public <T> T accept(ProtocolVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
