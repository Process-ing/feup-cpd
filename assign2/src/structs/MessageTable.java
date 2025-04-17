package structs;

import java.util.List;
import java.util.Optional;

public interface MessageTable {
    Message add(String username, String content);
    Optional<Message> get(int id);
    List<Message> getAll();
}
