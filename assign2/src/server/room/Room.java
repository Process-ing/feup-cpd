package server.room;

import java.util.List;
import java.util.Optional;

import server.client.RoomUser;
import server.client.User;
import structs.Message;

public interface Room {
    String getName();

    List<RoomUser> getOnlineUsers();
    Optional<RoomUser> connectUser(User user);
    Optional<User> disconnectUser(RoomUser user);

    Message addMessage(String content, RoomUser author);
    List<Message> getMessages();
    List<Message> getMessages(int firstId);
}
