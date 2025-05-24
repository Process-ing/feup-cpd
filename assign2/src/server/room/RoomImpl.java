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
    private final Map<Integer, RoomUser> userMap;  // Client thread ID to RoomUser mapping
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
        RoomUser newUser = new RoomUser(user.getThread(), user.getName(), this, user.getToken());
        userMap.put(newUser.getThread().getId(), newUser);
        return Optional.of(newUser);

        // Never returns empty because there is no authorization
    }

    @Override
    public Optional<User> disconnectUser(RoomUser user) {
        RoomUser removedUser = userMap.remove(user.getThread().getId());
        if (removedUser == null)
            return Optional.empty();

        User newUser = new User(user.getThread(), user.getName(), user.getToken());
        return Optional.of(newUser);
    }

    @Override
    public Message addMessage(String content, RoomUser author) {
        return messageTable.add(author, content);
    }

    @Override
    public List<Message> getMessages() {
        return messageTable.getAll();
    }

    @Override
    public List<Message> getMessages(int firstId) {
        return messageTable.getFrom(firstId);
    }
}
