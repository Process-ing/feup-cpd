package client.bot;

import java.util.Random;

import client.state.ClientState;
import client.state.UnstableBotState;
import client.storage.SessionStore;
import protocol.ProtocolParser;
import protocol.ProtocolParserImpl;
import protocol.ProtocolPort;

public class UnstableBot extends BaseBot {
    private static final int MAX_BOT = 255;
    private static final String NAME_PREFIX = "ubot-";
    private static final String DEFAULT_PASSWORD = "password";
    private static final int DEFAULT_PERIOD = 1000;
    private static final double DEFAULT_FAILURE_RATE = 0.10; // 10% failure rate

    private final int period;
    private final double failureRate;

    public UnstableBot(ProtocolPort protocolPort, ProtocolParser parser, SessionStore session, String password, int period, double failureRate) {
        super(protocolPort, parser, session, password);

        this.period = period;
        this.failureRate = failureRate;
    }

    @Override
    protected ClientState getTargetState(int syncId) {
        return new UnstableBotState(this, BotMessages.MESSAGES, period, failureRate, syncId);
    }

    private static void printUsage() {
        System.out.println("Usage: java client.bot.UnstableBot <room>");
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            printUsage();
            return;
        }

        SessionStore session = new SessionStore();
        session.setUsername(NAME_PREFIX + new Random().nextInt(MAX_BOT));
        session.setRoom(args[0]);

        ProtocolPort protocolPort = getProtocolPort()
                .orElseThrow(() -> new RuntimeException("Failed to create protocol port"));
        ProtocolParser parser = new ProtocolParserImpl();

        UnstableBot bot = new UnstableBot(protocolPort, parser, session, DEFAULT_PASSWORD, DEFAULT_PERIOD, DEFAULT_FAILURE_RATE);
        bot.run();
    }
}
