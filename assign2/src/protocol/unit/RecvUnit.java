package protocol.unit;

import protocol.ProtocolUtils;
import protocol.ProtocolVisitor;
import structs.Message;

public record RecvUnit(int id, String username, String message) implements ProtocolUnit {
    public RecvUnit(Message message) {
        this(message.id(), message.username(), message.content());
    }

    @Override
    public String serialize() {
        return String.format("recv %d %s %s", id, username, ProtocolUtils.escapeToken(message));
    }

    @Override
    public <T> T accept(ProtocolVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
