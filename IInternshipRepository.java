import java.util.List;

public interface IInternshipRepository {
    List<InternshipOpportunity> getAllInternships();
    InternshipOpportunity getInternshipById(String opportunityId);
    void addInternship(InternshipOpportunity internship);
    void removeInternship(String opportunityId);
    void saveInternships();
    String generateInternshipId();
}