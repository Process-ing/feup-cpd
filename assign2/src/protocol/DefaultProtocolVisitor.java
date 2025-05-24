package protocol;

import protocol.unit.*;

public interface DefaultProtocolVisitor<T> extends ProtocolVisitor<T> {
    T visitDefault(ProtocolUnit unit);

    @Override
    default T visit(RegisterUnit unit) {
        return visitDefault(unit);
    }

    @Override
    default T visit(LoginUnit unit) {
        return visitDefault(unit);
    }

    @Override
    default T visit(LogoutUnit unit) {
        return visitDefault(unit);
    }

    @Override
    default T visit(ListRoomsUnit unit) {
        return visitDefault(unit);
    }

    @Override
    default T visit(EnterUnit unit) {
        return visitDefault(unit);
    }

    @Override
    default T visit(LeaveUnit unit) {
        return visitDefault(unit);
    }

    @Override
    default T visit(SendUnit unit) {
        return visitDefault(unit);
    }

    @Override
    default T visit(RecvUnit unit) {
        return visitDefault(unit);
    }

    @Override
    default T visit(SyncUnit unit) {
        return visitDefault(unit);
    }

    @Override
    default T visit(OkUnit unit) {
        return visitDefault(unit);
    }

    @Override
    default T visit(ErrUnit unit) {
        return visitDefault(unit);
    }

    @Override
    default T visit(InvalidUnit unit) {
        return visitDefault(unit);
    }

    @Override
    default T visit(EofUnit unit) {
        return visitDefault(unit);
    }

    @Override
    default T visit(TokenLoginUnit unit) {
        return visitDefault(unit);
    }

    @Override
    default T visit(PingUnit unit) {
        return visitDefault(unit);
    }

    @Override
    default T visit(PongUnit unit) {
        return visitDefault(unit);
    }
}
