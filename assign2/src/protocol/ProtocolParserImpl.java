package protocol;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import protocol.unit.AuthTokenUnit;
import protocol.unit.EnterUnit;
import protocol.unit.EofUnit;
import protocol.unit.ErrUnit;
import protocol.unit.InvalidUnit;
import protocol.unit.LeaveUnit;
import protocol.unit.LoginUnit;
import protocol.unit.LogoutUnit;
import protocol.unit.OkUnit;
import protocol.unit.ProtocolUnit;
import protocol.unit.RegisterUnit;
import protocol.unit.SendUnit;
import protocol.unit.RecvUnit;

@FunctionalInterface
interface ParseHandler {
    ProtocolUnit apply(List<String> args);
}

public class ProtocolParserImpl implements ProtocolParser {
    private final Map<String, ParseHandler> handlerMap;

    public ProtocolParserImpl() {
        this.handlerMap = Map.ofEntries(
            Map.entry("login-token", this::buildAuthToken),
            Map.entry("login", this::buildLogin),
            Map.entry("register", this::buildRegister),
            Map.entry("logout", this::buildLogout),
            Map.entry("enter", this::buildEnter),
            Map.entry("leave", this::buildLeave),
            Map.entry("send", this::buildSend),
            Map.entry("recv", this::buildRecv),
            Map.entry("ok", this::buildOk),
            Map.entry("err", this::buildErr)
        );
    }

    @Override
    public ProtocolUnit parse(String string) {
        if (string == null || string.isEmpty())
            return new EofUnit();

        string = string.strip();

        int firstSpaceIndex = string.indexOf(' ');
        if (firstSpaceIndex == -1)
            firstSpaceIndex = string.length();

        String command = string.substring(0, firstSpaceIndex);
        ParseHandler handler = handlerMap.get(command);
        if (handler == null)
            return new InvalidUnit();

        String argString = string.substring(firstSpaceIndex).strip();
        List<String> args = ProtocolUtils.tokenize(argString);
        return handler.apply(args);
    }

    private ProtocolUnit buildLogin(List<String> args) {
        if (args.size() != 2)
            return new InvalidUnit();

        String user = args.get(0);
        String pass = args.get(1);

        return new LoginUnit(user, pass);
    }

    private ProtocolUnit buildRegister(List<String> args) {
        if (args.size() != 2)
            return new InvalidUnit();

        String user = args.get(0);
        String pass = args.get(1);

        return new RegisterUnit(user, pass);
    }

    private ProtocolUnit buildLogout(List<String> args) {
        if (args.size() != 0)
            return new InvalidUnit();

        return new LogoutUnit();
    }

    private ProtocolUnit buildEnter(List<String> args) {
        if (args.size() != 1)
            return new InvalidUnit();

        String roomName = args.get(0);

        return new EnterUnit(roomName);
    }

    private ProtocolUnit buildLeave(List<String> args) {
        if (args.size() != 0)
            return new InvalidUnit();

        return new LeaveUnit();
    }

    private ProtocolUnit buildSend(List<String> args) {
        if (args.size() != 1)
            return new InvalidUnit();

        String message = args.get(0);

        return new SendUnit(message);
    }

    private ProtocolUnit buildRecv(List<String> args) {
        if (args.size() != 2)
            return new InvalidUnit();

        String username = args.get(0);
        String message = args.get(1);

        return new RecvUnit(username, message);
    }

    private ProtocolUnit buildOk(List<String> args) {
        if (args.size() != 1)
            return new InvalidUnit();

        String data = args.get(0);

        return new OkUnit(data);
    }

    private ProtocolUnit buildErr(List<String> args) {
        if (args.size() != 1)
            return new InvalidUnit();

        Optional<ProtocolErrorIdentifier> id = ProtocolErrorIdentifier.fromString(args.get(0));
        if (id.isEmpty())
            return new InvalidUnit();

        return new ErrUnit(id.get());
    }

    private ProtocolUnit buildAuthToken(List<String> args) {
        if (args.size() != 1)
            return new InvalidUnit();

        String token = args.get(0);

        return new AuthTokenUnit(token);
    }
}
