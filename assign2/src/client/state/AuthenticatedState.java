package client.state;

public class AuthenticatedState extends ClientState {
    private final String username;

    public AuthenticatedState(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}