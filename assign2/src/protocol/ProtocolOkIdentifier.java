package protocol;

import java.util.Optional;

public enum ProtocolOkIdentifier {
    LOGIN,
    TOKEN_LOGIN,
    REGISTER,
    LOGOUT,
    LIST_ROOMS,
    CREATE_ROOM,
    ENTER_ROOM,
    LEAVE_ROOM;

    private final String name;

    private ProtocolOkIdentifier() {
        this.name = ProtocolUtils.toKebabCase(name());
    }

    public String getName() {
        return name;
    }

    public static Optional<ProtocolOkIdentifier> fromString(String name) {
        for (ProtocolOkIdentifier value: values()) {
            if (value.name.equals(name))
                return Optional.of(value);
        }

        return Optional.empty();
    }
}
