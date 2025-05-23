package client.bot;

import java.util.Random;

import client.state.ClientState;
import client.state.FixedIntervalBotState;
import client.storage.SessionStore;
import protocol.ProtocolParser;
import protocol.ProtocolParserImpl;
import protocol.ProtocolPort;

public class FixedIntervalBot extends BaseBot {
    private static final int MAX_BOT = 255;
    private static final String NAME_PREFIX = "fibot-";
    private static final String DEFAULT_PASSWORD = "password";
    private static final int DEFAULT_PERIOD = 1000;

    private final int period;

    public FixedIntervalBot(ProtocolPort protocolPort, ProtocolParser parser, SessionStore session, String password, int period) {
        super(protocolPort, parser, session, password);

        this.period = period;
    }

    @Override
    protected ClientState getTargetState(int syncId) {
        return new FixedIntervalBotState(this, BotMessages.MESSAGES, period, syncId);
    }

    private static void printUsage() {
        System.out.println("Usage: java client.bot.FixedIntervalBot <room>");
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

        FixedIntervalBot bot = new FixedIntervalBot(protocolPort, parser, session, DEFAULT_PASSWORD, DEFAULT_PERIOD);
        bot.run();
    }
}
