package protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ProtocolHandler {
    private Map<String, Function<TokenList, Boolean>> handlers;

    public ProtocolHandler() {
        this.handlers = new HashMap<>();
    }

    public void addHandler(String command, Function<TokenList, Boolean> handler) {
        handlers.put(command, handler);
    }

    private Boolean defaultHandler(TokenList list) {
        return false;
    }

    public boolean handle(TokenList list) {
        var handler = handlers.get(list.getCommand());
        return handler != null ? handler.apply(list) : defaultHandler(list);
    }
}
