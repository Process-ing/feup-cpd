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
import structs.security.TokenManager;

public class Guest extends Client {
    public Guest(ClientThread thread) {
        super(thread);
    }

    @Override
    public Optional<ProtocolUnit> visit(LoginUnit unit) {
        ClientThread thread = getThread();
        AuthDb authDb = thread.getServer().getAuthDb();
        TokenManager tm = thread.getServer().getTokens();

        Optional<User> loggedUser = authDb.login(unit.user(), unit.pass(), thread);
        if (loggedUser.isEmpty())
            return Optional.of(new ErrUnit(ProtocolErrorIdentifier.LOGIN));

        thread.setClient(loggedUser.get());

        String token = tm.issue(unit.user());
        return Optional.of(new OkUnit(token));
    }

    @Override
    public Optional<ProtocolUnit> visit(RegisterUnit unit) {
        ClientThread thread = getThread();
        AuthDb authDb = thread.getServer().getAuthDb();
        TokenManager tm = thread.getServer().getTokens();

        Optional<User> newUser = authDb.register(unit.user(), unit.pass(), thread);
        if (newUser.isEmpty())
            return Optional.of(new ErrUnit(ProtocolErrorIdentifier.REGISTER));

        thread.setClient(newUser.get());

        String token = tm.issue(unit.user());
        return Optional.of(new OkUnit(token));
    }

    @Override
    public Optional<ProtocolUnit> visit(AuthTokenUnit unit) {
        ClientThread thread = getThread();
        TokenManager tm = thread.getServer().getTokens();

        Optional<String> validated = tm.validate(unit.token());

        if (validated.isEmpty()) {
            return Optional.of(new ErrUnit(ProtocolErrorIdentifier.LOGIN));
        }

        User newUser = new User(thread, validated.get());
        thread.setClient(newUser);

        String token = tm.issue(validated.get());
        return Optional.of(new OkUnit(token));
    }

    @Override
    public void cleanup() {
        // No cleanup needed for guest
    }
}
