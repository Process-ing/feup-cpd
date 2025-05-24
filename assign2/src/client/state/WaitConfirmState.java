package client.state;

import java.util.Optional;

import client.BaseClient;
import protocol.unit.ErrUnit;
import protocol.unit.OkUnit;
import protocol.unit.ProtocolUnit;

public abstract class WaitConfirmState extends NonInteractiveState {
    private boolean unitSent;

    public WaitConfirmState(BaseClient client) {
        super(client);

        this.unitSent = false;
    }

    protected abstract ProtocolUnit buildUnitToSend();
    protected abstract ClientState getStateOnConfirm();
    protected abstract boolean handleError(ErrUnit unit);

    @Override
    public Optional<ProtocolUnit> buildNextUnit() {
        if (unitSent)
            return Optional.empty();

        unitSent = true;
        return Optional.of(buildUnitToSend());
    }

    @Override
    public Optional<ProtocolUnit> visit(OkUnit unit) {
        BaseClient client = getClient();
        client.setState(getStateOnConfirm());

        return Optional.empty();
    }

    @Override
    public Optional<ProtocolUnit> visit(ErrUnit unit) {
        return handleError(unit)
            ? Optional.empty()
            : visitDefault(unit);
    }

    @Override
    public Optional<ProtocolUnit> visitDefault(ProtocolUnit unit) {
        return Optional.of(buildUnitToSend());
    }
}
