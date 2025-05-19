package client.state;

import client.Client;
import client.storage.SessionStore;
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
        SessionStore session = client.getSession();
        ProtocolUnit previousUnit = client.getPreviousUnit();

        switch (previousUnit) {
            case EnterUnit enterUnit -> {
                if (unit.data().equals("ai")) {
                    System.out.println("Entered AI room: " + enterUnit.roomName());
                    System.out.println("Hi, " + username + "! Welcome to the AI room " + enterUnit.roomName() + "! Asks questions and AI will answer.");
                } else {
                    System.out.println("Entered room: " + enterUnit.roomName());
                }
                client.setState(new RoomState(client, username, enterUnit.roomName()));
                session.setRoom(enterUnit.roomName());
            }
            case LogoutUnit logoutUnit -> {
                System.out.println("Logged out: " + username);
                client.setState(new GuestState(client));
                session.clear();
                System.out.println("Session cleared");
            }
            default -> {
                // No other actions should be possible in this state
            }
        }

        try {
            session.save();
        } catch (Exception e) {
            System.err.println("Failed to save session: " + e.getMessage());
        }

        return Optional.empty();
    }
}
