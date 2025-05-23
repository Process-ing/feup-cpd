package protocol.unit;

import protocol.ArgedProtocolVisitor;
import protocol.ProtocolVisitor;

public record SyncUnit(int vectorClock) implements ProtocolUnit {
    @Override
    public String serialize() {
        return String.format("sync %d", vectorClock);
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
