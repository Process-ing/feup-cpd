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
                tokens.add(ProtocolUtils.unescape(matcher.group(1)));
            } else if (matcher.group(2) != null) {
                tokens.add(matcher.group(2));
            }
        }

        return tokens;
    }

    public static String escapeToken(String token) {      
        return "\"" + ProtocolUtils.escape(token) + "\"";
    }

    public static String escape(String s) {
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\' -> out.append("\\\\");
                case '"'  -> out.append("\\\"");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                default   -> out.append(c);
            }
        }
        
        return out.toString();
    }
    
    public static String unescape(String s) {
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                switch (s.charAt(++i)) {
                    case 'n'  -> out.append('\n');
                    case 'r'  -> out.append('\r');
                    case 't'  -> out.append('\t');
                    case '\\' -> out.append('\\');
                    case '"'  -> out.append('"');
                    default   -> out.append(s.charAt(i));
                }
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }
}
