package client.state.confirm;

import client.BaseClient;
import client.Cli;
import client.state.AuthState;
import client.state.GuestState;
import client.state.RoomState;
import client.storage.SessionStore;
import protocol.ProtocolOkIdentifier;
import protocol.unit.OkUnit;

public class RoomConfirmer extends Confirmer<RoomState> {
    public RoomConfirmer(RoomState state) {
        super(state);
    }

    @Override
    protected void buildVisitor() {
        addVisit(ProtocolOkIdentifier.LEAVE_ROOM, this::visitLeave);
        addVisit(ProtocolOkIdentifier.LOGOUT, this::visitLogout);
    }

    public void visitLeave(OkUnit confirmation) {
        BaseClient client = getState().getClient();
        SessionStore session = client.getSession();
        String username = getState().getUsername();
        String room = getState().getRoomName();

        Cli.printResponse("Left room: " + room);

        client.setState(new AuthState(client, username));
        session.clear();
    }

    public void visitLogout(OkUnit confirmation) {
        BaseClient client = getState().getClient();
        SessionStore session = client.getSession();
        String username = getState().getUsername();

        Cli.printResponse("Logged out: " + username);

        client.setState(new GuestState(client));
        session.clear();
    }
}
