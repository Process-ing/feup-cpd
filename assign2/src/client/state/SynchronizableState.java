package client.state;

import protocol.unit.SyncUnit;

public interface SynchronizableState {
    int getSyncId();
    void setSyncId(int syncId);
    SyncUnit getSyncUnit();
}
