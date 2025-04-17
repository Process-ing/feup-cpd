package protocol.unit;

import protocol.ProtocolUtils;

public record SendUnit(String message) implements ProtocolUnit {
    @Override
    public String serialize() {
        return String.format("send %s", ProtocolUtils.escapeToken(message));
    }
}
