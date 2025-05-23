package client.state;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import client.BaseClient;
import protocol.unit.ProtocolUnit;
import protocol.unit.SendUnit;

public class RandomIntervalBotState extends BotState {
    private final List<String> messages;
    private final int maxPeriod;
    private final Random rng;

    public RandomIntervalBotState(BaseClient client, List<String> messages, int maxPeriod) {
        this(client, messages, maxPeriod, -1);
    }

    public RandomIntervalBotState(BaseClient client, List<String> messages, int maxPeriod, int lastId) {
        super(client, lastId);

        this.messages = messages;
        this.maxPeriod = maxPeriod;
        this.rng = new Random();
    }

    @Override
    public Optional<ProtocolUnit> buildNextUnit() {
        try {
            Thread.sleep(rng.nextLong(0, maxPeriod));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int messageIndex = rng.nextInt(messages.size());
        String message = messages.get(messageIndex);

        return Optional.of(new SendUnit(message));
    }
}
