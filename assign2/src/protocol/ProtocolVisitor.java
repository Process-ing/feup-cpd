package protocol;

import protocol.unit.*;

public interface ProtocolVisitor<T> {
    T visit(RegisterUnit unit);

    T visit(LoginUnit unit);

    T visit(LogoutUnit unit);

    T visit(ListRoomsUnit unit);

    T visit(EnterUnit unit);

    T visit(LeaveUnit unit);

    T visit(SendUnit unit);

    T visit(RecvUnit unit);

    T visit(SyncUnit unit);

    T visit(OkUnit unit);

    T visit(ErrUnit unit);

    T visit(InvalidUnit unit);

    T visit(EofUnit unit);

    T visit(TokenLoginUnit unit);

    T visit(PingUnit unit);

    T visit(PongUnit unit);
}
