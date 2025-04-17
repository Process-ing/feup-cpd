package protocol.unit;

public record LogoutUnit() implements ProtocolUnit {
    @Override
    public String serialize() {
        return "logout";
    }
}
