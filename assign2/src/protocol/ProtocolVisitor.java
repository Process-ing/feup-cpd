package protocol;

import java.util.Optional;
import protocol.unit.*;

public interface ProtocolVisitor {
    Optional<ProtocolUnit> visit(RegisterUnit unit);

    Optional<ProtocolUnit> visit(LoginUnit unit);

    Optional<ProtocolUnit> visit(LogoutUnit unit);

    Optional<ProtocolUnit> visit(EnterUnit unit);

    Optional<ProtocolUnit> visit(LeaveUnit unit);

    Optional<ProtocolUnit> visit(SendUnit unit);

    Optional<ProtocolUnit> visit(RecvUnit unit);

    Optional<ProtocolUnit> visit(SyncUnit unit);

    Optional<ProtocolUnit> visit(OkUnit unit);

    Optional<ProtocolUnit> visit(ErrUnit unit);

    Optional<ProtocolUnit> visit(InvalidUnit unit);

    Optional<ProtocolUnit> visit(EofUnit unit);
    Optional<ProtocolUnit> visit(AuthTokenUnit unit);
}
