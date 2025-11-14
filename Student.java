import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    private int yearOfStudy;
    private String major;

    public Student(String userID, String name, String password, int yearOfStudy, String major) {
        super(userID, name, password);
        this.yearOfStudy = yearOfStudy;
        this.major = major;
    }

    public List<InternshipOpportunity> viewEligibleInternships() {
        List<InternshipOpportunity> eligible = new ArrayList<>();
        for (InternshipOpportunity opportunity : Database.getInternships()) {
            if (opportunity.isOpen() && opportunity.isVisible() && 
                opportunity.getPreferredMajor().equalsIgnoreCase(this.major)) {
                eligible.add(opportunity);
            }
        }
        return eligible;
    }

    public boolean applyForInternship(String opportunityID) {
        InternshipOpportunity opportunity = Database.getInternship(opportunityID);
        if (opportunity == null) return false;
        
        if (!opportunity.isOpen() || !opportunity.isVisible()) return false;
        
        if (!opportunity.getPreferredMajor().equalsIgnoreCase(this.major)) return false;
        
        for (Application app : Database.getApplications()) {
            if (app.getApplicant().getUserID().equals(this.userID) && 
                app.getOpportunity().getOpportunityID().equals(opportunityID)) {
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

    public void acceptInternship(String applicationID) {
        Application application = Database.getApplication(applicationID);
        if (application != null && 
            application.getApplicant().getUserID().equals(this.userID) &&
            application.getStatus().equals("Successful")) {
            
            application.updateStatus("Confirmed");
            
            for (Application app : Database.getApplications()) {
                if (!app.getApplicationID().equals(applicationID) &&
                    app.getApplicant().getUserID().equals(this.userID) &&
                    !app.getStatus().equals("Withdrawn")) {
                    app.updateStatus("Withdrawn");
                }
            }
            
            InternshipOpportunity opportunity = application.getOpportunity();
            int confirmedCount = 0;
            for (Application app : Database.getApplications()) {
                if (app.getOpportunity().getOpportunityID().equals(opportunity.getOpportunityID()) &&
                    app.getStatus().equals("Confirmed")) {
                    confirmedCount++;
                }
            }
            if (confirmedCount >= opportunity.getMaxSlots()) {
                opportunity.setStatus("Filled");
            }
        }
    }

    public void requestWithdrawal(String applicationID) {
        Application application = Database.getApplication(applicationID);
        if (application != null && 
            application.getApplicant().getUserID().equals(this.userID)) {
            application.updateStatus("Withdrawal Requested");
        }
    }

    public int getYearOfStudy() {
        return yearOfStudy;
    }

    public String getMajor() {
        return major;
    }
}