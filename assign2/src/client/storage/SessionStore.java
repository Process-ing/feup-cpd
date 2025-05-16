package client.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class SessionStore {

    private static final String COMMENT = "Chat session.";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROOM = "room";

    private final Path file;
    private final Properties p = new Properties();

    public SessionStore(Path file) { 
        this.file = file; 
        load();
    }

    public void load() {
        if (Files.exists(file))
            try (InputStream in = Files.newInputStream(file)) {
                p.load(in);
            }
            catch (IOException ignored) { }
    }

    public void save() {
        try {
            Files.createDirectories(file.getParent());
            OutputStream out = Files.newOutputStream(file);
            p.store(out, COMMENT);
        } catch (IOException ignored) { }
    }

    public boolean hasSession() {
        return getToken() != null && getUsername() != null;
    }

    public String getToken() {
        return p.getProperty(KEY_TOKEN);
    }

    public String getRoom() { 
        return p.getProperty(KEY_ROOM);  
    }

    public String getUsername() {
        return p.getProperty(KEY_USERNAME);
    }

    public void setToken(String token) { 
        if (token != null) p.setProperty(KEY_TOKEN, token);
        else p.remove(KEY_TOKEN);
    }

    public void setRoom(String room) { 
        if (room != null) p.setProperty(KEY_ROOM, room);
        else p.remove(KEY_ROOM); 
    }

    public void setUsername(String username) { 
        if (username != null) p.setProperty(KEY_USERNAME, username);
        else p.remove(KEY_USERNAME);
    }

    public void clear() {
        p.remove(KEY_ROOM);
        p.remove(KEY_USERNAME);
        p.remove(KEY_TOKEN);
    }
}
