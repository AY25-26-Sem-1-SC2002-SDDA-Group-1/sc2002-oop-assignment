import java.util.List;

/**
 * Repository interface for managing users.
 */
public interface IUserRepository {
    /**
     * Gets all users.
     *
     * @return list of all users
     */
    List<User> getAllUsers();

    /**
     * Gets a user by ID.
     *
     * @param userId the user ID
     * @return the user or null if not found
     */
    User getUserById(String userId);

    /**
     * Adds a new user.
     *
     * @param user the user to add
     */
    void addUser(User user);

    /**
     * Removes a user by ID.
     *
     * @param userId the user ID
     */
    void removeUser(String userId);

    /**
     * Saves all users to persistent storage.
     */
    void saveUsers();

    /**
     * Generates a new unique company representative ID.
     *
     * @return the generated ID
     */
    String generateCompanyRepId();
}