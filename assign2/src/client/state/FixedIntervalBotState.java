package client.state;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import client.BaseClient;
import protocol.unit.ProtocolUnit;
import protocol.unit.SendUnit;

public class FixedIntervalBotState extends BotState {
    private final Random rng;
    private final List<String> messages;
    private final int period;

    public FixedIntervalBotState(BaseClient client, List<String> messages, int period) {
        this(client, messages, period, -1);
    }

    public FixedIntervalBotState(BaseClient client, List<String> messages, int period, int lastId) {
        super(client, lastId);

        this.messages = messages;
        this.rng = new Random();
        this.period = period;
    }

    @Override
    public Optional<ProtocolUnit> buildNextUnit() {
        try {
            Thread.sleep(period);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int messageIndex = rng.nextInt(messages.size());
        String message = messages.get(messageIndex);

        return Optional.of(new SendUnit(message));
    }
}
