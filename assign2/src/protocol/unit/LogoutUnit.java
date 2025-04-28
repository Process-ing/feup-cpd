package protocol.unit;

import java.util.Optional;

import protocol.ProtocolVisitor;

public record LogoutUnit() implements ProtocolUnit {
    @Override
    public String serialize() {
        return "logout";
    }

    @Override
    public Optional<ProtocolUnit> accept(ProtocolVisitor visitor) {
        return visitor.visit(this);
    }
}
