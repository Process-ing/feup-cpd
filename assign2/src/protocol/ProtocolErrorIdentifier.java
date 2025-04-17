package protocol;

import java.util.Optional;

public enum ProtocolErrorIdentifier {
    SYNTAX,
    UNEXPECTED,
    ASYNC,
    LOGIN,
    REGISTER,
    UNKNOWN;

    private final String name;

    private ProtocolErrorIdentifier() {
        this.name = toKebabCase(name());
    }

    private static String toKebabCase(String string) {
        return string.toLowerCase().replace('_', '-');
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
