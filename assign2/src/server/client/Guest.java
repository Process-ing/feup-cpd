package server.client;

import java.util.Optional;

import protocol.ProtocolErrorIdentifier;
import protocol.unit.ErrUnit;
import protocol.unit.LoginUnit;
import protocol.unit.ProtocolUnit;
import protocol.unit.RegisterUnit;
import server.ClientThread;
import structs.AuthDb;

public class Guest extends Client {
    public Guest(ClientThread thread) {
        super(thread);
    }

    @Override
    public Optional<ProtocolUnit> visit(LoginUnit unit) {
        ClientThread thread = getThread();
        AuthDb authDb = thread.getServer().getAuthDb();

        Optional<User> loggedUser = authDb.login(unit.user(), unit.pass(), thread);
        if (loggedUser.isEmpty())
            return Optional.of(new ErrUnit(ProtocolErrorIdentifier.LOGIN));

        thread.setClient(loggedUser.get());

        return Optional.empty();
    }

    @Override
    public Optional<ProtocolUnit> visit(RegisterUnit unit) {
        ClientThread thread = getThread();
        AuthDb authDb = thread.getServer().getAuthDb();

        Optional<User> newUser = authDb.register(unit.user(), unit.pass(), thread);
        if (newUser.isEmpty())
            return Optional.of(new ErrUnit(ProtocolErrorIdentifier.REGISTER));

        thread.setClient(newUser.get());

        return Optional.empty();
    }

    @Override
    public void cleanup() {
        // No cleanup needed for guest
    }
}
