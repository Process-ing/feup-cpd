package server;

import structs.AuthDb;

public class Server {
    private final AuthDb authDb;

    public Server(AuthDb authDb) {
        this.authDb = authDb;
    }

    public AuthDb geAuthDb() {
        return authDb;
    }
}
