package client.state.confirm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import client.BaseClient;
import client.Cli;
import client.state.AuthState;
import client.state.GuestState;
import client.state.RoomState;
import client.storage.SessionStore;
import protocol.unit.EnterUnit;
import protocol.unit.ListRoomsUnit;
import protocol.unit.LogoutUnit;
import protocol.unit.OkUnit;

public class AuthConfirmer extends Confirmer<AuthState> {
    public AuthConfirmer(AuthState state) {
        super(state);
    }

    @Override
    public Void visit(ListRoomsUnit unit, OkUnit arg) {
        String[] roomDivisions = arg.data().split("\\n\\n");
        if (roomDivisions.length != 2)
            return null;

        List<String> normalRooms = toRoomNames(roomDivisions[0]);
        List<String> aiRooms = toRoomNames(roomDivisions[1]);

        Cli.printRooms(normalRooms, aiRooms);
        return null;
    }

    private List<String> toRoomNames(String division) {
        List<String> roomNames = Arrays.asList(division.split("\\n"));
        if (roomNames.getFirst().equals(""))
            return List.of();
        Collections.sort(roomNames);
        return roomNames;
    }

    @Override
    public Void visit(EnterUnit unit, OkUnit arg) {
        BaseClient client = getState().getClient();
        SessionStore session = client.getSession();
        String username = getState().getUsername();

        if (arg.data().equals("ai")) {
            Cli.printResponse("Entered AI room: " + unit.roomName());
            Cli.printMessage("Bot", "Hi, " + username + "! Welcome to the AI room " + unit.roomName()
                    + "! Asks questions and AI will answer.", false);
        } else {
            Cli.printResponse("Entered room: " + unit.roomName());
        }

        client.setState(new RoomState(client, username, unit.roomName()));
        session.setRoom(unit.roomName());

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
