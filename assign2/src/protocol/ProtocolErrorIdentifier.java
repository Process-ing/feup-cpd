package protocol;

import java.util.Optional;

public enum ProtocolErrorIdentifier {
    SYNTAX,
    UNEXPECTED,
    ASYNC,
    LOGIN,
    REPEATED_LOGIN,
    REGISTER,
    UNAUTHORIZED,
    UNKNOWN;

    private final String name;

    private ProtocolErrorIdentifier() {
        this.name = ProtocolUtils.toKebabCase(name());
    }

    public String getName() {
        return name;
    }

    public static Optional<ProtocolErrorIdentifier> fromString(String name) {
        for (ProtocolErrorIdentifier value: values()) {
            if (value.name.equals(name))
                return Optional.of(value);
        }

        return Optional.empty();
    }
}
