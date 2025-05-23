package client.state;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import client.BaseClient;
import client.Cli;
import protocol.ProtocolPort;
import protocol.unit.ProtocolUnit;
import protocol.unit.SendUnit;

public class UnstableBotState extends BotState {
    private final List<String> messages;
    private final int period;
    private final double faultRate;
    private final Random rng;

    public UnstableBotState(BaseClient client, List<String> messages, int period, double failureRate) {
        this(client, messages, period, failureRate, -1);
    }

    public UnstableBotState(BaseClient client, List<String> messages, int period, double failureRate, int lastId) {
        super(client, lastId);

        this.messages = messages;
        this.period = period;
        this.faultRate = failureRate;
        this.rng = new Random();
    }

    @Override
    public Optional<ProtocolUnit> buildNextUnit() {
        try {
            Thread.sleep(period);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (rng.nextDouble() < faultRate) {  // If test succeeds, close connection to simulate a connection fault
            ProtocolPort protocolPort = getClient().getPort();
            try {
                protocolPort.close();
                Cli.printWarning("Simulating connection fault...");
            } catch (IOException e) {
                Cli.printError("Failed to mimic connection fault: " + e.getMessage());
            }
        }

        int messageIndex = rng.nextInt(messages.size());
        String message = messages.get(messageIndex);

        return Optional.of(new SendUnit(message));
    }
}
