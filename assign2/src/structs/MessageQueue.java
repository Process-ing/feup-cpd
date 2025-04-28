package structs;

import java.util.Optional;

public interface MessageQueue {
    void push(Message message);
    Optional<Message> pop();
}
