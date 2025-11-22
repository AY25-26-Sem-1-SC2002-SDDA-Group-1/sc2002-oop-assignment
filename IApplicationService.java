import java.util.List;

/**
 * Interface for application management operations.
 */
public interface IApplicationService {
    OperationResult applyForInternship(String studentId, String opportunityId);
    void approveApplication(String applicationId);
    void rejectApplication(String applicationId);
    OperationResult acceptInternship(String studentId, String applicationId);
    OperationResult requestWithdrawal(String studentId, String applicationId);
    void approveWithdrawal(String applicationId);
    List<Application> getAllApplicationsForStudent(String studentId);
    List<Application> getAllApplicationsForInternship(String opportunityId);
    List<Application> getApplicationsForStudent(String studentId);
    List<Application> getApplicationsForCompanyRep(String repId);
    List<Application> getApplicationsForInternship(String opportunityId);
    IApplicationRepository getApplicationRepository();
}