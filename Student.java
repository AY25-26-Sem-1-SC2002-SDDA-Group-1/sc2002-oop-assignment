import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    private final int yearOfStudy;
    private final String major;
    private final double gpa;

    public Student(String userID, String name, String password, int yearOfStudy, String major, double gpa) {
        super(userID, name, password);
        this.yearOfStudy = yearOfStudy;
        this.major = major;
        this.gpa = gpa;
    }

    public List<InternshipOpportunity> viewEligibleInternships() {
        List<InternshipOpportunity> eligible = new ArrayList<>();
        for (InternshipOpportunity opportunity : Database.getInternships()) {
            if (opportunity.isVisible() &&
                opportunity.getPreferredMajor().equalsIgnoreCase(this.major) &&
                isEligibleForLevel(opportunity.getLevel()) &&
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
        for (Application app : Database.getApplications()) {
            if (app.getApplicant().getUserID().equals(this.userID) &&
                !app.getStatus().equals("Withdrawn") &&
                !app.getStatus().equals("Unsuccessful")) {
                count++;
            }
        }
        return count;
    }

    public boolean applyForInternship(String opportunityID) {
        InternshipOpportunity opportunity = Database.getInternship(opportunityID);
        if (opportunity == null) return false;
        
        // Check if student already has 3 active applications
        if (getActiveApplicationCount() >= 3) return false;
        
        if (!opportunity.isOpen() || !opportunity.isVisible()) return false;
        
        if (!opportunity.getPreferredMajor().equalsIgnoreCase(this.major)) return false;

        // Check if student is eligible for this level
        if (!isEligibleForLevel(opportunity.getLevel())) return false;

        // Check GPA requirement
        if (this.gpa < opportunity.getMinGPA()) return false;
        
        // Check if already applied
        for (Application app : Database.getApplications()) {
            if (app.getApplicant().getUserID().equals(this.userID) && 
                app.getOpportunity().getOpportunityID().equals(opportunityID)) {
                return false;
            }
        }
        
        // Check if student manually withdrew from this internship before
        for (Application app : Database.getApplications()) {
            if (app.getApplicant().getUserID().equals(this.userID) &&
                app.getOpportunity().getOpportunityID().equals(opportunityID) &&
                app.isManuallyWithdrawn()) {
                return false;
            }
        }
        
        Application application = new Application(
            Database.generateApplicationID(),
            this,
            opportunity,
            "Pending"
        );
        
        Database.addApplication(application);
        return true;
    }

    public List<Application> viewApplications() {
        List<Application> myApplications = new ArrayList<>();
        for (Application app : Database.getApplications()) {
            if (app.getApplicant().getUserID().equals(this.userID)) {
                myApplications.add(app);
            }
        }
        return myApplications;
    }

    public List<InternshipOpportunity> viewAllInternships() {
        List<InternshipOpportunity> allInternships = new ArrayList<>();
        for (InternshipOpportunity opportunity : Database.getInternships()) {
            // Show all approved internships OR internships the student has applied to
            if (opportunity.getStatus().equals("Approved")) {
                boolean hasApplied = false;
                for (Application app : Database.getApplications()) {
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
        Application application = Database.getApplication(applicationID);
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
        
        // Check if internship is already full
        InternshipOpportunity opportunity = application.getOpportunity();
        int confirmedCount = 0;
        for (Application app : Database.getApplications()) {
            if (app.getOpportunity().getOpportunityID().equals(opportunity.getOpportunityID()) &&
                app.getStatus().equals("Confirmed")) {
                confirmedCount++;
            }
        }
        
        if (confirmedCount >= opportunity.getMaxSlots()) {
            // Add to waitlist queue
            application.updateStatus("Queued");
            System.out.println("This internship is currently full (" + confirmedCount + "/" + opportunity.getMaxSlots() + " slots filled).");
            System.out.println("You have been added to the waitlist. You will be automatically confirmed if a slot becomes available.");
            Database.saveData();
            return;
        }
        
        // Accept the internship
        application.updateStatus("Confirmed");
        System.out.println("Internship accepted successfully!");
        
        // Withdraw all other applications
        for (Application app : Database.getApplications()) {
            if (!app.getApplicationID().equals(applicationID) &&
                app.getApplicant().getUserID().equals(this.userID) &&
                !app.getStatus().equals("Withdrawn")) {
                app.updateStatus("Withdrawn");
            }
        }
        
        // Check if internship should be marked as Filled
        confirmedCount++;
        if (confirmedCount >= opportunity.getMaxSlots()) {
            opportunity.setStatus("Filled");
        }
        
        Database.saveData();
    }

    public void requestWithdrawal(String applicationID) {
        Application application = Database.getApplication(applicationID);
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
        Database.saveData();
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
}