package protocol.unit;

import java.util.Optional;

import protocol.ProtocolVisitor;

public record SyncUnit(int vectorClock) implements ProtocolUnit {
    @Override
    public String serialize() {
        return String.format("sync %d", vectorClock);
    }

    @Override
    public Optional<ProtocolUnit> accept(ProtocolVisitor visitor) {
        return visitor.visit(this);
    }
}
