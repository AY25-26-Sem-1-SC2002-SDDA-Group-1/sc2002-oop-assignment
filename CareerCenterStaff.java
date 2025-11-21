import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a career center staff member who manages company representatives,
 * internship opportunities, and applications.
 */
public class CareerCenterStaff extends User {
    private final String staffDepartment;
    private final IUserRepository userRepository;
    private IInternshipRepository internshipRepository;
    private IApplicationRepository applicationRepository;

    /**
     * Constructs a CareerCenterStaff without repositories (for basic use).
     *
     * @param userID the user ID
     * @param name the name
     * @param password the password
     * @param staffDepartment the department
     */
    public CareerCenterStaff(String userID, String name, String password, String staffDepartment) {
        super(userID, name, password);
        this.staffDepartment = staffDepartment;
        this.userRepository = null;
        this.internshipRepository = null;
        this.applicationRepository = null;
    }

    /**
     * Constructs a CareerCenterStaff with repositories.
     *
     * @param userID the user ID
     * @param name the name
     * @param password the password
     * @param staffDepartment the department
     * @param userRepository the user repository
     * @param internshipRepository the internship repository
     * @param applicationRepository the application repository
     */
    public CareerCenterStaff(String userID, String name, String password, String staffDepartment,
                           IUserRepository userRepository, IInternshipRepository internshipRepository,
                           IApplicationRepository applicationRepository) {
        super(userID, name, password);
        this.staffDepartment = staffDepartment;
        this.userRepository = userRepository;
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
    }

    /**
     * Constructs a CareerCenterStaff with hashed password and repositories.
     *
     * @param userID the user ID
     * @param name the name
     * @param passwordHash the hashed password
     * @param salt the salt for password
     * @param staffDepartment the department
     * @param userRepository the user repository
     * @param internshipRepository the internship repository
     * @param applicationRepository the application repository
     */
    public CareerCenterStaff(String userID, String name, String passwordHash, String salt, String staffDepartment,
                           IUserRepository userRepository, IInternshipRepository internshipRepository,
                           IApplicationRepository applicationRepository) {
        super(userID, name, passwordHash, salt);
        this.staffDepartment = staffDepartment;
        this.userRepository = userRepository;
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;

    }

    /**
     * Sets the internship repository.
     *
     * @param internshipRepository the internship repository
     */
    public void setInternshipRepository(IInternshipRepository internshipRepository) {
        this.internshipRepository = internshipRepository;
    }

