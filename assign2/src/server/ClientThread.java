package server;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import protocol.ProtocolPort;
import protocol.unit.EofUnit;
import protocol.unit.ProtocolUnit;
import protocol.unit.SendUnit;
import server.client.Client;
import structs.Message;
import structs.MessageQueue;

public class ClientThread {
    private final Server server;
    private final ProtocolPort port;
    private final MessageQueue queue;
    private Client client;
    private boolean done;

    public ClientThread(Server server, ProtocolPort port, MessageQueue queue, Client client) {
        this.server = server;
        this.port = port;
        this.queue = queue;
        this.client = client;
        this.done = false;
    }

    public Server getServer() {
        return server;
    }

    public Client getClient() {
        return client;
    }

    public MessageQueue getMessageQueue() {
        return queue;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void start() {
        Thread.ofVirtual().start(this::handleSending);
        Thread.ofVirtual().start(this::handleReceiving);
    }

    private void handleSending() {
        try {
            while (!done) {
                ProtocolUnit request = port.receive();
                if (request instanceof EofUnit) {
                    System.out.printf("[%s] EOF\n", LocalDateTime.now());
                    break;
                } else {
                    System.out.printf("[%s] %s\n", LocalDateTime.now(), request.serialize());
                }

                Optional<ProtocolUnit> response = request.accept(client);
                if (response.isPresent())
                    port.send(response.get());
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private void handleReceiving() {
        try {
            while (!done) {
                Optional<Message> pendingMessage = queue.pop();
                if (pendingMessage.isPresent()) {
                    ProtocolUnit unit = new SendUnit(pendingMessage.get());
                    port.send(unit);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private void cleanup() {
        if (done)
            return;

        client.cleanup();
        try {
            port.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        done = true;
    }
}
