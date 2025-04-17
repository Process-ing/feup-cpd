package structs;

import java.util.Optional;

import server.client.User;

public class SyncAuthDb implements AuthDb {
    @Override
    public Optional<User> register(String user, String pass) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'register'");
    }

    @Override
    public Optional<User> login(String user, String pass) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'login'");
    }
}
