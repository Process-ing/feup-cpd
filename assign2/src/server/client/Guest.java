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

    @Override
    public Optional<ProtocolUnit> handle(ProtocolUnit unit) {
        return Optional.of(new ErrUnit(ProtocolErrorIdentifier.COMMAND));
    }

    public Optional<ProtocolUnit> handle(LoginUnit unit) {
        AuthDb authDb = thread.getServer().geAuthDb();

        if (!authDb.login(unit.user(), unit.pass()))
            return Optional.of(new ErrUnit(ProtocolErrorIdentifier.FAILED_LOGIN));

        thread.setClient(new User(unit.user()));

        return Optional.empty();
    }

    public Optional<ProtocolUnit> handle(RegisterUnit unit) {
        AuthDb authDb = thread.getServer().geAuthDb();

        if (!authDb.register(unit.user(), unit.pass()))
            return Optional.of(new ErrUnit(ProtocolErrorIdentifier.FAILED_REGISTER));

        thread.setClient(new User(unit.user()));

        return Optional.empty();
    }
}
