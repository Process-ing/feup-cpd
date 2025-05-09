package client.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class SessionStore {

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
            p.store(out, "chat session");
        } catch (IOException ignored) { }
    }

    public boolean exist() {
        return getToken() != null && getUsername() != null;
    }

    public String getToken() {
        return p.getProperty("token");
    }

    public String getRoom() { 
        return p.getProperty("room");  
    }

    public String getUsername() {
        return p.getProperty("username");
    }

    public void setToken(String t) { 
        p.setProperty("token", t);
    }

    public void setRoom(String r) { 
        if (r != null) p.setProperty("room", r);
        else p.remove("room"); 
    }

    public void setUsername(String u) { 
        p.setProperty("username", u);
    }

    public void removeRoom() {
        p.remove("room");
    }

    public void clear() {
        p.remove("room");
        p.remove("username");
        p.remove("token");
    }
}

