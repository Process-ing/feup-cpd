package client.state;

import java.util.Optional;

import client.BaseClient;
import client.Cli;
import protocol.unit.ProtocolUnit;
import protocol.unit.RecvUnit;
import protocol.unit.SyncUnit;

public abstract class BotState extends NonInteractiveState implements SynchronizableState {
    private int lastId;

    public BotState(BaseClient client, int lastId) {
        super(client);

        this.lastId = lastId;
    }

    @Override
    public Optional<ProtocolUnit> visit(RecvUnit unit) {
        String username = getClient().getSession().getUsername();
        if (unit.id() == lastId + 1 || lastId == -1) {
            Cli.printMessage(unit.username(), unit.message(), unit.username().equals(username));
            lastId = unit.id();

            return Optional.empty();
        }

        if (unit.id() > lastId + 1) { // Missing messages
            return Optional.of(getSyncUnit());
        }

        // Messages already received, ignore
        return Optional.empty();
    }

    @Override
    public int getSyncId() {
        return lastId;
    }

    @Override
    public void setSyncId(int syncId) {
        this.lastId = syncId;
    }

    @Override
    public SyncUnit getSyncUnit() {
        return new SyncUnit(lastId);
    }
}
