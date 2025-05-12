package structs;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SyncMessageQueue implements MessageQueue {
    private final Queue<Message> queue;
    private final ReentrantLock lock;
    private final Condition notEmpty;

    public SyncMessageQueue() {
        this(List.of());
    }

    public SyncMessageQueue(Collection<Message> queue) {
        this.queue = new ArrayDeque<>(queue);
        this.lock = new ReentrantLock();
        this.notEmpty = this.lock.newCondition();
    }

    @Override
    public void push(Message message) {
        lock.lock();
        try {
            queue.add(message);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void pushAll(Collection<Message> messages) {
        lock.lock();
        try {
            for (Message message : messages) {
                queue.add(message);
            }
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Optional<Message> pop() {
        lock.lock();
        try {
            while (queue.isEmpty())
                notEmpty.await();
            return Optional.of(queue.poll());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } finally {
            lock.unlock();
        }
    }
}
