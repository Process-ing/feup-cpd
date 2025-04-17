package server.client;

import java.util.Optional;

import protocol.unit.ProtocolUnit;

public class User implements Client {
    private final String name;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Optional<ProtocolUnit> handle(ProtocolUnit unit) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handle'");
    }
}
