package protocol;

import java.util.HashMap;
import java.util.Map;

@FunctionalInterface
interface Handler<T> {
    boolean apply(T entity, TokenList list);
}

public abstract class ProtocolHandler<T> {
    private Map<String, Handler<T>> handlers;

    public ProtocolHandler() {
        this.handlers = new HashMap<>();
        addHandlers();
    }

    protected abstract void addHandlers();

    protected void addHandler(String command, Handler<T> handler) {
        handlers.put(command, handler);
    }

    private Boolean defaultHandler(TokenList list) {
        return false;
    }

    public boolean handle(T entity, TokenList list) {
        var handler = handlers.get(list.getCommand());
        return handler != null ? handler.apply(entity, list) : defaultHandler(list);
    }
}
