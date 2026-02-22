package passwordmanager.writer;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;

public class HashGenerator {

    public static void main(String[] args) throws Exception {
        String username = "user";
        char[] password = "ZPOIF2025".toCharArray();
        String salt = "salt123";
        int iterations = 10000;
        int keyLength = 256;

        String hash = generateHash(password, salt, iterations, keyLength);

        System.out.printf("%s|%s|%s|%d%n", username, salt, hash, iterations);
    }

    private static String generateHash(char[] password, String salt, int iterations, int keyLength) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password, salt.getBytes(), iterations, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }
}