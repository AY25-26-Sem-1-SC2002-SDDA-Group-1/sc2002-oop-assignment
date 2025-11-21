import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyRepresentative extends User {
    private final String companyName;
    private final String department;
    private final String position;
    private final String email;
    private boolean isApproved; // true when approved by staff
    private boolean isRejected; // true when explicitly rejected by staff
    
    // Repositories for data access
    private IInternshipRepository internshipRepository;
    private IApplicationRepository applicationRepository;
    private IWaitlistManager waitlistManager;

    public CompanyRepresentative(String userID, String name, String password,
                                String companyName, String department, String position, String email,
                                IInternshipRepository internshipRepository, IApplicationRepository applicationRepository) {
        super(userID, name, password);
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.email = email;
        this.isApproved = false;
        this.isRejected = false;
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
    }

    // Setters for repository dependency injection
    public void setInternshipRepository(IInternshipRepository internshipRepository) {
        this.internshipRepository = internshipRepository;
    }

    public void setApplicationRepository(IApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }
    
    public void setWaitlistManager(IWaitlistManager waitlistManager) {
        this.waitlistManager = waitlistManager;
    }

    // Constructor with hash and salt for secure password storage
    public CompanyRepresentative(String userID, String name, String passwordHash, String salt,
                                String companyName, String department, String position, String email,
                                IInternshipRepository internshipRepository, IApplicationRepository applicationRepository) {
        super(userID, name, passwordHash, salt);
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.email = email;
        this.isApproved = false;
        this.isRejected = false;
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
    }

    public boolean createInternship(String title, String description, String level,
                                   String preferredMajor, Date openingDate, Date closingDate,
                                   int maxSlots, double minGPA) {
        // Only approved (and not rejected) representatives can create internships
        if (!isApproved || isRejected) return false;

        int internshipCount = 0;
        for (InternshipOpportunity opp : internshipRepository.getAllInternships()) {
            if (opp.getCreatedBy().getUserID().equals(this.userID)) {
                internshipCount++;
            }
        }
        if (internshipCount >= 5) return false;

        if (maxSlots > 10 || maxSlots < 1) return false;
        if (minGPA < 0.0 || minGPA > 5.0) return false;

        String internshipId = internshipRepository.generateInternshipId();
        InternshipOpportunity opportunity = new InternshipOpportunity(
            internshipId,
            title,
            description,
            level,
            preferredMajor,
            openingDate,
            closingDate,
            maxSlots,
            minGPA,
            this
        );
        internshipRepository.addInternship(opportunity);
        return true;
    }

    public List<Application> viewApplications() {
        List<Application> myApplications = new ArrayList<>();
        for (Application app : applicationRepository.getAllApplications()) {
            if (app.getOpportunity().getCreatedBy().getUserID().equals(this.userID)) {
                myApplications.add(app);
            }
        }
        return myApplications;
    }

    public List<Application> viewApplications(String opportunityID) {
        List<Application> opportunityApplications = new ArrayList<>();
        for (Application app : applicationRepository.getAllApplications()) {
            if (app.getOpportunity().getOpportunityID().equals(opportunityID) &&
                app.getOpportunity().getCreatedBy().getUserID().equals(this.userID)) {
                opportunityApplications.add(app);
            }
        }
        return opportunityApplications;
    }

    public List<Application> getPendingApplications() {
        List<Application> pendingApplications = new ArrayList<>();
        for (Application app : applicationRepository.getAllApplications()) {
            if (app.getOpportunity().getCreatedBy().getUserID().equals(this.userID) &&
                app.getStatus().equals("Pending")) {
                pendingApplications.add(app);
            }
        }
        return pendingApplications;
    }

    public boolean processApplication(String applicationID, boolean approve) {
        Application target = null;
        for (Application app : applicationRepository.getAllApplications()) {
            if (app.getApplicationID().equals(applicationID)) {
                target = app;
                break;
            }
        }
        if (target != null &&
            target.getOpportunity().getCreatedBy().getUserID().equals(this.userID) &&
            target.getStatus().equals("Pending")) {
            
            if (approve) {
                // Check if internship already has confirmed OR approved (successful) students at max slots
                InternshipOpportunity opportunity = target.getOpportunity();
                long filledSlots = applicationRepository.getAllApplications().stream()
                    .filter(app -> app.getOpportunity().getOpportunityID().equals(opportunity.getOpportunityID()))
                    .filter(app -> app.getStatus().equals("Confirmed") || app.getStatus().equals("Successful"))
                    .count();
                
                if (filledSlots >= opportunity.getMaxSlots()) {
                    // Add to waitlist instead of approving directly
                    target.updateStatus("Successful");
                    if (waitlistManager != null) {
                        waitlistManager.addToWaitlist(opportunity.getOpportunityID(), target);
                    }
                } else {
                    target.updateStatus("Successful");
                }
            } else {
                target.updateStatus("Unsuccessful");
            }
            
            applicationRepository.saveApplications();
            internshipRepository.saveInternships();
            return true;
        }
        return false;
    }
    
    /**
     * Batch approve applications with slot limit enforcement.
     * Only approves up to available slots, rest are added to waitlist.
     */
    public int batchApproveApplications(List<String> applicationIds) {
        if (applicationIds == null || applicationIds.isEmpty()) {
            return 0;
        }
        
        // Group applications by internship to enforce slot limits per internship
        Map<String, List<Application>> appsByInternship = new HashMap<>();
        
        for (String appId : applicationIds) {
            Application app = applicationRepository.getApplicationById(appId);
            if (app != null && 
                app.getStatus().equals("Pending") &&
                app.getOpportunity().getCreatedBy().getUserID().equals(this.userID)) {
                
                String oppId = app.getOpportunity().getOpportunityID();
                appsByInternship.computeIfAbsent(oppId, k -> new ArrayList<>()).add(app);
            }
        }
        
        int approvedCount = 0;
        int waitlistedCount = 0;
        
        // Process each internship separately
        for (Map.Entry<String, List<Application>> entry : appsByInternship.entrySet()) {
            String oppId = entry.getKey();
            List<Application> apps = entry.getValue();
            InternshipOpportunity opportunity = internshipRepository.getInternshipById(oppId);
            
            if (opportunity == null) continue;
            
            // Count current confirmed AND successful (approved) applications
            long filledSlots = applicationRepository.getAllApplications().stream()
                .filter(app -> app.getOpportunity().getOpportunityID().equals(oppId))
                .filter(app -> app.getStatus().equals("Confirmed") || app.getStatus().equals("Successful"))
                .count();
            
            int availableSlots = (int)(opportunity.getMaxSlots() - filledSlots);
            
            // Approve up to available slots, waitlist the rest
            for (int i = 0; i < apps.size(); i++) {
                Application app = apps.get(i);
                if (i < availableSlots) {
                    // Directly approve
                    app.updateStatus("Successful");
                    approvedCount++;
                } else {
                    // Add to waitlist (this will set status to "Waitlisted")
                    if (waitlistManager != null) {
                        waitlistManager.addToWaitlist(oppId, app);
                        waitlistedCount++;
                    }
                }
            }
        }
        
        applicationRepository.saveApplications();
        System.out.println("Batch approval complete: " + approvedCount + " approved, " + waitlistedCount + " added to waitlist.");
        return approvedCount + waitlistedCount;
    }
    
    /**
     * View waitlist for a specific internship
     */
    public List<WaitlistEntry> viewWaitlist(String opportunityID) {
        if (waitlistManager == null) {
            return new ArrayList<>();
        }
        
        InternshipOpportunity opportunity = internshipRepository.getInternshipById(opportunityID);
        if (opportunity == null || !opportunity.getCreatedBy().getUserID().equals(this.userID)) {
            return new ArrayList<>();
        }
        
        return waitlistManager.getWaitlist(opportunityID);
    }
    
    /**
     * Reorder waitlist by moving an application to a new position
     */
    public boolean reorderWaitlist(String opportunityID, String applicationID, int newPosition) {
        if (waitlistManager == null) {
            return false;
        }
        
        InternshipOpportunity opportunity = internshipRepository.getInternshipById(opportunityID);
        if (opportunity == null || !opportunity.getCreatedBy().getUserID().equals(this.userID)) {
            return false;
        }
        
        return waitlistManager.reorderWaitlist(opportunityID, applicationID, newPosition);
    }
    
    /**
     * Manually promote an application from waitlist to successful
     */
    public boolean promoteFromWaitlist(String opportunityID, String applicationID) {
        if (waitlistManager == null) {
            return false;
        }
        
        InternshipOpportunity opportunity = internshipRepository.getInternshipById(opportunityID);
        if (opportunity == null || !opportunity.getCreatedBy().getUserID().equals(this.userID)) {
            return false;
        }
        
        // Check if there are available slots
        long filledSlots = applicationRepository.getAllApplications().stream()
            .filter(app -> app.getOpportunity().getOpportunityID().equals(opportunityID))
            .filter(app -> app.getStatus().equals("Confirmed") || app.getStatus().equals("Successful"))
            .count();
        
        if (filledSlots >= opportunity.getMaxSlots()) {
            System.out.println("Cannot promote: All slots are filled.");
            return false;
        }
        
        // Remove from waitlist and update status
        boolean removed = waitlistManager.removeFromWaitlist(opportunityID, applicationID);
        if (removed) {
            Application app = applicationRepository.getApplicationById(applicationID);
            if (app != null) {
                app.updateStatus("Successful");
                applicationRepository.saveApplications();
                System.out.println("Application promoted from waitlist to Successful status.");
                return true;
            }
        }
        
        return false;
    }

    public void toggleVisibility(String opportunityID, boolean visible) {
        InternshipOpportunity opportunity = internshipRepository.getInternshipById(opportunityID);
        if (opportunity != null && opportunity.getCreatedBy().getUserID().equals(this.userID)) {
            opportunity.setVisibility(visible);
            internshipRepository.saveInternships();
        }
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getDepartment() {
        return department;
    }

    public String getPosition() {
        return position;
    }
    
    public String getEmail() {
        return email;
    }

    public boolean isApproved() { return isApproved; }
    public boolean isRejected() { return isRejected; }
    public void setApproved(boolean approved) {
        if (approved) {
            this.isApproved = true;
            this.isRejected = false; // cannot be rejected if approved
        } else {
            this.isApproved = false;
        }
    }
    public void setRejected(boolean rejected) {
        if (rejected) {
            this.isRejected = true;
            this.isApproved = false; // cannot be approved if rejected
        } else {
            this.isRejected = false;
        }
    }

    @Override
    public IMenuHandler createMenuHandler(
        InternshipService internshipService,
        ApplicationService applicationService,
        UserService userService,
        java.util.Scanner scanner
    ) {
        return new CompanyRepMenuHandler(this, internshipService, applicationService, userService, scanner);
    }

    @Override
    public boolean isCompanyRepresentative() { return true; }
    
    @Override
    public CompanyRepresentative asCompanyRepresentative() { return this; }
}