package server.client;

import java.util.Optional;

import protocol.ProtocolErrorIdentifier;
import protocol.unit.ErrUnit;
import protocol.unit.LoginUnit;
import protocol.unit.ProtocolUnit;
import protocol.unit.RegisterUnit;
import server.ClientThread;
import structs.AuthDb;

public class Guest implements Client {
    private final ClientThread thread;

    public Guest(ClientThread thread) {
        this.thread = thread;
    }

    public Optional<ProtocolUnit> handle(LoginUnit unit) {
        AuthDb authDb = thread.getServer().geAuthDb();

        var loggedUser = authDb.login(unit.user(), unit.pass());
        if (loggedUser.isEmpty())
            return Optional.of(new ErrUnit(ProtocolErrorIdentifier.LOGIN));

        thread.setClient(loggedUser.get());

        return Optional.empty();
    }

    public Optional<ProtocolUnit> handle(RegisterUnit unit) {
        AuthDb authDb = thread.getServer().geAuthDb();

        var newUser = authDb.register(unit.user(), unit.pass());
        if (newUser.isEmpty())
            return Optional.of(new ErrUnit(ProtocolErrorIdentifier.REGISTER));

        thread.setClient(newUser.get());

        return Optional.empty();
    }

    @Override
    public Optional<ProtocolUnit> handle(ProtocolUnit unit) {
        // Default handler for unknown commands
        return Optional.of(new ErrUnit(ProtocolErrorIdentifier.UNEXPECTED));
    }
}
