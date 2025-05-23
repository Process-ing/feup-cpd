package server;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import protocol.ProtocolPort;
import protocol.unit.EofUnit;
import protocol.unit.ProtocolUnit;
import protocol.unit.RecvUnit;
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
                Optional<Message> pendingMessage = queue.pop();
                if (pendingMessage.isPresent()) {
                    ProtocolUnit unit = new RecvUnit(pendingMessage.get());

                    port.send(unit);
                    logResponse(unit);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private void handleReceiving() {
        try {
            while (!done) {
                ProtocolUnit request = port.receive();
                logRequest(request);
                if (request instanceof EofUnit)
                    break;

                Optional<ProtocolUnit> response = request.accept(client);
                if (response.isPresent()) {
                    port.send(response.get());
                    logResponse(response.orElse(null));
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private void logRequest(ProtocolUnit request) {
        LocalDateTime now = LocalDateTime.now();
        System.out.printf("[%s - %s] < %s\n", now, client, request.serialize());
    }

    private void logResponse(ProtocolUnit response) {
        LocalDateTime now = LocalDateTime.now();
        System.out.printf("[%s - %s] > %s\n", now, client, response.serialize());
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
