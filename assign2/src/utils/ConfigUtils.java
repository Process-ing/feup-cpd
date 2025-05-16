package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class ConfigUtils {
    public static Properties loadConfig(String filepath) throws IOException {
        FileInputStream fis = new FileInputStream(filepath);
        Properties properties = new Properties();
        properties.load(fis);

        return properties;
    }

    public static void saveConfig(String filepath, Properties properties) throws IOException {
        try (var outputStream = new FileOutputStream(filepath)) {
            properties.store(outputStream, null);
        }
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
