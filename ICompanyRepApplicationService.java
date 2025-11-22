import java.util.List;

/**
 * Interface for company representative application operations.
 * Follows Interface Segregation Principle by providing only the methods
 * needed by company representatives for managing their internship applications.
 */
public interface ICompanyRepApplicationService {

    /**
     * Gets all applications for a specific company representative.
     * @param repId the company representative ID
     * @return list of applications
     */
    List<Application> getApplicationsForCompanyRep(String repId);

    /**
     * Gets applications for a specific internship opportunity created by the company representative.
     * @param repId the company representative ID
     * @param opportunityId the opportunity ID
     * @return list of applications
     */
    List<Application> getApplicationsForCompanyRepOpportunity(String repId, String opportunityId);

    /**
     * Processes an internship application (approve or reject).
     * @param repId the company representative ID
     * @param applicationId the application ID to process
     * @param approve true to approve, false to reject
     * @return true if processed successfully
     */
    boolean processApplication(String repId, String applicationId, boolean approve);

    /**
     * Approves an internship application.
     * @param applicationId the application ID to approve
     */
    void approveApplication(String applicationId);

    /**
     * Rejects an internship application.
     * @param applicationId the application ID to reject
     */
    void rejectApplication(String applicationId);
}