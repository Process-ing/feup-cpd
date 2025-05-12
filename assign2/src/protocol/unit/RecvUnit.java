package protocol.unit;

import java.util.Optional;

import protocol.ProtocolUtils;
import protocol.ProtocolVisitor;
import structs.Message;

public record RecvUnit(String username, String message) implements ProtocolUnit {
    public RecvUnit(Message message) {
        this(message.username(), message.content());
    }

    @Override
    public String serialize() {
        return String.format("recv %s %s", username, ProtocolUtils.escapeToken(message));
    }

    @Override
    public Optional<ProtocolUnit> accept(ProtocolVisitor visitor) {
        return visitor.visit(this);
    }
}
