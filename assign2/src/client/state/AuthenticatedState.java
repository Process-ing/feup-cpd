package client.state;

import client.Client;

public class AuthenticatedState extends ClientState {
    private final String username;

    public AuthenticatedState(Client client, String username) {
        super(client);

        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}