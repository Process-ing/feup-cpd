package client.storage;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import utils.ConfigUtils;

public final class SessionStore {
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROOM = "room";
    private static final String KEY_LOCK = "lock";

    private final Optional<String> filename;
    private final Properties properties;

    public SessionStore(String filename, Properties properties) {
        this.filename = Optional.ofNullable(filename);
        this.properties = properties;
    }

    public SessionStore(String filename) throws IOException {
        this(filename, ConfigUtils.loadConfig(filename));
    }

    public SessionStore() {
        this(null, new Properties());
    }

    public void save() throws IOException {
        if (filename.isPresent())
            ConfigUtils.saveConfig(filename.get(), properties);
    }

    public boolean hasSession() {
        return getToken() != null && getUsername() != null;
    }

    public String getToken() {
        return properties.getProperty(KEY_TOKEN);
    }

    public String getRoom() {
        return properties.getProperty(KEY_ROOM);
    }

    public String getUsername() {
        return properties.getProperty(KEY_USERNAME);
    }

    public boolean isLocked() {
        return Boolean.parseBoolean(properties.getProperty(KEY_LOCK, "false"));
    }

    public void setToken(String token) {
        if (token != null)
            properties.setProperty(KEY_TOKEN, token);
        else properties.remove(KEY_TOKEN);
    }

    public void setRoom(String room) {
        if (room != null)
            properties.setProperty(KEY_ROOM, room);
        else properties.remove(KEY_ROOM);
    }

    public void setUsername(String username) {
        if (username != null)
            properties.setProperty(KEY_USERNAME, username);
        else properties.remove(KEY_USERNAME);
    }

    public void setLocked(boolean locked) {
        properties.setProperty(KEY_LOCK, Boolean.toString(locked));
    }

    public void clear() {
        properties.remove(KEY_ROOM);
        properties.remove(KEY_USERNAME);
        properties.remove(KEY_TOKEN);
    }
}
