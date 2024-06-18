import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Hash {

    public static void main(String[] args) {
        String plainText = "900478657";
        try {
            String hashedData = hashWithSHA256(plainText);
            System.out.println("Plain data: " + plainText);
            System.out.println("Hashed data(SHA-256): " + hashedData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String hashWithSHA256(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
