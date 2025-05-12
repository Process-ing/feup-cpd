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
import structs.security.TokenManager;
import structs.storage.AuthFileStore;

public class SyncAuthDb implements AuthDb {
    private final Map<String, CredentialRecord> creds;
    private final AuthFileStore store;
    private final TokenManager tokenManager;

    private final ReentrantReadWriteLock.ReadLock readLock;
    private final ReentrantReadWriteLock.WriteLock writeLock;

    public SyncAuthDb(Path file) throws IOException {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();

        this.store = new AuthFileStore(file);
        this.creds = new HashMap<>(store.load());
        this.tokenManager = new TokenManager();
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

        String token = tokenManager.issue(user);

        return Optional.of(new User(thread, user, token));
    }

    @Override
    public Optional<User> loginPass(String user, String pass, ClientThread thread) {
        readLock.lock();

        try {
            CredentialRecord rec = creds.get(user);
            if (rec == null || !PasswordHasher.verify(pass.toCharArray(), rec)) 
                return Optional.empty();
        } finally {
            readLock.unlock();
        }

        String token = tokenManager.issue(user);

        return Optional.of(new User(thread, user, token));
    }

    @Override
    public Optional<User> loginToken(String token, ClientThread thread) {
        Optional<String> validated = tokenManager.validate(token);

        if (validated.isEmpty()) return Optional.empty();

        String newToken = tokenManager.issue(validated.get());
        
        return Optional.of(new User(thread, validated.get(), newToken));
    }
}
