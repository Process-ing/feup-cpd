package client;

import client.state.ClientState;
import client.state.GuestState;
import client.state.ReloginState;
import client.storage.SessionStore;
import protocol.ProtocolParser;
import protocol.ProtocolParserImpl;
import protocol.ProtocolPort;

public class Client extends BaseClient {
    public Client(ProtocolPort protocolPort, ProtocolParser parser, SessionStore session) {
        super(protocolPort, parser, session);
    }

    @Override
    protected ClientState getInitialState() {
        return getSession().getToken() != null
            ? new ReloginState(this, getState())
            : new GuestState(this);
    }

    private static void printUsage() {
        System.out.println("Usage: java client.Client [<session-suffix>]");
    }

    public static void main(String[] args) {
        if (args.length > 1) {
            printUsage();
            return;
        }

        String sessionSuffix = args.length == 1 ? "-" + args[0] : "";
        SessionStore session = loadSession(sessionSuffix)
            .orElseThrow(() -> new RuntimeException("Failed to load session"));

        ProtocolPort protocolPort = getProtocolPort()
            .orElseThrow(() -> new RuntimeException("Failed to create protocol port"));

        ProtocolParser parser = new ProtocolParserImpl();

        Client client = new Client(protocolPort, parser, session);
        client.run();
    }
}
