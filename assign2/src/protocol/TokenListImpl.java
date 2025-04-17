package protocol;

import java.util.List;

import exception.ProtocolException;

public class TokenListImpl implements TokenList {
    private List<String> tokens;

    public TokenListImpl(String message) {
        tokens = List.of(message.split("\\s+"));

        if (tokens.size() == 0)
            throw new ProtocolException("Empty message cannot be parsed");
    }

    @Override
    public String getCommand() {
        return tokens.get(0);
    }

    @Override
    public List<String> getTokens() {
        return tokens;
    }

    @Override
    public String getToken(int index) {
        return tokens.get(index);
    }

    @Override
    public int getNumTokens() {
        return tokens.size();
    }
}
