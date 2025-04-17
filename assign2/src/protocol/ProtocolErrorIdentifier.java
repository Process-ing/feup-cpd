package protocol;

import java.util.Optional;

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

    public static Optional<ProtocolErrorIdentifier> fromString(String name) {
        for (var value: values()) {
            if (value.name.equals(name))
                return Optional.of(value);
        }

        return Optional.empty();
    }
}
