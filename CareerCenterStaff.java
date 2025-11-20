import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CareerCenterStaff extends User {
    private final String staffDepartment;
    private final IUserRepository userRepository;
    private final IInternshipRepository internshipRepository;
    private final IApplicationRepository applicationRepository;

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
            // Approve withdrawal: mark as Withdrawn
            application.updateStatus("Withdrawn");

            // If previous status was Confirmed, free a slot and process queue
            String prev = application.getPreviousStatus();
            if (prev != null && prev.equals("Confirmed")) {
                InternshipOpportunity internship = application.getOpportunity();
                if (internship != null) {
                    // Count remaining confirmed applications
                    List<Application> allApplications = applicationRepository.getAllApplications();
                    int confirmedCount = 0;
                    for (Application app : allApplications) {
                        if (app.getOpportunity().getOpportunityID().equals(internship.getOpportunityID()) && 
                            app.getStatus().equals("Confirmed")) {
                            confirmedCount++;
                        }
                    }
                    if (internship.getStatus().equals("Filled") && confirmedCount < internship.getMaxSlots()) {
                        internship.setStatus("Approved");
                    }
                    processQueue(internship);
                }
            }
        } else {
            // Reject withdrawal: restore to previous status or Successful
            application.updateStatus("Withdrawal Rejected");
        }

        applicationRepository.saveApplications();
        internshipRepository.saveInternships();
        return true;
    }
    
    private void processQueue(InternshipOpportunity internship) {
        String internshipId = internship.getOpportunityID();

        // Pre-filter applications for this internship to avoid repeated iterations
        List<Application> internshipApplications = applicationRepository.getAllApplications().stream()
            .filter(app -> app.getOpportunity().getOpportunityID().equals(internshipId))
            .toList();

        // While slots available and queue has applications
        while (true) {
            // Count current confirmed applications for this internship
            long confirmedCount = internshipApplications.stream()
                .filter(app -> app.getStatus().equals("Confirmed"))
                .count();

            // Check if slots are available
            if (confirmedCount >= internship.getMaxSlots()) {
                break; // Internship is full
            }

            // Find oldest queued application using streams for efficiency
            Application queuedApp = internshipApplications.stream()
                .filter(app -> app.getStatus().equals("Queued"))
                .min((app1, app2) -> {
                    Date date1 = app1.getQueuedDate() != null ? app1.getQueuedDate() : app1.getAppliedDate();
                    Date date2 = app2.getQueuedDate() != null ? app2.getQueuedDate() : app2.getAppliedDate();
                    return date1.compareTo(date2);
                })
                .orElse(null);

            if (queuedApp == null) {
                break; // No more queued applications
            }

            // Confirm the queued application
            queuedApp.updateStatus("Confirmed");

            // Withdraw all other applications for this student (only for this internship)
            Student student = queuedApp.getApplicant();
            internshipApplications.stream()
                .filter(app -> !app.getApplicationID().equals(queuedApp.getApplicationID()) &&
                              app.getApplicant().getUserID().equals(student.getUserID()) &&
                              !app.getStatus().equals("Withdrawn"))
                .forEach(app -> app.updateStatus("Withdrawn"));

            // Save changes to persist immediately
            applicationRepository.saveApplications();

            // The loop will check confirmedCount in the next iteration and break if full
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