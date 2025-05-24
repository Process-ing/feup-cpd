package client.state.confirm;

import java.util.HashMap;
import java.util.Map;

import client.state.ClientState;
import protocol.ProtocolOkIdentifier;
import protocol.unit.OkUnit;
import protocol.unit.ProtocolUnit;

@FunctionalInterface
interface ConfirmerVisit {
    void apply(OkUnit unit);
}

public abstract class Confirmer<StateT extends ClientState> {
    private final StateT state;
    private final Map<ProtocolOkIdentifier, ConfirmerVisit> visitMap;

    public Confirmer(StateT state) {
        this.state = state;
        this.visitMap = new HashMap<>();

        buildVisitor();
    }

    public StateT getState() {
        return state;
    }

    protected abstract void buildVisitor();

    protected void addVisit(ProtocolOkIdentifier identifier, ConfirmerVisit visit) {
        visitMap.put(identifier, visit);
    }

    protected void visitDefault(ProtocolUnit unit) {}

    public void visit(OkUnit unit) {
        ProtocolOkIdentifier identifier = unit.id();
        ConfirmerVisit visit = visitMap.get(identifier);

        if (visit != null) {
            visit.apply(unit);
        } else {
            visitDefault(unit);
        }
    }
}
