package structs;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public class SyncMessageQueue implements MessageQueue {
    private final Queue<Message> queue;
    private final ReentrantLock lock;

    public SyncMessageQueue() {
        this(List.of());
    }

    public SyncMessageQueue(Collection<Message> queue) {
        this.queue = new ArrayDeque<>(queue);
        this.lock = new ReentrantLock();
    }

    @Override
    public void push(Message message) {
        lock.lock();
        try {
            queue.add(message);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Optional<Message> pop() {
        Message message;

        lock.lock();
        try {
            message = queue.poll();
        } finally {
            lock.unlock();
        }

        return Optional.ofNullable(message);
    }
}
