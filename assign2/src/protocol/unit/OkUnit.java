package protocol.unit;

import java.util.Optional;

import protocol.ProtocolVisitor;

public record OkUnit(String data) implements ProtocolUnit {
    @Override
    public String serialize() {
        return String.format("ok %s", data);
    }

    @Override
    public Optional<ProtocolUnit> accept(ProtocolVisitor visitor) {
        return visitor.visit(this);
    }
}
