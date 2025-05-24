package protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProtocolUtils {
    public static List<String> tokenize(String string) {
        string = string.strip();
        List<String> tokens = new ArrayList<>();

        Pattern pattern = Pattern.compile("\"((?:[^\"\\\\]|\\\\.)*+)\"|([^\\s\"]+)");
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                tokens.add(ProtocolUtils.unescapeSpecials(matcher.group(1)));
            } else if (matcher.group(2) != null) {
                tokens.add(matcher.group(2));
            }
        }

        return tokens;
    }

    public static String escapeToken(String token) {
        return "\"" + ProtocolUtils.escapeSpecials(token) + "\"";
    }

    public static String escapeSpecials(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static String unescapeSpecials(String s) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char next = s.charAt(i + 1);
                switch (next) {
                    case 'n': result.append('\n'); i++; break;
                    case 'r': result.append('\r'); i++; break;
                    case 't': result.append('\t'); i++; break;
                    case '"': result.append('\"'); i++; break;
                    case '\\': result.append('\\'); i++; break;
                    default: result.append(c);
                }
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public static String toKebabCase(String string) {
        return string.toLowerCase().replace('_', '-');
    }
}
