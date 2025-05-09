package structs;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import server.ClientThread;
import server.client.User;
import structs.security.PasswordHasher;
import structs.storage.AuthFileStore;

public class SyncAuthDb implements AuthDb {
    private final Map<String, CredentialRecord> creds;
    private final AuthFileStore store;

    private final ReentrantReadWriteLock.ReadLock readLock;
    private final ReentrantReadWriteLock.WriteLock writeLock;

    public SyncAuthDb(Path file) throws IOException {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();

        this.store = new AuthFileStore(file);
        this.creds = new HashMap<>(store.load());
    }

    @Override
    public Optional<User> register(String user, String pass, ClientThread thread) {
        writeLock.lock();

        try {
            if (creds.containsKey(user)) return Optional.empty();
            
            CredentialRecord rec = PasswordHasher.hash(pass.toCharArray());
            creds.put(user, rec);

            try {
                store.append(user, rec);
            } catch (IOException ioe) {
                creds.remove(user);
                return Optional.empty();
            }

        } finally {
            writeLock.unlock();
        }

        return Optional.of(new User(thread, user));
    }

    @Override
    public Optional<User> login(String user, String pass, ClientThread thread) {
        readLock.lock();

        try {
            CredentialRecord rec = creds.get(user);
            if (rec == null || !PasswordHasher.verify(pass.toCharArray(), rec)) 
                return Optional.empty();
        } finally {
            readLock.unlock();
        }

        return Optional.of(new User(thread, user));
    }
}
