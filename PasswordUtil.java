import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for password hashing and verification using SHA-256 with salt.
 * Provides static methods for secure password handling.
 */
public class PasswordUtil {
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

    private PasswordUtil() {
        // Utility class
    }

    /**
     * Generates a random salt for password hashing.
     *
     * @return the generated salt as a Base64 string
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hashes a password with the given salt using SHA-256.
     *
     * @param password the password to hash
     * @param salt the salt as a Base64 string
     * @return the hashed password as a Base64 string
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(Base64.getDecoder().decode(salt));
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Verifies a password against a hash and salt.
     *
     * @param password the password to verify
     * @param hash the expected hash
     * @param salt the salt used for hashing
     * @return true if the password matches the hash
     */
    public static boolean verifyPassword(String password, String hash, String salt) {
        String hashedInput = hashPassword(password, salt);
        return hashedInput.equals(hash);
    }
}