package server.client;

import java.util.Optional;
import protocol.ProtocolErrorIdentifier;
import protocol.unit.AuthTokenUnit;
import protocol.unit.ErrUnit;
import protocol.unit.LoginUnit;
import protocol.unit.OkUnit;
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

        Optional<User> loggedUser = authDb.loginPass(unit.user(), unit.pass(), thread);
        if (loggedUser.isEmpty())
            return Optional.of(new ErrUnit(ProtocolErrorIdentifier.LOGIN));

        thread.setClient(loggedUser.get());

        return Optional.of(new OkUnit(loggedUser.get().getToken()));
    }

    @Override
    public Optional<ProtocolUnit> visit(RegisterUnit unit) {
        ClientThread thread = getThread();
        AuthDb authDb = thread.getServer().getAuthDb();

        Optional<User> newUser = authDb.register(unit.user(), unit.pass(), thread);
        if (newUser.isEmpty())
            return Optional.of(new ErrUnit(ProtocolErrorIdentifier.REGISTER));

        thread.setClient(newUser.get());

        return Optional.of(new OkUnit(newUser.get().getToken()));
    }

    @Override
    public Optional<ProtocolUnit> visit(AuthTokenUnit unit) {
        ClientThread thread = getThread();
        AuthDb authDb = thread.getServer().getAuthDb();

        Optional<User> loggedUser = authDb.loginToken(unit.token(), thread);

        if (loggedUser.isEmpty()) 
            return Optional.of(new ErrUnit(ProtocolErrorIdentifier.LOGIN));
        
        thread.setClient(loggedUser.get());

        return Optional.of(new OkUnit(loggedUser.get().getToken()));
    }

    @Override
    public void cleanup() {
        // No cleanup needed for guest
    }
}
