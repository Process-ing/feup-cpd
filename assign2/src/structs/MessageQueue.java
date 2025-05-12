package structs;

import java.util.Collection;
import java.util.Optional;

public interface MessageQueue {
    void push(Message message);
    void pushAll(Collection<Message> messages);
    Optional<Message> pop();
}
