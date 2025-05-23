package structs;

import java.util.List;
import java.util.Optional;

import server.client.RoomUser;

public interface MessageTable {
    Message add(RoomUser user, String content);
    Optional<Message> get(int id);
    List<Message> getAll();
    List<Message> getFrom(int id);
    List<Message> getLast(int count);
}
