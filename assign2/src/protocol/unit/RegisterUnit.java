package protocol.unit;

import java.util.Optional;

import protocol.ProtocolVisitor;

public record RegisterUnit(String user, String pass) implements ProtocolUnit {
    @Override
    public String serialize() {
        return String.format("register %s %s", user, pass);
    }

    @Override
    public Optional<ProtocolUnit> accept(ProtocolVisitor visitor) {
        return visitor.visit(this);
    }
}