package protocol.unit;

public record InvalidUnit() implements ProtocolUnit {
    @Override
    public String serialize() {
        throw new UnsupportedOperationException("Cannot serialize invalid protocol unit");
    }
}
