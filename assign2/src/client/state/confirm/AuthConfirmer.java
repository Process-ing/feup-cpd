package client.state.confirm;

import client.Cli;
import client.state.AuthenticatedState;
import client.state.GuestState;
import client.state.RoomState;
import client.storage.SessionStore;
import protocol.unit.EnterUnit;
import protocol.unit.LogoutUnit;
import protocol.unit.OkUnit;
import client.Client;

public class AuthConfirmer extends Confirmer<AuthenticatedState> {
    public AuthConfirmer(AuthenticatedState state) {
        super(state);
    }

    @Override
    public Void visit(EnterUnit unit, OkUnit arg) {
        Client client = getState().getClient();
        SessionStore session = client.getSession();
        String username = getState().getUsername();

        Cli.printResponse("Entered room: " + unit.roomName());

        client.setState(new RoomState(client, username, unit.roomName()));
        session.setRoom(unit.roomName());

        return null;
    }

    @Override
    public Void visit(LogoutUnit unit, OkUnit arg) {
        Client client = getState().getClient();
        SessionStore session = client.getSession();
        String username = getState().getUsername();

        Cli.printResponse("Logged out: " + username);

        client.setState(new GuestState(client));
        session.clear();

        return null;
    }
}
