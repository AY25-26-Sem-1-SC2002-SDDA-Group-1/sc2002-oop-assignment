/**
 * Abstract base class for all user types in the system.
 */
public abstract class User {
    /** The user ID. */
    protected String userID;
    /** The name. */
    protected String name;
    /** The password hash. */
    protected String passwordHash;
    /** The salt for password hashing. */
    protected String salt;
    /** Whether the user is logged in. */
    protected boolean isLoggedIn;

    /**
     * Constructs a User with a plain text password.
     *
     * @param userID the user ID
     * @param name the name
     * @param password the password
     */
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

    /**
     * Constructs a User with hashed password and salt.
     *
     * @param userID the user ID
     * @param name the name
     * @param passwordHash the hashed password
     * @param salt the salt
     */
    public User(String userID, String name, String passwordHash, String salt) {
        this.userID = userID;
        this.name = name;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.isLoggedIn = false;
    }

    /**
     * Logs in the user with the given password.
     *
     * @param password the password
     * @return true if login successful
     */
    public boolean login(String password) {
        if (PasswordUtil.verifyPassword(password, this.passwordHash, this.salt)) {
            this.isLoggedIn = true;
            return true;
        }
        return false;
    }

    /**
     * Logs out the user.
     */
    public void logout() {
        this.isLoggedIn = false;
    }

    /**
     * Changes the user's password.
     *
     * @param newPassword the new password
     */
    public void changePassword(String newPassword) {
        this.salt = PasswordUtil.generateSalt();
        this.passwordHash = PasswordUtil.hashPassword(newPassword, this.salt);
    }

    /**
     * Verifies the given password.
     *
     * @param password the password to verify
     * @return true if password matches
     */
    public boolean verifyPassword(String password) {
        return PasswordUtil.verifyPassword(password, this.passwordHash, this.salt);
    }

    /**
     * Gets the password hash.
     *
     * @return the password hash
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Gets the salt.
     *
     * @return the salt
     */
    public String getSalt() {
        return salt;
    }

    /**
     * Gets the user ID.
     *
     * @return the user ID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if the user is logged in.
     *
     * @return true if logged in
     */
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    /**
     * Creates a menu handler for this user type.
     *
     * @param internshipService the internship service
     * @param applicationService the application service
     * @param userService the user service
     * @param scanner the scanner
     * @return the menu handler
     */
    public abstract IMenuHandler createMenuHandler(
        InternshipService internshipService,
        ApplicationService applicationService,
        UserService userService,
         java.util.Scanner scanner
    );

    /**
     * Checks if this user is a student.
     *
     * @return true if student
     */
    public boolean isStudent() { return false; }

    /**
     * Checks if this user is a company representative.
     *
     * @return true if company representative
     */
    public boolean isCompanyRepresentative() { return false; }

    /**
     * Checks if this user is career center staff.
     *
     * @return true if career center staff
     */
    public boolean isCareerCenterStaff() { return false; }

    /**
     * Safely casts to Student.
     *
     * @return the Student instance or null
     */
    public Student asStudent() { return null; }

    /**
     * Safely casts to CompanyRepresentative.
     *
     * @return the CompanyRepresentative instance or null
     */
    public CompanyRepresentative asCompanyRepresentative() { return null; }

    /**
     * Safely casts to CareerCenterStaff.
     *
     * @return the CareerCenterStaff instance or null
     */
    public CareerCenterStaff asCareerCenterStaff() { return null; }
}