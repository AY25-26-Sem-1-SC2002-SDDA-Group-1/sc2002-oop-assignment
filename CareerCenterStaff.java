import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CareerCenterStaff extends User {
    private final String staffDepartment;
    private final IUserRepository userRepository;
    private IInternshipRepository internshipRepository;
    private IApplicationRepository applicationRepository;
    private IWaitlistManager waitlistManager;

    public CareerCenterStaff(String userID, String name, String password, String staffDepartment) {
        super(userID, name, password);
        this.staffDepartment = staffDepartment;
        this.userRepository = null;
        this.internshipRepository = null;
        this.applicationRepository = null;
    }

    public CareerCenterStaff(String userID, String name, String password, String staffDepartment,
                           IUserRepository userRepository, IInternshipRepository internshipRepository,
                           IApplicationRepository applicationRepository) {
        super(userID, name, password);
        this.staffDepartment = staffDepartment;
        this.userRepository = userRepository;
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
    }

    // Constructor with hash and salt for secure password storage
    public CareerCenterStaff(String userID, String name, String passwordHash, String salt, String staffDepartment,
                           IUserRepository userRepository, IInternshipRepository internshipRepository,
                           IApplicationRepository applicationRepository) {
        super(userID, name, passwordHash, salt);
        this.staffDepartment = staffDepartment;
        this.userRepository = userRepository;
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
        this.waitlistManager = null; // Set via setter if needed
    }
    
    public void setWaitlistManager(IWaitlistManager waitlistManager) {
        this.waitlistManager = waitlistManager;
    }
    
    public void setInternshipRepository(IInternshipRepository internshipRepository) {
        this.internshipRepository = internshipRepository;
    }
    
    public void setApplicationRepository(IApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

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

    public List<InternshipOpportunity> getPendingInternships() {
        List<InternshipOpportunity> pendingInternships = new ArrayList<>();
        for (InternshipOpportunity opp : internshipRepository.getAllInternships()) {
            if (opp.getStatus().equals("Pending")) {
                pendingInternships.add(opp);
            }
        }
        return pendingInternships;
    }

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

    public boolean processWithdrawal(String applicationID, boolean approve) {
        Application application = applicationRepository.getApplicationById(applicationID);
        if (application == null || !application.getStatus().equals("Withdrawal Requested")) {
            return false;
        }
        
        if (approve) {
            String previousStatus = application.getPreviousStatus();
            
            // If previous status was Confirmed or Successful, free a slot and process waitlist
            // Both statuses count toward slot limits, so withdrawal should trigger promotion
            if ("Confirmed".equals(previousStatus) || "Successful".equals(previousStatus)) {
                application.updateStatus("Withdrawn");
                
                // Withdraw all other pending/successful applications for this student
                Student student = application.getApplicant();
                for (Application app : applicationRepository.getAllApplications()) {
                    if (app.getApplicant().getUserID().equals(student.getUserID()) &&
                        !app.getApplicationID().equals(applicationID) &&
                        (app.getStatus().equals("Pending") || 
                         app.getStatus().equals("Successful") ||
                         app.getStatus().equals("Waitlisted"))) {
                        app.updateStatus("Withdrawn");
                    }
                }
                
                // Get the internship opportunity
                InternshipOpportunity internship = application.getOpportunity();
                
                // Auto-promote from waitlist if available
                if (waitlistManager != null) {
                    Application promoted = waitlistManager.promoteNextFromWaitlist(internship.getOpportunityID());
                    if (promoted != null) {
                        System.out.println("[AUTO-PROMOTION] Application " + promoted.getApplicationID() + 
                                         " for student " + promoted.getApplicant().getName() + 
                                         " has been automatically promoted from waitlist to Successful status.");
                    }
                }
                
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

    public String getStaffDepartment() {
        return staffDepartment;
    }

    @Override
    public IMenuHandler createMenuHandler(
        InternshipService internshipService,
        ApplicationService applicationService,
        UserService userService,
        java.util.Scanner scanner
    ) {
        return new CareerStaffMenuHandler(this, userService, internshipService, applicationService, scanner);
    }

    @Override
    public boolean isCareerCenterStaff() { return true; }
    
    @Override
    public CareerCenterStaff asCareerCenterStaff() { return this; }
}