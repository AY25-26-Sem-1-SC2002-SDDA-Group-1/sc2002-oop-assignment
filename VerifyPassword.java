import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class VerifyPassword {
    public static void main(String[] args) {
        String password = "password";
        String hash = "Ku5dBL6bk8SpUzbTwWcPwdVawMvj6B2jql2XwZ+m170=";
        String salt = "bqUaSMmYtMgqOWutPjRYow==";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            md.update(password.getBytes());
            md.update(saltBytes);
            byte[] hashed = md.digest();
            String computed = Base64.getEncoder().encodeToString(hashed);
            System.out.println("Password + salt computed hash: " + computed);
            System.out.println("Expected: " + hash);
            System.out.println("Matches: " + computed.equals(hash));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}