package structs;

public interface AuthDb {
    boolean register(String user, String pass);
    boolean login(String user, String pass);
}
