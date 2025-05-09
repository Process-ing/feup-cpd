package protocol.unit;

import java.util.Optional;
import protocol.ProtocolVisitor;

public record AuthTokenUnit(String token, String room) implements ProtocolUnit {
    @Override
    public String serialize() {
        return room == null 
            ? String.format("login-token %s", token)
            : String.format("login-token %s %s", token, room);
    }

    @Override
    public Optional<ProtocolUnit> accept(ProtocolVisitor visitor) {
        return visitor.visit(this);
    }
}
