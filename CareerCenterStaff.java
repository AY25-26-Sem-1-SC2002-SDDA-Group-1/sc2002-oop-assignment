import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CareerCenterStaff extends User {
    private final String staffDepartment;

    public CareerCenterStaff(String userID, String name, String password, String staffDepartment) {
        super(userID, name, password);
        this.staffDepartment = staffDepartment;
    }

    public List<CompanyRepresentative> getPendingCompanyReps() {
        List<CompanyRepresentative> pendingReps = new ArrayList<>();
        for (User user : Database.getUsers()) {
            if (user instanceof CompanyRepresentative) {
                CompanyRepresentative rep = (CompanyRepresentative) user;
                if (!rep.isApproved()) {
                    pendingReps.add(rep);
                }
            }
        }
        return pendingReps;
    }

    public boolean processCompanyRep(String repID, boolean approve) {
        User user = Database.getUser(repID);
        if (user instanceof CompanyRepresentative) {
            CompanyRepresentative rep = (CompanyRepresentative) user;
            if (!rep.isApproved() && approve) {
                rep.setApproved(true);
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
        for (Application app : Database.getApplications()) {
            if (app.getStatus().equals("Withdrawal Requested")) {
                withdrawalRequests.add(app);
            }
        }
        return withdrawalRequests;
    }

    public boolean processWithdrawal(String applicationID, boolean approve) {
        Application application = Database.getApplication(applicationID);
        if (application != null && application.getStatus().equals("Withdrawal Requested")) {
            if (approve) {
                // Store original status before withdrawal for proper handling
                boolean wasConfirmed = false;
                // Check if this was a confirmed placement
                for (Application app : Database.getApplications()) {
                    if (app.getApplicationID().equals(applicationID)) {
                        // Check manuallyWithdrawn flag to determine if it was confirmed
                        wasConfirmed = app.isManuallyWithdrawn();
                        break;
                    }
                }
                
                application.updateStatus("Withdrawn");
                
                // Only process internship status and queue if it was a confirmed placement
                if (wasConfirmed) {
                    InternshipOpportunity internship = application.getOpportunity();
                    if (internship != null) {
                        // Check if the internship should be unfilled
                        if (internship.getStatus().equals("Filled")) {
                            // Count remaining confirmed applications
                            int confirmedCount = 0;
                            for (Application app : Database.getApplications()) {
                                if (app.getOpportunity().equals(internship) && 
                                    app.getStatus().equals("Confirmed")) {
                                    confirmedCount++;
                                }
                            }
                            
                            // If slots are now available, change status back to Approved
                            if (confirmedCount < internship.getMaxSlots()) {
                                internship.setStatus("Approved");
                            }
                        }
                        
                        // Process waitlist queue - automatically confirm next queued application
                        processQueue(internship);
                    }
                }
            } else {
                // Rejection: restore to Pending (cannot determine original status, safe default)
                application.updateStatus("Pending");
            }
            Database.saveData();
            return true;
        }
        return false;
    }
    
    private void processQueue(InternshipOpportunity internship) {
        // Count current confirmed applications
        int confirmedCount = 0;
        for (Application app : Database.getApplications()) {
            if (app.getOpportunity().equals(internship) &&
                app.getStatus().equals("Confirmed")) {
                confirmedCount++;
            }
        }
        
        // While slots available and queue has applications
        while (confirmedCount < internship.getMaxSlots()) {
            // Find oldest queued application
            Application queuedApp = null;
            for (Application app : Database.getApplications()) {
                if (app.getOpportunity().equals(internship) &&
                    app.getStatus().equals("Queued")) {
                    if (queuedApp == null || app.getAppliedDate().before(queuedApp.getAppliedDate())) {
                        queuedApp = app;
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
            for (Application app : Database.getApplications()) {
                if (!app.getApplicationID().equals(queuedApp.getApplicationID()) &&
                    app.getApplicant().getUserID().equals(student.getUserID()) &&
                    !app.getStatus().equals("Withdrawn")) {
                    app.updateStatus("Withdrawn");
                }
            }
            
            confirmedCount++;
            
            // Mark internship as filled if max slots reached
            if (confirmedCount >= internship.getMaxSlots()) {
                internship.setStatus("Filled");
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