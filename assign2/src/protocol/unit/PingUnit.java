package protocol.unit;

import java.util.Optional;

import protocol.ProtocolVisitor;

public record PingUnit() implements ProtocolUnit {
    @Override
    public String serialize() {
        return "ping";
    }

    @Override
    public Optional<ProtocolUnit> accept(ProtocolVisitor visitor) {
        return visitor.visit(this);
    }
}
