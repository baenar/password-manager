package passwordmanager.utils;

import lombok.Setter;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class SecurityUtils {

    @Setter
    private static String SECRET_KEY = "DowolnieDlugiKluczUzytkownika123!";


    private static SecretKeySpec getSecretKeySpec(String myKey) {
        try {
            byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            return new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            System.err.println("Error while generating key: " + e);
        }
        return null;
    }

    // Szyfruje tekst jawny do formatu Base64
    public static String encrypt(String strToEncrypt) {
        try {
            SecretKeySpec secretKey = getSecretKeySpec(SECRET_KEY);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.err.println("Error while encrypting: " + e);
        }
        return null;
    }

    // Deszyfruje ciąg Base64 do tekstu jawnego
    public static String decrypt(String strToDecrypt) {
        try {
            SecretKeySpec secretKey = getSecretKeySpec(SECRET_KEY);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("Error while decrypting: " + e);
        }
        return null;
    }

    public static String encodeSafe(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    public static String decodeSafe(String text) {
        return new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8);
    }
}