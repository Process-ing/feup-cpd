package client.state.confirm;

import client.BaseClient;
import client.Cli;
import client.state.AuthenticatedState;
import client.state.GuestState;
import client.state.RoomState;
import client.storage.SessionStore;
import protocol.unit.LeaveUnit;
import protocol.unit.LogoutUnit;
import protocol.unit.OkUnit;

public class RoomConfirmer extends Confirmer<RoomState> {
    public RoomConfirmer(RoomState state) {
        super(state);
    }

    @Override
    public Void visit(LeaveUnit unit, OkUnit arg) {
        BaseClient client = getState().getClient();
        SessionStore session = client.getSession();
        String username = getState().getUsername();
        String room = getState().getRoomName();

        Cli.printResponse("Left room: " + room);

        client.setState(new AuthenticatedState(client, username));
        session.clear();

        return null;
    }

    @Override
    public Void visit(LogoutUnit unit, OkUnit arg) {
        BaseClient client = getState().getClient();
        SessionStore session = client.getSession();
        String username = getState().getUsername();

        Cli.printResponse("Logged out: " + username);

        client.setState(new GuestState(client));
        session.clear();

        return null;
    }
}
