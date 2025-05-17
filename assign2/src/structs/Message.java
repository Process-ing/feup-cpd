package structs;

public record Message(int id, String username, String content) {
    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public int getId() {
        return id;
    }
}
