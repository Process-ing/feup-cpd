package protocol.unit;

import java.util.Optional;

import protocol.ProtocolOkIdentifier;
import protocol.ProtocolVisitor;

public record OkUnit(ProtocolOkIdentifier id, Optional<String> data) implements ProtocolUnit {
    public OkUnit(ProtocolOkIdentifier id) {
        this(id, Optional.empty());
    }

    public OkUnit(ProtocolOkIdentifier id, String data) {
        this(id, Optional.ofNullable(data));
    }

    @Override
    public String serialize() {
        return data.isEmpty()
            ? String.format("ok %s", id.getName())
            : String.format("ok %s %s", id.getName(), data.get());
    }

    @Override
    public <T> T accept(ProtocolVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
