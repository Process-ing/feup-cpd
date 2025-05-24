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

    private static record Session(String username, String token, Instant issued) {}

    private final Map<String, Session> userSessionMap;
    private final Map<String, String> tokenUserMap;

    private final SecureRandom rng;
    private final ReentrantReadWriteLock lock;

    public TokenManager() {
        this.userSessionMap = new HashMap<>();
        this.tokenUserMap = new HashMap<>();

        this.rng = new SecureRandom();
        this.lock = new ReentrantReadWriteLock();
    }

    public String issue(String username) {
        byte[] buf = new byte[TOKEN_BYTES];
        rng.nextBytes(buf);

        String token = Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(buf);

        Session session = new Session(username, token, Instant.now());

        lock.writeLock().lock();
        try {
            tokenUserMap.put(username, session.token());
            userSessionMap.put(token, session);
        } finally { lock.writeLock().unlock(); }

        return token;
    }

    public Optional<String> validate(String token) {
        lock.readLock().lock();
        try {
            Session session = userSessionMap.get(token);
            if (session == null)
                return Optional.empty();

            if (session.issued().plus(TTL).isBefore(Instant.now()))
                return Optional.empty();

            return Optional.of(session.username());
        } finally { lock.readLock().unlock(); }
    }

    public boolean invalidate(String token) {
        lock.writeLock().lock();
        try {
            Session session = userSessionMap.remove(token);
            if (session != null) {
                tokenUserMap.remove(session.username());
                return true;
            } else {
                return false;
            }
        } finally { lock.writeLock().unlock(); }
    }

    public String getUserToken(String username) {
        lock.readLock().lock();
        try {
            return tokenUserMap.get(username);
        } finally { lock.readLock().unlock(); }
    }
}
