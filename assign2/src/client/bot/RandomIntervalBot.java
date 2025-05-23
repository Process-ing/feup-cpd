package client.bot;

import java.util.Random;

import client.state.ClientState;
import client.state.RandomIntervalBotState;
import client.storage.SessionStore;
import protocol.ProtocolParser;
import protocol.ProtocolParserImpl;
import protocol.ProtocolPort;

public class RandomIntervalBot extends BaseBot {
    private static final int MAX_BOT = 255;
    private static final String NAME_PREFIX = "ribot-";
    private static final String DEFAULT_PASSWORD = "password";
    private static final int DEFAULT_MAX_PERIOD = 2000;

    private final int maxPeriod;

    public RandomIntervalBot(ProtocolPort protocolPort, ProtocolParser parser, SessionStore session, String password, int maxPeriod) {
        super(protocolPort, parser, session, password);

        this.maxPeriod = maxPeriod;
    }

    @Override
    protected ClientState getTargetState(int syncId) {
        return new RandomIntervalBotState(this, BotMessages.MESSAGES, maxPeriod, syncId);
    }

    private static void printUsage() {
        System.out.println("Usage: java client.bot.RandomIntervalBot <room>");
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

        RandomIntervalBot bot = new RandomIntervalBot(protocolPort, parser, session, DEFAULT_PASSWORD, DEFAULT_MAX_PERIOD);
        bot.run();
    }
}
