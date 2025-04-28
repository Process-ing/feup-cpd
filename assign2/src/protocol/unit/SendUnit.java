package protocol.unit;

import java.util.Optional;

import protocol.ProtocolUtils;
import protocol.ProtocolVisitor;
import structs.Message;

public record SendUnit(String username, String message) implements ProtocolUnit {
    public SendUnit(Message message) {
        this(message.username(), message.content());
    }

    @Override
    public String serialize() {
        return String.format("send %s %s", username, ProtocolUtils.escapeToken(message));
    }

    @Override
    public Optional<ProtocolUnit> accept(ProtocolVisitor visitor) {
        return visitor.visit(this);
    }
}
