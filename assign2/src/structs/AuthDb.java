package structs;

import java.util.Optional;
import server.ClientThread;
import server.client.User;

public interface AuthDb {
    Optional<User> register(String user, String pass, ClientThread thread);
    Optional<User> login(String user, String pass, ClientThread thread);
}
