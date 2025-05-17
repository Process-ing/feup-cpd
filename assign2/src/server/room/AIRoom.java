package server.room;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import server.client.RoomUser;
import server.client.User;
import structs.Message;
import structs.MessageQueue;
import structs.MessageTable;
import structs.SyncMessageTable;

public class AIRoom implements Room {
    private static final int WINDOW = 50;

    private final String name;
    private final Map<String, RoomUser> userMap;
    private final MessageTable messageTable;
    private final BlockingQueue<Runnable> taskQueue;
    private final RoomUser bot;


    public AIRoom(String name) {
        this.name = name;
        this.userMap = new HashMap<>();
        this.messageTable = new SyncMessageTable();
        this.taskQueue = new LinkedBlockingQueue<>();
        this.bot = new RoomUser(null, "Bot", this, null);

        Thread.ofVirtual().name("AI-Thread").start(this::processTasks);
    }

    private void processTasks() {
        try {
            while (true) {
                Runnable task = taskQueue.take();
                task.run();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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
        RoomUser currentUser = userMap.get(user.getName());
        if (currentUser != null)
            return Optional.of(currentUser);

        RoomUser newUser = new RoomUser(user.getThread(), user.getName(), this, user.getToken());
        userMap.put(newUser.getName(), newUser);
        return Optional.of(newUser);
    }

    @Override
    public Optional<User> disconnectUser(RoomUser user) {
        RoomUser removedUser = userMap.remove(user.getName());
        if (removedUser == null)
            return Optional.empty();

        User newUser = new User(user.getThread(), user.getName(), user.getToken());
        return Optional.of(newUser);
    }

    @Override
    public Message addMessage(String content, RoomUser author) {
        Message message = messageTable.add(author, content);
        
        String prompt = buildPrompt();

        taskQueue.add(() -> {
            String aiResponse = getAiResponse(prompt);
            if (aiResponse != null) {
                broadcastMessage(aiResponse);
            }
        });

        return message;
    }

    @Override
    public List<Message> getMessages() {
        return messageTable.getAll();
    }

    @Override
    public List<Message> getMessages(int firstId) {
        return messageTable.getFrom(firstId);
    }

    private String getAiResponse(String prompt) {
        try {
            String jsonBody = """
                {
                    "model": "llama3",
                    "prompt": "%s",
                    "stream": false
                }
            """.formatted(prompt);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:11434/api/generate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200)
                return null;
            
            String body = response.body();
            Pattern p = Pattern.compile("\"response\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL);
            Matcher m = p.matcher(body);
            if (!m.find())
                return null;

            String answer = m.group(1).strip();

            return answer;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String buildPrompt() {
        List<Message> recent = messageTable.getLast(WINDOW);
        StringBuilder prompt = new StringBuilder();

        for (Message message: recent) {
            prompt.append(String.format("%s:%s;", message.getUsername(), message.getContent()));
        }

        return prompt.toString();
    }

    private void broadcastMessage(String content) {
        Message message = messageTable.add(bot, content);
        
        for (RoomUser user : getOnlineUsers()) {
            MessageQueue userQueue = user.getThread().getMessageQueue();
            userQueue.push(message);
        }
    }
}
