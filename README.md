import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
public class EncryptionController {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;

    @PostMapping("/encrypt")
    public ResponseEntity<String> encrypt(@RequestBody EncryptionRequest encryptionRequest) {
        try {
            SecretKey secretKey = new SecretKeySpec(encryptionRequest.getSecretKey().getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            byte[] iv = new byte[GCM_TAG_LENGTH / 8];
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            byte[] ciphertext = cipher.doFinal(encryptionRequest.getPlaintext().getBytes(StandardCharsets.UTF_8));
            String encryptedMessage = Base64.getEncoder().encodeToString(iv) + Base64.getEncoder().encodeToString(ciphertext);
            return ResponseEntity.ok(encryptedMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Encryption failed");
        }
    }

    @PostMapping("/decrypt")
    public ResponseEntity<String> decrypt(@RequestBody EncryptionRequest encryptionRequest) {
        try {
            SecretKey secretKey = new SecretKeySpec(encryptionRequest.getSecretKey().getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            // Extract IV and ciphertext from the combined string
            String combined = new String(Base64.getDecoder().decode(encryptionRequest.getEncryptedMessage()), StandardCharsets.UTF_8);
            byte[] iv = Base64.getDecoder().decode(combined.substring(0, GCM_TAG_LENGTH / 8));
            byte[] ciphertext = Base64.getDecoder().decode(combined.substring(GCM_TAG_LENGTH / 8));

            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            byte[] decryptedBytes = cipher.doFinal(ciphertext);
            String decryptedMessage = new String(decryptedBytes, StandardCharsets.UTF_8);
            return ResponseEntity.ok(decryptedMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Decryption failed");
        }
    }
}



public class EncryptionRequest {
    private String secretKey;
    private String plaintext;
    private String encryptedMessage;

    // Getters and setters
}
