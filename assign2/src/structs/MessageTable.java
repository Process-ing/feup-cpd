package structs;

import java.util.Optional;

public interface MessageTable {
    Message add(String content);
    Optional<Message> get(int id);
}
