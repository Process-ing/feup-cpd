package server.client;

import java.util.Optional;

import protocol.ProtocolErrorIdentifier;
import protocol.unit.ErrUnit;
import protocol.unit.ProtocolUnit;
import server.ClientThread;

public class User extends Client {
    private final String name;

    public User(ClientThread thread, String name) {
        super(thread);
        
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Optional<ProtocolUnit> handle(ProtocolUnit unit) {
        // Default handler for unknown commands
        return Optional.of(new ErrUnit(ProtocolErrorIdentifier.UNEXPECTED));
    }
}
