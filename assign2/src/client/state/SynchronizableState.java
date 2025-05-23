package client.state;

import protocol.unit.SyncUnit;

public interface SynchronizableState {
    SyncUnit getSyncUnit();
}
