package client.state;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import client.BaseClient;
import client.Cli;
import protocol.unit.ProtocolUnit;
import protocol.unit.RecvUnit;
import protocol.unit.SendUnit;
import protocol.unit.SyncUnit;

public class PeriodicBotState extends NonInteractiveState implements SynchronizableState {
    private final Random rng;
    private final List<String> messages;
    private final int period;
    private int lastId;

    public PeriodicBotState(BaseClient client, List<String> messages, int period) {
        super(client);

        this.messages = messages;
        this.rng = new Random();
        this.period = period;
        this.lastId = -1;
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

    @Override
    public Optional<ProtocolUnit> visit(RecvUnit unit) {
        String username = getClient().getSession().getUsername();
        if (unit.id() == lastId + 1 || lastId == -1) {
            Cli.printMessage(unit.username(), unit.message(), unit.username().equals(username));
            lastId = unit.id();

            return Optional.empty();
        }

        if (unit.id() > lastId + 1) { // Missing messages
            return Optional.of(getSyncUnit());
        }

        // Messages already received, ignore
        return Optional.empty();
    }

    @Override
    public SyncUnit getSyncUnit() {
        return new SyncUnit(lastId);
    }
}
