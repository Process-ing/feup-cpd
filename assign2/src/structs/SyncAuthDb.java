package structs;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import server.ClientThread;
import server.client.User;

public class SyncAuthDb implements AuthDb {
    private final Map<String, String> logins;

    public SyncAuthDb() {
        // TODO(Process-ing): Add load from file
        this.logins = new HashMap<>();
    }

    @Override
    public Optional<User> register(String user, String pass, ClientThread thread) {
        if (logins.putIfAbsent(user, pass) != null) {
            return Optional.empty();
        }

        return Optional.of(new User(thread, user));
    }

    @Override
    public Optional<User> login(String user, String pass, ClientThread thread) {
        String storedPass = logins.get(user);
        if (!pass.equals(storedPass))
            return Optional.empty();

        return Optional.of(new User(thread, user));
    }
}