    /**
     * Sets the application repository.
     *
     * @param applicationRepository the application repository
     */
    public void setApplicationRepository(IApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    /**
     * Gets the list of pending company representatives awaiting approval.
     *
     * @return list of pending company representatives
     */
    public List<CompanyRepresentative> getPendingCompanyReps() {
        List<CompanyRepresentative> pendingReps = new ArrayList<>();
        // Prefer repository if available for SOLID compliance
        List<User> sourceUsers = userRepository.getAllUsers();
        for (User user : sourceUsers) {
            if (user.isCompanyRepresentative()) {
                CompanyRepresentative rep = user.asCompanyRepresentative();
                if (!rep.isApproved() && !rep.isRejected()) {
                    pendingReps.add(rep);
                }
            }
        }
        return pendingReps;
    }

    /**
     * Processes a company representative's approval or rejection.
     *
     * @param repID the representative ID
     * @param approve true to approve, false to reject
     * @return true if processed successfully
     */
    public boolean processCompanyRep(String repID, boolean approve) {
        User user = userRepository.getUserById(repID);
        if (user.isCompanyRepresentative()) {
            CompanyRepresentative rep = user.asCompanyRepresentative();
            if (approve && !rep.isApproved() && !rep.isRejected()) {
                rep.setApproved(true);
                // sync legacy Database copy if different instance
                // Repository already updated above, no need for legacy sync
                return true;
            }
            if (!approve && !rep.isApproved() && !rep.isRejected()) {
                rep.setRejected(true);
                // Repository already updated above, no need for legacy sync
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the list of pending internship opportunities.
     *
     * @return list of pending internships
     */
    public List<InternshipOpportunity> getPendingInternships() {
        List<InternshipOpportunity> pendingInternships = new ArrayList<>();
        for (InternshipOpportunity opp : internshipRepository.getAllInternships()) {
            if (opp.getStatus().equals("Pending")) {
                pendingInternships.add(opp);
            }
        }
        return pendingInternships;
    }

    /**
     * Processes an internship opportunity's approval or rejection.
     *
     * @param opportunityID the opportunity ID
     * @param approve true to approve, false to reject
     * @return true if processed successfully
     */
    public boolean processInternship(String opportunityID, boolean approve) {
        InternshipOpportunity opportunity = internshipRepository.getInternshipById(opportunityID);
        if (opportunity != null && opportunity.getStatus().equals("Pending")) {
            if (approve) {
                opportunity.setStatus("Approved");
                opportunity.setVisibility(true);  // Automatically set visibility to true when approved
            } else {
                opportunity.setStatus("Rejected");
            }
            internshipRepository.saveInternships();
            return true;
        }
        return false;
    }

    /**
     * Gets the list of withdrawal requests.
     *
     * @return list of applications with withdrawal requests
     */
    public List<Application> getWithdrawalRequests() {
        List<Application> withdrawalRequests = new ArrayList<>();
        List<Application> allApplications = applicationRepository.getAllApplications();
        for (Application app : allApplications) {
            if (app.getStatus().equals("Withdrawal Requested")) {
                withdrawalRequests.add(app);
            }
        }
        return withdrawalRequests;
    }

    /**
     * Processes a withdrawal request.
     *
     * @param applicationID the application ID
     * @param approve true to approve, false to reject
     * @return true if processed successfully
     */
    public boolean processWithdrawal(String applicationID, boolean approve) {
        Application application = applicationRepository.getApplicationById(applicationID);
        if (application == null || !application.getStatus().equals("Withdrawal Requested")) {
            return false;
        }
        
        if (approve) {
            String previousStatus = application.getPreviousStatus();
            
            // If previous status was Confirmed or Successful, free a slot
            // Both statuses count toward slot limits, so withdrawal should trigger promotion
            if ("Confirmed".equals(previousStatus) || "Successful".equals(previousStatus)) {
                application.updateStatus("Withdrawn");
                
                // Withdraw all other pending/successful applications for this student
                Student student = application.getApplicant();
                for (Application app : applicationRepository.getAllApplications()) {
                    if (app.getApplicant().getUserID().equals(student.getUserID()) &&
                        !app.getApplicationID().equals(applicationID) &&
                        (app.getStatus().equals("Pending") ||
                         app.getStatus().equals("Successful"))) {
                        app.updateStatus("Withdrawn");
                    }
                }
                
                // Get the internship opportunity
                InternshipOpportunity internship = application.getOpportunity();
                
                applicationRepository.saveApplications();
                return true;
                
            } else {
                // For Pending withdrawals, just mark as withdrawn (no slot to free)
                application.updateStatus("Withdrawn");
                applicationRepository.saveApplications();
                return true;
            }
        } else {
            // Rejection: revert to previous status
            application.updateStatus("Withdrawal Rejected");
            applicationRepository.saveApplications();
            return true;
        }
    }

    /**
     * Generates a report based on filters.
     *
     * @param filters the filters to apply
     * @return the generated report
     */
    public Report generateReports(Map<String, String> filters) {
        List<InternshipOpportunity> filteredOpportunities = new ArrayList<>();
        
        for (InternshipOpportunity opportunity : internshipRepository.getAllInternships()) {
            boolean matches = true;
            
            if (filters.containsKey("status") && 
                !opportunity.getStatus().equals(filters.get("status"))) {
                matches = false;
            }
            
            if (filters.containsKey("level") && 
                !opportunity.getLevel().equals(filters.get("level"))) {
                matches = false;
            }
            
            if (filters.containsKey("preferredMajor") && 
                !opportunity.getPreferredMajor().equalsIgnoreCase(filters.get("preferredMajor"))) {
                matches = false;
            }
            
            if (matches) {
                filteredOpportunities.add(opportunity);
            }
        }
        
        return new Report(filteredOpportunities, filters);
    }

    /**
     * Gets the staff department.
     *
     * @return the department
     */
    public String getStaffDepartment() {
        return staffDepartment;
    }

    /**
     * Creates a menu handler for this staff member.
     *
     * @param internshipService the internship service
     * @param applicationService the application service
     * @param userService the user service
     * @param scanner the scanner for input
     * @return the menu handler
     */
    @Override
    public IMenuHandler createMenuHandler(
        InternshipService internshipService,
        ApplicationService applicationService,
        UserService userService,
        java.util.Scanner scanner
    ) {
        return new CareerStaffMenuHandler(this, userService, internshipService, applicationService, scanner);
    }

    /**
     * Checks if this user is a career center staff.
     *
     * @return true
     */
    @Override
    public boolean isCareerCenterStaff() { return true; }

    /**
     * Casts this user to CareerCenterStaff.
     *
     * @return this instance
     */
    @Override
    public CareerCenterStaff asCareerCenterStaff() { return this; }
}