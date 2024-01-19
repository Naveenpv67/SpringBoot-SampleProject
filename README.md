import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionService {

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

    public static void main(String[] args) {
        try {
            String secretKey = "yourBase64EncodedSecretKey";
            String dataToEncrypt = "Hello, this is a test message.";

            String encryptedMessage = encryptAES256(secretKey, dataToEncrypt);
            System.out.println("Encrypted Message: " + encryptedMessage);

            // Add decryption logic in Go service using the same secretKey
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
