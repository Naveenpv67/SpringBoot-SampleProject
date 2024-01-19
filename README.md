import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionDecryptionExample {

    public static String encryptAES256(String secret, String data) throws Exception {
        byte[] rawSecret = Base64.getDecoder().decode(secret);
        byte[] rawData = data.getBytes(StandardCharsets.UTF_8);

        if (rawSecret.length != 32) {
            throw new IllegalArgumentException("Secret is not 32 bytes");
        }

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256, new SecureRandom(rawSecret));
        SecretKey secretKey = keyGenerator.generateKey();

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] nonce = new byte[cipher.getBlockSize()];
        SecureRandom.getInstanceStrong().nextBytes(nonce);

        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, nonce);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

        byte[] encryptedData = cipher.doFinal(rawData);

        // Combine nonce and encrypted data for transport
        byte[] result = new byte[nonce.length + encryptedData.length];
        System.arraycopy(nonce, 0, result, 0, nonce.length);
        System.arraycopy(encryptedData, 0, result, nonce.length, encryptedData.length);

        return Base64.getEncoder().encodeToString(result);
    }

    public static String decryptAES256(String secret, String encryptedData) throws Exception {
        byte[] rawSecret = Base64.getDecoder().decode(secret);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);

        if (rawSecret.length != 32) {
            throw new IllegalArgumentException("Secret is not 32 bytes");
        }

        // Extract nonce from the encrypted data
        int nonceSize = 12; // GCM nonce size
        byte[] nonce = new byte[nonceSize];
        System.arraycopy(encryptedBytes, 0, nonce, 0, nonceSize);

        // Extract encrypted message
        byte[] encryptedMessage = new byte[encryptedBytes.length - nonceSize];
        System.arraycopy(encryptedBytes, nonceSize, encryptedMessage, 0, encryptedMessage.length);

        SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(rawSecret, "AES");

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, nonce);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

        byte[] decryptedBytes = cipher.doFinal(encryptedMessage);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        try {
            String secretKey = Base64.getEncoder().encodeToString("YourSecretKey".getBytes(StandardCharsets.UTF_8));
            String dataToEncrypt = "Hello, this is a test message.";

            // Encryption
            String encryptedMessage = encryptAES256(secretKey, dataToEncrypt);
            System.out.println("Encrypted Message: " + encryptedMessage);

            // Decryption
            String decryptedMessage = decryptAES256(secretKey, encryptedMessage);
            System.out.println("Decrypted Message: " + decryptedMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
