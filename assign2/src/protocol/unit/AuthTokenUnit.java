package protocol.unit;

import java.util.Optional;
import protocol.ProtocolVisitor;

public record AuthTokenUnit(String token) implements ProtocolUnit {
    @Override
    public String serialize() {
        return String.format("login-token %s", token);
    }

    @Override
    public Optional<ProtocolUnit> accept(ProtocolVisitor visitor) {
        return visitor.visit(this);
    }
}
