package protocol;

public enum ProtocolErrorIdentifier {
    COMMAND,
    PARAMS,
    ASYNC;

    private final String name;

    private ProtocolErrorIdentifier() {
        this.name = name().toLowerCase();
    }

    public String getName() {
        return name;
    }
}
