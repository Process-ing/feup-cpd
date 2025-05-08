package structs.storage;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import structs.CredentialRecord;

public final class PasswordHasher {

    private static final String ALG = "PBKDF2WithHmacSHA256";
    private static final int ITER     = 10_000;
    private static final int KEY_LEN  = 256;
    private static final int SALT_LEN = 16;
    private static final SecureRandom RNG = new SecureRandom();
    private static final HexFormat HEX = HexFormat.of();

    private PasswordHasher() { }

    public static CredentialRecord hash(char[] password) {
        byte[] salt = new byte[SALT_LEN];
        RNG.nextBytes(salt);
        byte[] hash = derive(password, salt);
        return new CredentialRecord(HEX.formatHex(salt), HEX.formatHex(hash));
    }

    public static boolean verify(char[] password, CredentialRecord rec) {
        byte[] salt = HEX.parseHex(rec.saltHex());
        byte[] expected = HEX.parseHex(rec.hashHex());
        byte[] actual = derive(password, salt);
        return MessageDigest.isEqual(expected, actual);
    }

    private static byte[] derive(char[] pw, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(pw, salt, ITER, KEY_LEN);
            SecretKeyFactory f = SecretKeyFactory.getInstance(ALG);
            return f.generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 failure", e);
        }
    }
}
