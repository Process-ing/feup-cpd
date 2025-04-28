package structs;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import server.ClientThread;
import server.client.User;

public class SyncAuthDb implements AuthDb {
    private final Map<String, String> logins;
    private final ReentrantReadWriteLock.ReadLock readLock;
    private final ReentrantReadWriteLock.WriteLock writeLock;

    public SyncAuthDb() {
        // TODO(Process-ing): Add load from file
        this.logins = new HashMap<>();

        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }

    @Override
    public Optional<User> register(String user, String pass, ClientThread thread) {
        String storedPass;

        writeLock.lock();
        try {
            storedPass = logins.putIfAbsent(user, pass);
        } finally {
            writeLock.unlock();
        }

        if (storedPass != null) {
            return Optional.empty();
        }

        return Optional.of(new User(thread, user));
    }

    @Override
    public Optional<User> login(String user, String pass, ClientThread thread) {
        String storedPass;

        readLock.lock();
        try {
            storedPass = logins.get(user);
        } finally {
            readLock.unlock();
        }

        if (!pass.equals(storedPass))
            return Optional.empty();

        return Optional.of(new User(thread, user));
    }
}
