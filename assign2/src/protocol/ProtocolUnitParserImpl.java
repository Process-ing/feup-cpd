package protocol;

import java.util.List;
import java.util.Map;

import protocol.unit.EnterUnit;
import protocol.unit.ErrUnit;
import protocol.unit.InvalidUnit;
import protocol.unit.LeaveUnit;
import protocol.unit.LoginUnit;
import protocol.unit.LogoutUnit;
import protocol.unit.OkUnit;
import protocol.unit.ProtocolUnit;
import protocol.unit.RegisterUnit;
import protocol.unit.SendUnit;

@FunctionalInterface
interface ParseHandler {
    ProtocolUnit apply(List<String> tokens);
}

public class ProtocolUnitParserImpl implements ProtocolUnitParser {
    private final Map<String, ParseHandler> handlerMap;

    public ProtocolUnitParserImpl() {
        this.handlerMap = Map.ofEntries(
            Map.entry("login", this::buildLogin),
            Map.entry("register", this::buildRegister),
            Map.entry("logout", this::buildLogout),
            Map.entry("enter", this::buildEnter),
            Map.entry("leave", this::buildLeave),
            Map.entry("send", this::buildSend),
            Map.entry("ok", this::buildOk),
            Map.entry("err", this::buildErr)
        );
    }

    @Override
    public ProtocolUnit parse(String string) {
        string = string.strip();

        var firstSpaceIndex = string.indexOf(' ');
        if (firstSpaceIndex == -1)
            return new InvalidUnit();

        var command = string.substring(0, firstSpaceIndex);
        var handler = handlerMap.get(command);
        if (handler == null)
            return new InvalidUnit();

        var tokens = ProtocolUtils.tokenize(string.substring(firstSpaceIndex + 1));
        return handler.apply(tokens);
    }

    private ProtocolUnit buildLogin(List<String> tokens) {
        if (tokens.size() != 2)
            return new InvalidUnit();

        var user = tokens.get(0);
        var pass = tokens.get(1);

        return new LoginUnit(user, pass);
    }

    private ProtocolUnit buildRegister(List<String> tokens) {
        if (tokens.size() != 2)
            return new InvalidUnit();

        var user = tokens.get(0);
        var pass = tokens.get(1);

        return new RegisterUnit(user, pass);
    }

    private ProtocolUnit buildLogout(List<String> tokens) {
        if (tokens.size() != 0)
            return new InvalidUnit();
        return new LogoutUnit();
    }

    private ProtocolUnit buildEnter(List<String> tokens) {
        if (tokens.size() != 1)
            return new InvalidUnit();

        var roomName = tokens.get(0);

        return new EnterUnit(roomName);
    }

    private ProtocolUnit buildLeave(List<String> tokens) {
        if (tokens.size() != 0)
            return new InvalidUnit();

        return new LeaveUnit();
    }

    private ProtocolUnit buildSend(List<String> tokens) {
        if (tokens.size() != 1)
            return new InvalidUnit();

        var message = tokens.get(0);
        return new SendUnit(message);
    }

    private ProtocolUnit buildOk(List<String> tokens) {
        if (tokens.size() != 1)
            return new InvalidUnit();

        var data = tokens.get(0);

        return new OkUnit(data);
    }

    private ProtocolUnit buildErr(List<String> tokens) {
        if (tokens.size() != 1)
            return new InvalidUnit();

        var id = ProtocolErrorIdentifier.fromString(tokens.get(0));
        if (id.isEmpty())
            return new InvalidUnit();

        return new ErrUnit(id.get());
    }
}
