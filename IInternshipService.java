import java.util.List;

/**
 * Interface for internship management operations.
 */
public interface IInternshipService {
    boolean createInternship(String userId, String title, String description, String level, String preferredMajor, java.util.Date openingDate, java.util.Date closingDate, int maxSlots, double minGPA);
    void approveInternship(String opportunityId);
    void rejectInternship(String opportunityId);
    void deleteInternship(String opportunityId);
    List<InternshipOpportunity> getAllInternships();
    InternshipOpportunity getInternship(String id);
    void toggleVisibility(String opportunityId, boolean visible);
    IInternshipRepository getInternshipRepository();
}