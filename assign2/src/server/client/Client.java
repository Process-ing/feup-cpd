package server.client;

import protocol.ProtocolHandler;
import server.ClientThread;

public abstract class Client implements ProtocolHandler {
    private final ClientThread thread;

    public Client(ClientThread thread) {
        this.thread = thread;
    }

    public ClientThread getThread() {
        return thread;
    }
}
