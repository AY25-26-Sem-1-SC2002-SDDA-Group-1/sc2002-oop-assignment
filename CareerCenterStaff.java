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

    public List<CompanyRepresentative> getPendingCompanyReps() {
        List<CompanyRepresentative> pendingReps = new ArrayList<>();
        // Prefer repository if available for SOLID compliance
        List<User> sourceUsers = (userRepository != null) ? userRepository.getAllUsers() : Database.getUsers();
        for (User user : sourceUsers) {
            if (user instanceof CompanyRepresentative) {
                CompanyRepresentative rep = (CompanyRepresentative) user;
                if (!rep.isApproved() && !rep.isRejected()) {
                    pendingReps.add(rep);
                }
            }
        }
        return pendingReps;
    }

    public boolean processCompanyRep(String repID, boolean approve) {
        User user = (userRepository != null) ? userRepository.getUserById(repID) : Database.getUser(repID);
        if (user instanceof CompanyRepresentative) {
            CompanyRepresentative rep = (CompanyRepresentative) user;
            if (approve && !rep.isApproved() && !rep.isRejected()) {
                rep.setApproved(true);
                // sync legacy Database copy if different instance
                User legacy = Database.getUser(repID);
                if (legacy instanceof CompanyRepresentative && legacy != rep) {
                    ((CompanyRepresentative) legacy).setApproved(true);
                }
                if (userRepository != null) userRepository.saveUsers();
                Database.saveData();
                return true;
            }
            if (!approve && !rep.isApproved() && !rep.isRejected()) {
                rep.setRejected(true);
                User legacy = Database.getUser(repID);
                if (legacy instanceof CompanyRepresentative && legacy != rep) {
                    ((CompanyRepresentative) legacy).setRejected(true);
                }
                if (userRepository != null) userRepository.saveUsers();
                Database.saveData();
                return true;
            }
        }
        return false;
    }

    public List<InternshipOpportunity> getPendingInternships() {
        List<InternshipOpportunity> pendingInternships = new ArrayList<>();
        for (InternshipOpportunity opp : Database.getInternships()) {
            if (opp.getStatus().equals("Pending")) {
                pendingInternships.add(opp);
            }
        }
        return pendingInternships;
    }

    public boolean processInternship(String opportunityID, boolean approve) {
        InternshipOpportunity opportunity = Database.getInternship(opportunityID);
        if (opportunity != null && opportunity.getStatus().equals("Pending")) {
            if (approve) {
                opportunity.setStatus("Approved");
                opportunity.setVisibility(true);  // Automatically set visibility to true when approved
            } else {
                opportunity.setStatus("Rejected");
            }
            Database.saveData();
            return true;
        }
        return false;
    }

    public List<Application> getWithdrawalRequests() {
        List<Application> withdrawalRequests = new ArrayList<>();
        List<Application> allApplications = (applicationRepository != null) 
            ? applicationRepository.getAllApplications() 
            : Database.getApplications();
        for (Application app : allApplications) {
            if (app.getStatus().equals("Withdrawal Requested")) {
                withdrawalRequests.add(app);
            }
        }
        return withdrawalRequests;
    }

    public boolean processWithdrawal(String applicationID, boolean approve) {
        Application application = (applicationRepository != null)
            ? applicationRepository.getApplicationById(applicationID)
            : Database.getApplication(applicationID);
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
                    List<Application> allApplications = (applicationRepository != null)
                        ? applicationRepository.getAllApplications()
                        : Database.getApplications();
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

        if (applicationRepository != null) {
            applicationRepository.saveApplications();
            if (internshipRepository != null) {
                internshipRepository.saveInternships();
            }
        } else {
            Database.saveData();
        }
        return true;
    }
    
    private void processQueue(InternshipOpportunity internship) {
        // While slots available and queue has applications
        while (true) {
            // Refresh application list each iteration to get latest statuses
            List<Application> allApplications = (applicationRepository != null) 
                ? applicationRepository.getAllApplications() 
                : Database.getApplications();
            
            // Count current confirmed applications
            int confirmedCount = 0;
            for (Application app : allApplications) {
                if (app.getOpportunity().getOpportunityID().equals(internship.getOpportunityID()) &&
                    app.getStatus().equals("Confirmed")) {
                    confirmedCount++;
                }
            }
            
            // Check if slots are available
            if (confirmedCount >= internship.getMaxSlots()) {
                break; // Internship is full
            }
            
            // Find oldest queued application (by queuedDate, not appliedDate)
            Application queuedApp = null;
            for (Application app : allApplications) {
                if (app.getOpportunity().getOpportunityID().equals(internship.getOpportunityID()) &&
                    app.getStatus().equals("Queued")) {
                    Date compareDate = app.getQueuedDate() != null ? app.getQueuedDate() : app.getAppliedDate();
                    if (queuedApp == null) {
                        queuedApp = app;
                    } else {
                        Date currentDate = queuedApp.getQueuedDate() != null ? queuedApp.getQueuedDate() : queuedApp.getAppliedDate();
                        if (compareDate.before(currentDate)) {
                            queuedApp = app;
                        }
                    }
                }
            }
            
            if (queuedApp == null) {
                break; // No more queued applications
            }
            
            // Confirm the queued application
            queuedApp.updateStatus("Confirmed");
            
            // Withdraw all other applications for this student
            Student student = queuedApp.getApplicant();
            for (Application app : allApplications) {
                if (!app.getApplicationID().equals(queuedApp.getApplicationID()) &&
                    app.getApplicant().getUserID().equals(student.getUserID()) &&
                    !app.getStatus().equals("Withdrawn")) {
                    app.updateStatus("Withdrawn");
                }
            }
            
            // Save changes to persist immediately
            if (applicationRepository != null) {
                applicationRepository.saveApplications();
            }
            
            // Mark internship as filled if max slots reached (will be checked in next iteration)
            if (confirmedCount + 1 >= internship.getMaxSlots()) {
                internship.setStatus("Filled");
                if (internshipRepository != null) {
                    internshipRepository.saveInternships();
                }
            }
        }
    }

    public Report generateReports(Map<String, String> filters) {
        List<InternshipOpportunity> filteredOpportunities = new ArrayList<>();
        
        for (InternshipOpportunity opportunity : Database.getInternships()) {
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
}