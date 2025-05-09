package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class ConfigUtils {
    public static Properties loadConfig(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        Properties properties = new Properties();
        properties.load(fis);

        return properties;
    }

    public static List<String> getMissing(Properties properties, List<String> keys) {
        return keys.stream()
                .filter(key -> properties.getProperty(key) == null)
                .toList();
    }

    public static Integer getIntProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null)
            return null;

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
