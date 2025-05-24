package protocol;

import protocol.unit.TokenLoginUnit;
import protocol.unit.EnterUnit;
import protocol.unit.EofUnit;
import protocol.unit.ErrUnit;
import protocol.unit.InvalidUnit;
import protocol.unit.LeaveUnit;
import protocol.unit.ListRoomsUnit;
import protocol.unit.LoginUnit;
import protocol.unit.LogoutUnit;
import protocol.unit.OkUnit;
import protocol.unit.PingUnit;
import protocol.unit.PongUnit;
import protocol.unit.ProtocolUnit;
import protocol.unit.RecvUnit;
import protocol.unit.RegisterUnit;
import protocol.unit.SendUnit;
import protocol.unit.SyncUnit;

public interface DefaultArgedProtocolVisitor<R, A> extends ArgedProtocolVisitor<R, A> {
    R visitDefault(ProtocolUnit unit, A arg);

    @Override
    default R visit(RegisterUnit unit, A arg) {
        return visitDefault(unit, arg);
    }

    @Override
    default R visit(LoginUnit unit, A arg) {
        return visitDefault(unit, arg);
    }

    @Override
    default R visit(LogoutUnit unit, A arg) {
        return visitDefault(unit, arg);
    }

    @Override
    default R visit(ListRoomsUnit unit, A arg) {
        return visitDefault(unit, arg);
    }

    @Override
    default R visit(EnterUnit unit, A arg) {
        return visitDefault(unit, arg);
    }

    @Override
    default R visit(LeaveUnit unit, A arg) {
        return visitDefault(unit, arg);
    }

    @Override
    default R visit(SendUnit unit, A arg) {
        return visitDefault(unit, arg);
    }

    @Override
    default R visit(RecvUnit unit, A arg) {
        return visitDefault(unit, arg);
    }

    @Override
    default R visit(SyncUnit unit, A arg) {
        return visitDefault(unit, arg);
    }

    @Override
    default R visit(OkUnit unit, A arg) {
        return visitDefault(unit, arg);
    }

    @Override
    default R visit(ErrUnit unit, A arg) {
        return visitDefault(unit, arg);
    }

    @Override
    default R visit(InvalidUnit unit, A arg) {
        return visitDefault(unit, arg);
    }

    @Override
    default R visit(EofUnit unit, A arg) {
        return visitDefault(unit, arg);
    }

    @Override
    default R visit(TokenLoginUnit unit, A arg) {
        return visitDefault(unit, arg);
    }

    @Override
    default R visit(PingUnit unit, A arg) {
        return visitDefault(unit, arg);
    }

    @Override
    default R visit(PongUnit unit, A arg) {
        return visitDefault(unit, arg);
    }
}
