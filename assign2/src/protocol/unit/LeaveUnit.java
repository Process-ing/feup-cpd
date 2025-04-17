package protocol.unit;

public record LeaveUnit() implements ProtocolUnit {
    @Override
    public String serialize() {
        return "leave";
    }
}
