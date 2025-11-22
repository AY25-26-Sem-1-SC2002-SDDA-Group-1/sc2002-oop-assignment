import java.util.List;

/**
 * Interface for staff-facing application operations.
 */
public interface IStaffApplicationService {
    void approveApplication(String applicationId);
    void rejectApplication(String applicationId);
    void approveWithdrawal(String applicationId);
    List<Application> getAllApplications();
    List<Application> getApplicationsForInternship(String opportunityId);
    List<Application> getApplicationsForCompanyRep(String repId);
}