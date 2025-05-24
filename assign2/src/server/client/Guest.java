package server.client;

import java.util.Optional;
import protocol.ProtocolErrorIdentifier;
import protocol.ProtocolOkIdentifier;
import protocol.ProtocolUtils;
import protocol.unit.TokenLoginUnit;
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

        if (authDb.userLoggedIn(unit.user()))
            return Optional.of(new ErrUnit(ProtocolErrorIdentifier.REPEATED_LOGIN));

        Optional<User> loggedUser = authDb.loginPass(unit.user(), unit.pass(), thread);
        if (loggedUser.isEmpty())
            return Optional.of(new ErrUnit(ProtocolErrorIdentifier.LOGIN));

        String token = loggedUser.get().getToken();
        String username = loggedUser.get().getName();

        thread.setClient(loggedUser.get());

        ProtocolUnit response = new OkUnit(
            ProtocolOkIdentifier.LOGIN,
            ProtocolUtils.escapeToken(token + "\n" + username)
        );
        return Optional.of(response);
    }

    @Override
    public Optional<ProtocolUnit> visit(RegisterUnit unit) {
        ClientThread thread = getThread();
        AuthDb authDb = thread.getServer().getAuthDb();

        Optional<User> newUser = authDb.register(unit.user(), unit.pass(), thread);
        if (newUser.isEmpty())
            return Optional.of(new ErrUnit(ProtocolErrorIdentifier.REGISTER));

        String token = newUser.get().getToken();
        String username = newUser.get().getName();

        thread.setClient(newUser.get());

        ProtocolUnit response = new OkUnit(
            ProtocolOkIdentifier.REGISTER,
            ProtocolUtils.escapeToken(token + "\n" + username)
        );
        return Optional.of(response);
    }

    @Override
    public Optional<ProtocolUnit> visit(TokenLoginUnit unit) {
        ClientThread thread = getThread();
        AuthDb authDb = thread.getServer().getAuthDb();

        if (authDb.userLoggedInWith(unit.token()))
            return Optional.of(new ErrUnit(ProtocolErrorIdentifier.REPEATED_LOGIN));

        Optional<User> loggedUser = authDb.loginToken(unit.token(), thread);
        if (loggedUser.isEmpty())
            return Optional.of(new ErrUnit(ProtocolErrorIdentifier.LOGIN));

        String token = loggedUser.get().getToken();
        String username = loggedUser.get().getName();

        thread.setClient(loggedUser.get());

        ProtocolUnit response = new OkUnit(
            ProtocolOkIdentifier.LOGIN,
            ProtocolUtils.escapeToken(token + "\n" + username)
        );
        return Optional.of(response);
    }

    @Override
    public void cleanup() {
        // No cleanup needed for guest
    }

    @Override
    public String toString() {
        return "guest";
    }
}
