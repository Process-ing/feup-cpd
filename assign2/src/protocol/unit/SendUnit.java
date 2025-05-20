package protocol.unit;

import protocol.ArgedProtocolVisitor;
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

    @Override
    public <R, A> R accept(ArgedProtocolVisitor<R, A> visitor, A arg) {
        return visitor.visit(this, arg);
    }
}
