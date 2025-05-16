package protocol;

import java.util.Optional;
import protocol.unit.*;

public interface DefaultProtocolVisitor extends ProtocolVisitor {
    Optional<ProtocolUnit> visitDefault(ProtocolUnit unit);

    @Override
    default Optional<ProtocolUnit> visit(RegisterUnit unit) {
        return visitDefault(unit);
    }

    @Override
    default Optional<ProtocolUnit> visit(LoginUnit unit) {
        return visitDefault(unit);
    }

    @Override
    default Optional<ProtocolUnit> visit(LogoutUnit unit) {
        return visitDefault(unit);
    }

    @Override
    default Optional<ProtocolUnit> visit(EnterUnit unit) {
        return visitDefault(unit);
    }

    @Override
    default Optional<ProtocolUnit> visit(LeaveUnit unit) {
        return visitDefault(unit);
    }

    @Override
    default Optional<ProtocolUnit> visit(SendUnit unit) {
        return visitDefault(unit);
    }

    @Override
    default Optional<ProtocolUnit> visit(OkUnit unit) {
        return visitDefault(unit);
    }

    @Override
    default Optional<ProtocolUnit> visit(ErrUnit unit) {
        return visitDefault(unit);
    }

    @Override
    default Optional<ProtocolUnit> visit(InvalidUnit unit) {
        return visitDefault(unit);
    }

    @Override
    default Optional<ProtocolUnit> visit(EofUnit unit) {
        return visitDefault(unit);
    }

    @Override
    default Optional<ProtocolUnit> visit(AuthTokenUnit unit) {
        return visitDefault(unit);
    }
}
