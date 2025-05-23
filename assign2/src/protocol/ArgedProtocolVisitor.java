package protocol;

import protocol.unit.TokenLoginUnit;
import protocol.unit.EnterUnit;
import protocol.unit.EofUnit;
import protocol.unit.ErrUnit;
import protocol.unit.InvalidUnit;
import protocol.unit.LeaveUnit;
import protocol.unit.LoginUnit;
import protocol.unit.LogoutUnit;
import protocol.unit.OkUnit;
import protocol.unit.PingUnit;
import protocol.unit.PongUnit;
import protocol.unit.RecvUnit;
import protocol.unit.RegisterUnit;
import protocol.unit.SendUnit;
import protocol.unit.SyncUnit;

public interface ArgedProtocolVisitor<R, A> {
    R visit(RegisterUnit unit, A arg);

    R visit(LoginUnit unit, A arg);

    R visit(LogoutUnit unit, A arg);

    R visit(EnterUnit unit, A arg);

    R visit(LeaveUnit unit, A arg);

    R visit(SendUnit unit, A arg);

    R visit(RecvUnit unit, A arg);

    R visit(SyncUnit unit, A arg);

    R visit(OkUnit unit, A arg);

    R visit(ErrUnit unit, A arg);

    R visit(InvalidUnit unit, A arg);

    R visit(EofUnit unit, A arg);

    R visit(TokenLoginUnit unit, A arg);

    R visit(PingUnit unit, A arg);

    R visit(PongUnit unit, A arg);
}