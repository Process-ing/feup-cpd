package protocol.unit;

import protocol.ProtocolVisitor;

public record LogoutUnit() implements ProtocolUnit {
    @Override
    public String serialize() {
        return "logout";
    }

    @Override
    public <T> T accept(ProtocolVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
