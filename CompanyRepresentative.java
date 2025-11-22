import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a company representative who can create and manage internship opportunities.
 */
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

    /**
     * Constructs a CompanyRepresentative.
     *
     * @param userID the user ID
     * @param name the name
     * @param password the password
     * @param companyName the company name
     * @param department the department
     * @param position the position
     * @param email the email
     * @param internshipRepository the internship repository
     * @param applicationRepository the application repository
     */
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
     * Constructs a CompanyRepresentative with hashed password.
     *
     * @param userID the user ID
     * @param name the name
     * @param passwordHash the hashed password
     * @param salt the salt
     * @param companyName the company name
     * @param department the department
     * @param position the position
     * @param email the email
     * @param internshipRepository the internship repository
     * @param applicationRepository the application repository
     */
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

    /**
     * Creates a new internship opportunity.
     *
     * @param title the title
     * @param description the description
     * @param level the level
     * @param preferredMajor the preferred major
     * @param openingDate the opening date
     * @param closingDate the closing date
     * @param maxSlots the max slots
     * @param minGPA the min GPA
     * @return true if created successfully
     */
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

    /**
     * Gets all applications for internships created by this representative.
     *
     * @return list of applications
     */
    public List<Application> viewApplications() {
        List<Application> myApplications = new ArrayList<>();
        for (Application app : applicationRepository.getAllApplications()) {
            if (app.getOpportunity().getCreatedBy().getUserID().equals(this.userID)) {
                myApplications.add(app);
            }
        }
        return myApplications;
    }

    /**
     * Gets all applications for a specific internship opportunity.
     *
     * @param opportunityID the opportunity ID
     * @return list of applications
     */
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

    /**
     * Gets the list of pending applications for internships created by this representative.
     *
     * @return list of pending applications
     */
    public List<Application> getPendingApplications() {
        List<Application> pendingApplications = new ArrayList<>();
        for (Application app : applicationRepository.getAllApplications()) {
            if (app.getOpportunity().getCreatedBy().getUserID().equals(this.userID) &&
                app.getStatusEnum() == ApplicationStatus.PENDING) {
                pendingApplications.add(app);
            }
        }
        return pendingApplications;
    }

    /**
     * Processes an application by approving or rejecting it.
     *
     * @param applicationID the application ID
     * @param approve true to approve, false to reject
     * @return true if processed successfully
     */
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
            target.getStatusEnum() == ApplicationStatus.PENDING) {

            if (approve) {
                // Check slot limit before approving
                InternshipOpportunity opp = target.getOpportunity();
                long filledSlots = applicationRepository.getAllApplications().stream()
                    .filter(a -> a.getOpportunity().getOpportunityID().equals(opp.getOpportunityID()) &&
                           (a.getStatusEnum() == ApplicationStatus.SUCCESSFUL || a.getStatusEnum() == ApplicationStatus.CONFIRMED))
                    .count();
                if (filledSlots >= opp.getMaxSlots()) {
                    return false; // Cannot approve - slots are full
                }
                target.updateStatus(ApplicationStatus.SUCCESSFUL);
            } else {
                target.updateStatus(ApplicationStatus.UNSUCCESSFUL);
            }

            applicationRepository.saveApplications();
            internshipRepository.saveInternships();
            return true;
        }
        return false;
    }

    /**
     * Checks if the representative is approved.
     *
     * @return true if approved
     */
    public boolean isApproved() { return isApproved; }

    /**
     * Checks if the representative is rejected.
     *
     * @return true if rejected
     */
    public boolean isRejected() { return isRejected; }

    /**
     * Sets the approved status.
     *
     * @param approved true to approve
     */
    public void setApproved(boolean approved) {
        if (approved) {
            this.isApproved = true;
            this.isRejected = false; // cannot be rejected if approved
        } else {
            this.isApproved = false;
        }
    }

    /**
     * Sets the rejected status.
     *
     * @param rejected true to reject
     */
    public void setRejected(boolean rejected) {
        if (rejected) {
            this.isRejected = true;
            this.isApproved = false; // cannot be approved if rejected
        } else {
            this.isRejected = false;
        }
    }

    @Override
    public boolean isCompanyRepresentative() { return true; }

    @Override
    public CompanyRepresentative asCompanyRepresentative() { return this;     }

    /**
     * Toggles the visibility of an internship opportunity.
     *
     * @param opportunityID the opportunity ID
     * @param visible true to make visible
     */
    public void toggleVisibility(String opportunityID, boolean visible) {
        InternshipOpportunity opportunity = internshipRepository.getInternshipById(opportunityID);
        if (opportunity != null && opportunity.getCreatedBy().getUserID().equals(this.userID)) {
            opportunity.setVisibility(visible);
            internshipRepository.saveInternships();
        }
    }

    /**
     * Gets the company name.
     *
     * @return the company name
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Gets the department.
     *
     * @return the department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Gets the position.
     *
     * @return the position
     */
    public String getPosition() {
        return position;
    }

    /**
     * Gets the email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }
}