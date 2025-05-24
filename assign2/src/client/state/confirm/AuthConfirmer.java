package client.state.confirm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import client.BaseClient;
import client.Cli;
import client.state.AuthState;
import client.state.GuestState;
import client.state.RoomState;
import client.storage.SessionStore;
import protocol.ProtocolOkIdentifier;
import protocol.unit.OkUnit;

record RoomInfo(String roomName, boolean isAi) {}

public class AuthConfirmer extends Confirmer<AuthState> {
    public AuthConfirmer(AuthState state) {
        super(state);
    }

    @Override
    protected void buildVisitor() {
        addVisit(ProtocolOkIdentifier.LIST_ROOMS, this::visitListRooms);
        addVisit(ProtocolOkIdentifier.CREATE_ROOM, (OkUnit confirmation) -> this.visitEnter(confirmation, true));
        addVisit(ProtocolOkIdentifier.ENTER_ROOM, (OkUnit confirmation) -> visitEnter(confirmation, false));
        addVisit(ProtocolOkIdentifier.LOGOUT, this::visitLogout);
    }

    public void visitListRooms(OkUnit confirmation) {
        Optional<String> data = confirmation.data();
        if (data.isEmpty())
            return;

        String[] roomDivisions = data.get().split("\\n\\n");
        if (roomDivisions.length != 2)
            return;

        List<String> normalRooms = toRoomNames(roomDivisions[0]);
        List<String> aiRooms = toRoomNames(roomDivisions[1]);

        Cli.printRooms(normalRooms, aiRooms);
    }

    private List<String> toRoomNames(String division) {
        List<String> roomNames = Arrays.asList(division.split("\\n"));
        if (roomNames.getFirst().equals(""))
            return List.of();
        Collections.sort(roomNames);
        return roomNames;
    }

    public void visitEnter(OkUnit confirmation, boolean created) {
        BaseClient client = getState().getClient();
        SessionStore session = client.getSession();
        String username = getState().getUsername();

        Optional<RoomInfo> roomInfo = parseRoomInfo(confirmation);
        if (roomInfo.isEmpty())
            return;

        String roomName = roomInfo.get().roomName();
        if (roomInfo.get().isAi()) {
            Cli.printResponse((created ? "Created" : "Entered") + " AI room: " + roomName);
            Cli.printMessage("Bot", "Hi, " + username + "! Welcome to the AI room " + roomName
                    + "! Asks questions and AI will answer.", false);
        } else {
            Cli.printResponse((created ? "Created" : "Entered") + " room: " + roomName);
        }

        client.setState(new RoomState(client, username, roomName));
        session.setRoom(roomName);
    }

    private Optional<RoomInfo> parseRoomInfo(OkUnit confirmation) {
        Optional<String> data = confirmation.data();
        if (data.isEmpty())
            return Optional.empty();

        String[] parts = data.get().split("\n", 2);
        if (parts.length < 2)
            return Optional.empty();

        String roomName = parts[0];
        boolean isAi = parts[1].equals("ai");

        return Optional.of(new RoomInfo(roomName, isAi));
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
