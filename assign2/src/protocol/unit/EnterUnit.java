package protocol.unit;

import protocol.ProtocolUtils;

public record EnterUnit(String roomName) implements ProtocolUnit {
    @Override
    public String serialize() {
        return String.format("enter %s", ProtocolUtils.escapeToken(roomName));
    }
}
