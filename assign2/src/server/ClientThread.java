package server;

import server.client.Client;

public class ClientThread {
    private final Server server;
    private Client client;

    public ClientThread(Server server, Client client) {
        this.server = server;
        this.client = client;
    }

    public Server getServer() {
        return server;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
