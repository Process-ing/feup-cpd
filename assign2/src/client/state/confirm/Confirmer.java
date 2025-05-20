package client.state.confirm;

import client.state.ClientState;
import protocol.DefaultArgedProtocolVisitor;
import protocol.unit.OkUnit;
import protocol.unit.ProtocolUnit;

public abstract class Confirmer<StateT extends ClientState> implements DefaultArgedProtocolVisitor<Void, OkUnit> {
    private final StateT state;

    public Confirmer(StateT state) {
        this.state = state;
    }

    public StateT getState() {
        return state;
    }

    @Override
    public Void visitDefault(ProtocolUnit unit, OkUnit confirmation) {
        return null;
    }
}
