package structs;

import java.util.Optional;

import server.client.User;

public interface AuthDb {
    Optional<User> register(String user, String pass);
    Optional<User> login(String user, String pass);
}
