import java.util.List;

/**
 * Repository interface for managing internship opportunities.
 */
public interface IInternshipRepository {
    /**
     * Gets all internships.
     *
     * @return list of all internships
     */
    List<InternshipOpportunity> getAllInternships();

    /**
     * Gets an internship by ID.
     *
     * @param opportunityId the internship ID
     * @return the internship or null if not found
     */
    InternshipOpportunity getInternshipById(String opportunityId);

    /**
     * Adds a new internship.
     *
     * @param internship the internship to add
     */
    void addInternship(InternshipOpportunity internship);

    /**
     * Removes an internship by ID.
     *
     * @param opportunityId the internship ID
     */
    void removeInternship(String opportunityId);

    /**
     * Saves all internships to persistent storage.
     */
    void saveInternships();

    /**
     * Generates a new unique internship ID.
     *
     * @return the generated ID
     */
    String generateInternshipId();
}