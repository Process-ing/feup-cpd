package protocol.unit;

public record OkUnit(String data) implements ProtocolUnit {
    @Override
    public String serialize() {
        return String.format("ok %s", data);
    }
}
