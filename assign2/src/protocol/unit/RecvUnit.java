package protocol.unit;

import java.util.Optional;

import protocol.ProtocolUtils;
import protocol.ProtocolVisitor;
import structs.Message;

public record RecvUnit(long vectorClock, String username, String message) implements ProtocolUnit {
    public RecvUnit(Message message) {
        this(message.id(), message.username(), message.content());
    }

    @Override
    public String serialize() {
        return String.format("recv %d %s %s", vectorClock, username, ProtocolUtils.escapeToken(message));
    }

    @Override
    public Optional<ProtocolUnit> accept(ProtocolVisitor visitor) {
        return visitor.visit(this);
    }
}
