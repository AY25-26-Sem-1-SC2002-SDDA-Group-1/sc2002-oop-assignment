public class User {
    protected String userID;
    protected String name;
    protected String passwordHash;
    protected String salt;
    protected boolean isLoggedIn;

    public User(String userID, String name, String password) {
        this.userID = userID;
        this.name = name;
        this.isLoggedIn = false;
        // For backward compatibility, check if password is already hashed
        if (password.length() == 44 && password.contains("/") || password.contains("+")) {
            // Likely a base64 hash, treat as hash+salt combined (temporary migration)
            this.passwordHash = password;
            this.salt = PasswordUtil.generateSalt(); // Generate new salt for migration
        } else {
            // Plain text password, hash it
            this.salt = PasswordUtil.generateSalt();
            this.passwordHash = PasswordUtil.hashPassword(password, this.salt);
        }
    }

    // Constructor for loading from CSV with existing hash and salt
    public User(String userID, String name, String passwordHash, String salt) {
        this.userID = userID;
        this.name = name;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.isLoggedIn = false;
    }

    public boolean login(String password) {
        if (PasswordUtil.verifyPassword(password, this.passwordHash, this.salt)) {
            this.isLoggedIn = true;
            return true;
        }
        return false;
    }

    public void logout() {
        this.isLoggedIn = false;
    }

    public void changePassword(String newPassword) {
        this.salt = PasswordUtil.generateSalt();
        this.passwordHash = PasswordUtil.hashPassword(newPassword, this.salt);
    }

    public boolean verifyPassword(String password) {
        return PasswordUtil.verifyPassword(password, this.passwordHash, this.salt);
    }

    // For CSV persistence
    public String getPasswordHash() {
        return passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}