package protocol.unit;

import protocol.ProtocolUtils;
import protocol.ProtocolVisitor;
import structs.Message;

public record SendUnit(String message) implements ProtocolUnit {
    public SendUnit(Message message) {
        this(message.content());
    }

    @Override
    public String serialize() {
        return String.format("send %s", ProtocolUtils.escapeToken(message));
    }

    @Override
    public <T> T accept(ProtocolVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
