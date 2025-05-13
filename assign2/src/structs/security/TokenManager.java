package structs.security;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class TokenManager {
    
    private static final int TOKEN_BYTES = 32;
    private static final Duration TTL = Duration.ofHours(24);

    private final Map<String, Session> byToken = new HashMap<>();
    private final Map<String, String>  byUser  = new HashMap<>();

    private final SecureRandom RNG = new SecureRandom();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();


    public String issue(String username) {
        byte[] buf = new byte[TOKEN_BYTES];
        RNG.nextBytes(buf);
        String token = Base64.getUrlEncoder().withoutPadding()
                             .encodeToString(buf);

        Session s = new Session(username, token, Instant.now());

        lock.writeLock().lock();
        try {
            String old = byUser.put(username, s.token());
            if (old != null) byToken.remove(old);

            byToken.put(token, s);
        } finally { lock.writeLock().unlock(); }

        return token;
    }

    public Optional<String> validate(String token) {
        lock.readLock().lock();
        try {
            Session s = byToken.get(token);
            if (s == null) return Optional.empty();
            if (s.issued().plus(TTL).isBefore(Instant.now())) return Optional.empty();

            return Optional.of(s.username());
        } finally { lock.readLock().unlock(); }
    }

    private static record Session(String username, String token, Instant issued) {}
}
