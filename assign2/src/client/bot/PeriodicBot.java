package client.bot;

import java.util.Random;

import client.Client;
import client.state.BotLoginState;
import client.state.ClientState;
import client.state.PeriodicBotState;
import client.storage.SessionStore;
import protocol.ProtocolParser;
import protocol.ProtocolParserImpl;
import protocol.ProtocolPort;

public class PeriodicBot extends Client {
    private static final int MAX_BOT = 255;
    private static final String NAME_PREFIX = "pbot-";
    private static final String DEFAULT_PASSWORD = "password";
    private static final int DEFAULT_PERIOD = 1000;

    private final String password;

    public PeriodicBot(ProtocolPort protocolPort, ProtocolParser parser, SessionStore session, String password) {
        super(protocolPort, parser, session);

        this.password = password;
    }

    @Override
    protected ClientState getInitialState() {
        ClientState targetState = new PeriodicBotState(this, BotMessages.MESSAGES, DEFAULT_PERIOD);
        return new BotLoginState(this, password, targetState);
    }

    private static void printUsage() {
        System.out.println("Usage: java client.bot.PeriodicBot <room>");
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

        PeriodicBot bot = new PeriodicBot(protocolPort, parser, session, DEFAULT_PASSWORD);
        bot.run();
    }
}
