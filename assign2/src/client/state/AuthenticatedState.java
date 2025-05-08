package client.state;

import client.Client;
import java.util.Optional;
import protocol.unit.EnterUnit;
import protocol.unit.LogoutUnit;
import protocol.unit.OkUnit;
import protocol.unit.ProtocolUnit;

public class AuthenticatedState extends ClientState {
    private final String username;

    public AuthenticatedState(Client client, String username) {
        super(client);

        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public Optional<ProtocolUnit> visit(OkUnit unit) {
        Client client = this.getClient();
        ProtocolUnit previousUnit = client.getPreviousUnit();

        switch (previousUnit) {
            case EnterUnit enterUnit -> {
                System.out.println("Entered room: " + enterUnit.roomName());
                client.setState(new RoomState(client, username, enterUnit.roomName()));
            }
            case LogoutUnit logoutUnit -> {
                System.out.println("Logged out: " + username);
                client.setState(new GuestState(client));
            }
            default -> {
            }
        }

        return Optional.empty();
    }
}