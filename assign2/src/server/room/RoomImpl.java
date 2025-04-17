package server.room;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import server.client.RoomUser;
import server.client.User;
import structs.Message;
import structs.MessageTable;
import structs.SyncMessageTable;

public class RoomImpl implements Room {
    private final String name;
    private final Map<String, RoomUser> userMap;
    private final MessageTable messageTable;

    public RoomImpl(String name) {
        this.name = name;
        this.userMap = new HashMap<>();
        this.messageTable = new SyncMessageTable();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<RoomUser> getOnlineUsers() {
        return userMap.values().stream().toList();
    }

    @Override
    public Optional<RoomUser> connectUser(User user) {
        var currentUser = userMap.get(user.getName());
        if (currentUser != null)
            return Optional.of(currentUser);

        var newUser = new RoomUser(user.getName(), this);
        userMap.put(newUser.getName(), newUser);
        return Optional.of(newUser);

        // Never returns empty because there is no authorization
    }

    @Override
    public Optional<User> disconnectUser(RoomUser user) {
        var removedUser = userMap.remove(user.getName());
        if (removedUser == null)
            return Optional.empty();

        var newUser = new User(user.getName());
        return Optional.of(newUser);
    }

    @Override
    public Message addMessage(String content, RoomUser author) {
        return messageTable.add(author.getName(), content);
    }

    @Override
    public List<Message> getMessages() {
        return messageTable.getAll();
    }
}
