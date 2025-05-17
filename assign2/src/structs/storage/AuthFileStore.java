package structs.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import structs.CredentialRecord;

public final class AuthFileStore {
    private final Path path;
    private final String NAME_SEP = ":";
    private final String REGEX_PWD_SEP = "\\$";
    private final String FILE_PWD_SEP = "$";

    public AuthFileStore(Path path) {
        this.path = path;
    }

    public Map<String, CredentialRecord> load() throws IOException {
        if (!Files.exists(path)) {
            Path parent = path.getParent();
            if (parent != null)
                Files.createDirectories(path.getParent());

            Files.createFile(path);
            return Map.of();
        }

        Map<String, CredentialRecord> out = new HashMap<>();

        try (Stream<String> lines = Files.lines(path)) {
            lines.map(String::trim)
                .filter(line -> !line.isBlank())
                .forEach(line -> {
                    String[] userPass = line.split(NAME_SEP, 2);
                    if (userPass.length != 2)
                        throw new IllegalStateException("Malformed entry: " + line);

                    String[] saltHash = userPass[1].split(REGEX_PWD_SEP, 2);
                    if (saltHash.length != 2)
                        throw new IllegalStateException("Missing salt/hash: " + line);

                    out.put(userPass[0], new CredentialRecord(saltHash[0], saltHash[1]));
                });
        }

        return Collections.unmodifiableMap(out);
    }

    public void append(String user, CredentialRecord rec) throws IOException {
        String line = user + NAME_SEP +
            rec.saltHex() + FILE_PWD_SEP +
            rec.hashHex() + System.lineSeparator();

        Files.writeString(path, line, StandardOpenOption.APPEND);
    }
}
