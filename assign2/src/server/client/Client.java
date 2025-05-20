package server.client;

import java.util.Optional;

import protocol.DefaultProtocolVisitor;
import protocol.ProtocolErrorIdentifier;
import protocol.unit.ErrUnit;
import protocol.unit.ProtocolUnit;
import server.ClientThread;

public abstract class Client implements DefaultProtocolVisitor<Optional<ProtocolUnit>> {
    private final ClientThread thread;

    public Client(ClientThread thread) {
        this.thread = thread;
    }

    public ClientThread getThread() {
        return thread;
    }

    @Override
    public Optional<ProtocolUnit> visitDefault(ProtocolUnit unit) {
        return Optional.of(new ErrUnit(ProtocolErrorIdentifier.UNEXPECTED));
    }

    public abstract void cleanup();
}
