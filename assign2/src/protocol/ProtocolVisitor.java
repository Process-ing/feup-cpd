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
    Optional<ProtocolUnit> visit(OkUnit unit);
    Optional<ProtocolUnit> visit(ErrUnit unit);
}
