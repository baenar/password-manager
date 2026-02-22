package passwordmanager.data;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Hasher {
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int KEY_LENGTH = 256;
    private static final int ITERATIONS = 1000;
    private static final String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String lowerCase = "abcdefghijklmnopqrstuvwxyz";
    private static final String digits = "0123456789";
    private static final String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    private static final String allChars = upperCase + lowerCase + digits + specialChars;

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(char[] password, String salt, int iterations)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        PBEKeySpec spec = new PBEKeySpec(password, salt.getBytes(), iterations, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash = skf.generateSecret(spec).getEncoded();

        return Base64.getEncoder().encodeToString(hash);
    }

    public static String generateStrongPassword() {
        SecureRandom random = new SecureRandom();

        StringBuilder password = new StringBuilder();
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        for (int i = 0; i < SettingsData.generatedPasswordLength - 4; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        List<Character> letters = password.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(letters, random);

        return letters.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    public static String createDatabaseFirstLine(String username, String password) {
        String salt = generateSalt();
        String hash;
        try {
            hash = hashPassword(password.toCharArray(), salt, ITERATIONS);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
            return null;
        }

        return String.format("%s|%s|%s|%d",
                username,
                salt,
                hash,
                ITERATIONS
        );    }
}
