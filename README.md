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
