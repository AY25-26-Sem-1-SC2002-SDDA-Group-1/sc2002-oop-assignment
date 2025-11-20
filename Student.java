import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Student extends User {
    private final int yearOfStudy;
    private final String major;
    private final double gpa;
    
    // Repositories for data access
    private IInternshipRepository internshipRepository;
    private IApplicationRepository applicationRepository;

    public Student(String userID, String name, String password, int yearOfStudy, String major, double gpa,
                   IInternshipRepository internshipRepository, IApplicationRepository applicationRepository) {
        super(userID, name, password);
        this.yearOfStudy = yearOfStudy;
        this.major = major;
        this.gpa = gpa;
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
    public Student(String userID, String name, String passwordHash, String salt, int yearOfStudy, String major, double gpa,
                   IInternshipRepository internshipRepository, IApplicationRepository applicationRepository) {
        super(userID, name, passwordHash, salt);
        this.yearOfStudy = yearOfStudy;
        this.major = major;
        this.gpa = gpa;
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
    }

    public List<InternshipOpportunity> viewEligibleInternships() {
        List<InternshipOpportunity> eligible = new ArrayList<>();
        for (InternshipOpportunity opportunity : internshipRepository.getAllInternships()) {
            if (opportunity.isVisible() &&
                opportunity.getPreferredMajor().equalsIgnoreCase(this.major) &&
                this.gpa >= opportunity.getMinGPA()) {
                eligible.add(opportunity);
            }
        }
        return eligible;
    }
    
    private boolean isEligibleForLevel(String level) {
        // Year 1 and 2 students can only apply for Basic level
        if (yearOfStudy <= 2) {
            return level.equals("Basic");
        }
        // Year 3 and above can apply for any level
        return true;
    }
    
    private int getActiveApplicationCount() {
        int count = 0;
        for (Application app : applicationRepository.getAllApplications()) {
            if (app.getApplicant().getUserID().equals(this.userID) &&
                !app.getStatus().equals("Withdrawn") &&
                !app.getStatus().equals("Unsuccessful")) {
                count++;
            }
        }
        return count;
    }

    public boolean applyForInternship(String opportunityID) {
        // Repositories are guaranteed to be initialized
        InternshipOpportunity opportunity = internshipRepository.getInternshipById(opportunityID);
        if (opportunity == null) {
            System.out.println("[ERROR] Internship not found.");
            return false;
        }
        
        // Check if student already has a confirmed internship
        for (Application app : applicationRepository.getAllApplications()) {
            if (app.getApplicant().getUserID().equals(this.userID) &&
                app.getStatus().equals("Confirmed")) {
                System.out.println("[BLOCKED] You cannot apply to new internships while you have a confirmed internship.");
                System.out.println("          Confirmed internship: " + app.getOpportunity().getTitle() + " at " + app.getOpportunity().getCreatedBy().getCompanyName());
                return false;
            }
        }
        
        // Check if student already has 3 active applications
        int activeCount = getActiveApplicationCount();
        if (activeCount >= 3) {
            System.out.println("[BLOCKED] You cannot have more than 3 active applications.");
            System.out.println("          Current active applications: " + activeCount);
            return false;
        }
        
        if (!opportunity.isOpen()) {
            System.out.println("[BLOCKED] This internship is not open for applications.");
            System.out.println("          Status: " + opportunity.getStatus());
            return false;
        }
        
        if (!opportunity.isVisible()) {
            System.out.println("[BLOCKED] This internship is not currently visible.");
            return false;
        }
        
        if (!opportunity.getPreferredMajor().equalsIgnoreCase(this.major)) {
            System.out.println("[BLOCKED] Your major does not match the internship requirements.");
            System.out.println("          Required: " + opportunity.getPreferredMajor() + " | Your major: " + this.major);
            return false;
        }

        // Check if student is eligible for this level
        if (!isEligibleForLevel(opportunity.getLevel())) {
            System.out.println("[BLOCKED] You are not eligible for this internship level.");
            System.out.println("          Required level: " + opportunity.getLevel() + " | Your year: " + yearOfStudy);
            System.out.println("          Note: Year 1-2 students can only apply for Basic level internships.");
            return false;
        }

        // Check GPA requirement
        if (this.gpa < opportunity.getMinGPA()) {
            System.out.println("[BLOCKED] Your GPA does not meet the minimum requirement.");
            System.out.println("          Required: " + opportunity.getMinGPA() + " | Your GPA: " + this.gpa);
            return false;
        }
        
        // Check if already applied
        for (Application app : applicationRepository.getAllApplications()) {
            if (app.getApplicant().getUserID().equals(this.userID) && 
                app.getOpportunity().getOpportunityID().equals(opportunityID)) {
                System.out.println("[BLOCKED] You have already applied to this internship.");
                System.out.println("          Application status: " + app.getStatus());
                return false;
            }
        }
        
        // Check if student manually withdrew from this internship before
        for (Application app : applicationRepository.getAllApplications()) {
            if (app.getApplicant().getUserID().equals(this.userID) &&
                app.getOpportunity().getOpportunityID().equals(opportunityID) &&
                app.isManuallyWithdrawn()) {
                System.out.println("[BLOCKED] You cannot reapply to an internship you manually withdrew from.");
                System.out.println("          Previous application: " + app.getApplicationID() + " (Withdrawn)");
                return false;
            }
        }
        
        // Determine if internship is already full
        int confirmedCount = 0;
        for (Application app : applicationRepository.getAllApplications()) {
            if (app.getOpportunity().getOpportunityID().equals(opportunity.getOpportunityID()) &&
                app.getStatus().equals("Confirmed")) {
                confirmedCount++;
            }
        }

        String initialStatus = (confirmedCount >= opportunity.getMaxSlots()) ? "Queued" : "Pending";

        Application application = new Application(
            applicationRepository.generateApplicationId(),
            this,
            opportunity,
            initialStatus
        );

        applicationRepository.addApplication(application);

        if ("Queued".equals(initialStatus)) {
            // Compute waitlist position (queued applications count for this internship)
            int queuedCount = 0;
            for (Application app : applicationRepository.getAllApplications()) {
                if (app.getOpportunity().getOpportunityID().equals(opportunity.getOpportunityID()) &&
                    app.getStatus().equals("Queued")) {
                    queuedCount++;
                }
            }
            System.out.println("This internship is currently full (" + confirmedCount + "/" + opportunity.getMaxSlots() + " slots filled).");
            System.out.println("You have been added to the waitlist. Current waitlist size: " + queuedCount + ".");
            System.out.println("We will automatically confirm you if a slot becomes available.");
        } else {
            System.out.println("Application submitted successfully.");
        }
        return true;
    }

    public List<Application> viewApplications() {
        List<Application> myApplications = new ArrayList<>();
        for (Application app : applicationRepository.getAllApplications()) {
            if (app.getApplicant().getUserID().equals(this.userID)) {
                myApplications.add(app);
            }
        }
        return myApplications;
    }

    public List<InternshipOpportunity> viewAllInternships() {
        List<InternshipOpportunity> allInternships = new ArrayList<>();
        for (InternshipOpportunity opportunity : internshipRepository.getAllInternships()) {
            // Show all approved internships OR internships the student has applied to
            if (opportunity.getStatus().equals("Approved")) {
                boolean hasApplied = false;
                for (Application app : applicationRepository.getAllApplications()) {
                    if (app.getApplicant().getUserID().equals(this.userID) &&
                        app.getOpportunity().getOpportunityID().equals(opportunity.getOpportunityID())) {
                        hasApplied = true;
                        break;
                    }
                }
                // Show if visible OR if student has applied (even if visibility is off)
                if (opportunity.isVisible() || hasApplied) {
                    allInternships.add(opportunity);
                }
            }
        }
        return allInternships;
    }

    public void acceptInternship(String applicationID) {
        Application application = applicationRepository.getApplicationById(applicationID);
        if (application == null) {
            System.out.println("Application not found.");
            return;
        }
        
        if (!application.getApplicant().getUserID().equals(this.userID)) {
            System.out.println("This application does not belong to you.");
            return;
        }
        
        if (!application.getStatus().equals("Successful")) {
            System.out.println("You can only accept applications with 'Successful' status. Current status: " + application.getStatus());
            return;
        }
        
        // Check for overlapping confirmed internships
        InternshipOpportunity opportunity = application.getOpportunity();
        for (Application app : applicationRepository.getAllApplications()) {
            if (app.getApplicant().getUserID().equals(this.userID) &&
                app.getStatus().equals("Confirmed") &&
                !app.getApplicationID().equals(applicationID)) {
                InternshipOpportunity confirmedInternship = app.getOpportunity();
                // Check if dates overlap
                if (datesOverlap(opportunity.getOpeningDate(), opportunity.getClosingDate(),
                               confirmedInternship.getOpeningDate(), confirmedInternship.getClosingDate())) {
                    System.out.println("Cannot accept this internship: You already have a confirmed internship that overlaps with this period.");
                    System.out.println("Confirmed internship: " + confirmedInternship.getTitle() + 
                                     " (" + confirmedInternship.getOpeningDate() + " to " + confirmedInternship.getClosingDate() + ")");
                    System.out.println("This internship: " + opportunity.getTitle() + 
                                     " (" + opportunity.getOpeningDate() + " to " + opportunity.getClosingDate() + ")");
                    return;
                }
            }
        }
        
        // Check if internship is already full
        int confirmedCount = 0;
        for (Application app : applicationRepository.getAllApplications()) {
            if (app.getOpportunity().getOpportunityID().equals(opportunity.getOpportunityID()) &&
                app.getStatus().equals("Confirmed")) {
                confirmedCount++;
            }
        }
        
        if (confirmedCount >= opportunity.getMaxSlots()) {
            // Inform user and request explicit confirmation before queuing
            System.out.println("[INFO] The internship '" + opportunity.getTitle() + "' is currently full (" + confirmedCount + "/" + opportunity.getMaxSlots() + " slots filled).");
            System.out.print("Proceed to join the queue? (Y/N): ");
            Scanner confirmScanner = new Scanner(System.in);
            String confirm = confirmScanner.nextLine().trim().toLowerCase();
            // Do not close System.in backed scanner to avoid affecting other input operations
            if (!(confirm.equals("y") || confirm.equals("yes"))) {
                System.out.println("[CANCELLED] You chose not to join the queue. Your application remains in 'Successful' status.");
                return;
            }
            // Add to waitlist queue upon confirmation
            application.updateStatus("Queued");
            System.out.println("[QUEUED] You have been added to the waitlist. You will be automatically confirmed if a slot becomes available.");
            if (applicationRepository != null) {
                applicationRepository.saveApplications();
            }
            return;
        }
        
        // Accept the internship
        application.updateStatus("Confirmed");
        System.out.println("Internship accepted successfully!");
        
        // Withdraw only overlapping applications
        int withdrawnCount = 0;
        for (Application app : applicationRepository.getAllApplications()) {
            if (!app.getApplicationID().equals(applicationID) &&
                app.getApplicant().getUserID().equals(this.userID) &&
                !app.getStatus().equals("Withdrawn") &&
                !app.getStatus().equals("Confirmed")) {
                InternshipOpportunity otherOpportunity = app.getOpportunity();
                // Withdraw if dates overlap
                if (datesOverlap(opportunity.getOpeningDate(), opportunity.getClosingDate(),
                               otherOpportunity.getOpeningDate(), otherOpportunity.getClosingDate())) {
                    app.updateStatus("Withdrawn");
                    withdrawnCount++;
                }
            }
        }
        if (withdrawnCount > 0) {
            System.out.println(withdrawnCount + " overlapping application(s) have been automatically withdrawn.");
        }
        
        // Check if internship should be marked as Filled
        confirmedCount++;
        if (confirmedCount >= opportunity.getMaxSlots()) {
            opportunity.setStatus("Filled");
        }
        
        // Save changes through repositories
        if (applicationRepository != null) {
            applicationRepository.saveApplications();
        }
        if (internshipRepository != null) {
            internshipRepository.saveInternships();
        }
    }

    public void requestWithdrawal(String applicationID) {
        Application application = applicationRepository.getApplicationById(applicationID);
        if (application == null) {
            System.out.println("Application not found.");
            return;
        }
        
        if (!application.getApplicant().getUserID().equals(this.userID)) {
            System.out.println("This application does not belong to you.");
            return;
        }
        
        String currentStatus = application.getStatus();
        // Allow withdrawal for Pending, Successful, or Confirmed applications
        if (!currentStatus.equals("Pending") && 
            !currentStatus.equals("Successful") && 
            !currentStatus.equals("Confirmed")) {
            System.out.println("Cannot request withdrawal for application with status: " + currentStatus);
            return;
        }
        
        application.updateStatus("Withdrawal Requested");
        application.setManuallyWithdrawn(true);
        System.out.println("Withdrawal request submitted successfully.");
        
        if (applicationRepository != null) {
            applicationRepository.saveApplications();
        }
    }

    public int getYearOfStudy() {
        return yearOfStudy;
    }

    public String getMajor() {
        return major;
    }

    public double getGpa() {
        return gpa;
    }

    public boolean isEligibleForInternship(InternshipOpportunity opportunity) {
        return opportunity.isVisible() &&
               opportunity.getPreferredMajor().equalsIgnoreCase(this.major) &&
               isEligibleForLevel(opportunity.getLevel()) &&
               this.gpa >= opportunity.getMinGPA();
    }

    public String getIneligibilityReason(InternshipOpportunity opportunity) {
        if (!opportunity.isVisible()) {
            return "Internship is not visible";
        }
        if (!opportunity.getPreferredMajor().equalsIgnoreCase(this.major)) {
            return "Major mismatch: required " + opportunity.getPreferredMajor() + ", your major " + this.major;
        }
        if (!isEligibleForLevel(opportunity.getLevel())) {
            return "Level restriction: year 1-2 can only apply for Basic level";
        }
        if (this.gpa < opportunity.getMinGPA()) {
            return "GPA requirement not met: required " + opportunity.getMinGPA() + ", your GPA " + this.gpa;
        }
        return "Eligible";
    }

    private boolean datesOverlap(Date start1, Date end1, Date start2, Date end2) {
        // Two date ranges overlap if one starts before the other ends
        // Range 1: [start1, end1], Range 2: [start2, end2]
        // They overlap if: start1 <= end2 AND start2 <= end1
        return !start1.after(end2) && !start2.after(end1);
    }

    @Override
    public IMenuHandler createMenuHandler(
        InternshipService internshipService,
        ApplicationService applicationService,
        UserService userService,
        java.util.Scanner scanner
    ) {
        return new StudentMenuHandler(this, internshipService, applicationService, userService, scanner);
    }

    @Override
    public boolean isStudent() { return true; }
    
    @Override
    public Student asStudent() { return this; }
}