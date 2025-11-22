import java.util.List;

/**
 * Interface for student-facing application operations.
 */
public interface IStudentApplicationService {
    OperationResult applyForInternship(String studentId, String opportunityId);
    OperationResult acceptInternship(String studentId, String applicationId);
    OperationResult requestWithdrawal(String studentId, String applicationId);
    List<Application> getApplicationsForStudent(String studentId);
    List<InternshipOpportunity> getEligibleInternshipsForStudent(String studentId);
    List<Application> getApplicationsForInternship(String opportunityId);
    IApplicationRepository getApplicationRepository();
}