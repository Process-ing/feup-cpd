package protocol;

import java.util.List;

public interface TokenList {
    String getCommand();
    List<String> getTokens();
    String getToken(int index);
    int getNumTokens();
}
