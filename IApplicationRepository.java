import java.util.List;

/**
 * Repository interface for managing internship applications.
 */
public interface IApplicationRepository {
    /**
     * Gets all applications.
     *
     * @return list of all applications
     */
    List<Application> getAllApplications();

    /**
     * Gets an application by ID.
     *
     * @param applicationId the application ID
     * @return the application or null if not found
     */
    Application getApplicationById(String applicationId);

    /**
     * Adds a new application.
     *
     * @param application the application to add
     */
    void addApplication(Application application);

    /**
     * Saves all applications to persistent storage.
     */
    void saveApplications();

    /**
     * Generates a new unique application ID.
     *
     * @return the generated ID
     */
    String generateApplicationId();
}