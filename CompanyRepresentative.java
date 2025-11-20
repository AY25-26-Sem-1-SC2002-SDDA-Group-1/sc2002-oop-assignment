import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
            target.updateStatus(approve ? "Successful" : "Unsuccessful");
            applicationRepository.saveApplications();
            internshipRepository.saveInternships();
            return true;
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
}