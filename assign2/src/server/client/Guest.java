package server.client;

import java.util.Optional;
import protocol.ProtocolErrorIdentifier;
import protocol.unit.AuthTokenUnit;
import protocol.unit.ErrUnit;
import protocol.unit.InvalidUnit;
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
    public Optional<ProtocolUnit> visit(LoginUnit u) {
        ClientThread thread = getThread();
        AuthDb authDb = thread.getServer().getAuthDb();
        TokenManager tm = thread.getServer().getTokens();

        Optional<User> loggedUser = authDb.login(u.user(), u.pass(), thread);
        if (loggedUser.isEmpty())
            return Optional.of(new ErrUnit(ProtocolErrorIdentifier.LOGIN));

        thread.setClient(loggedUser.get());

        String token = tm.issue(u.user());
        return Optional.of(new OkUnit(token));
    }

    @Override
    public Optional<ProtocolUnit> visit(RegisterUnit u) {
        ClientThread thread = getThread();
        AuthDb authDb = thread.getServer().getAuthDb();
        TokenManager tm = thread.getServer().getTokens();

        Optional<User> newUser = authDb.register(u.user(), u.pass(), thread);
        if (newUser.isEmpty())
            return Optional.of(new ErrUnit(ProtocolErrorIdentifier.REGISTER));

        thread.setClient(newUser.get());

        String token = tm.issue(u.user());
        return Optional.of(new OkUnit(token));
    }

    @Override
    public Optional<ProtocolUnit> visit(AuthTokenUnit u) {
        ClientThread thread = getThread();
        TokenManager tm = thread.getServer().getTokens();

        Optional<String> validated = tm.validate(u.token());

        if (validated.isEmpty()) {
            return Optional.of(new OkUnit(null));
        }

        if (u.room() == null) {
            User newUser = new User(thread, validated.get());
            thread.setClient(newUser);
        } else {
            // TODO(mm): Enter the room
        }

        String token = tm.issue(validated.get());
        return Optional.of(new OkUnit(token));
    }

    @Override
    public void cleanup() {
        // No cleanup needed for guest
    }
}
