package client.state;

public class RoomState extends ClientState {
    private final String username;
    private final String roomName;

    public RoomState(String username, String roomName) {
        this.username = username;
        this.roomName = roomName;
    }

    public String getUsername() {
        return username;
    }

    public String getRoomName() {
        return roomName;
    }
}
