package protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProtocolUtils {
    public static List<String> tokenize(String string) {
        string = string.strip();
        List<String> tokens = new ArrayList<>();

        Pattern pattern = Pattern.compile("\"([^\"]*)\"|(\\S+)");
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            tokens.add(matcher.group(matcher.group(1) != null ? 1 : 2));
        }

        return tokens;
    }

    public static String escapeToken(String token) {
        return "\"" + token.replace("\"", "\\\"") + "\"";
    }
}
