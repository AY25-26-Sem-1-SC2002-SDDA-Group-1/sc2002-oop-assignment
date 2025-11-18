import java.util.ArrayList;
import java.util.List;

public class ApplicationManager {
    private static ApplicationManager instance;
    
    private ApplicationManager() {}
    
    public static ApplicationManager getInstance() {
        if (instance == null) {
            instance = new ApplicationManager();
        }
        return instance;
    }
    
    public boolean createApplication(Student student, String opportunityID) {
        InternshipOpportunity opportunity = Database.getInternship(opportunityID);
        if (opportunity == null) return false;
        
        if (!student.isEligibleForInternship(opportunity)) return false;
        
        // Check if already applied
        for (Application app : Database.getApplications()) {
            if (app.getApplicant().getUserID().equals(student.getUserID()) && 
                app.getOpportunity().getOpportunityID().equals(opportunityID)) {
                return false;
            }
        }
        
        Application application = new Application(
            Database.generateApplicationID(),
            student,
            opportunity,
            "Pending"
        );
        
        Database.addApplication(application);
        return true;
    }
    
    public List<Application> getStudentApplications(String studentID) {
        List<Application> studentApplications = new ArrayList<>();
        for (Application app : Database.getApplications()) {
            if (app.getApplicant().getUserID().equals(studentID)) {
                studentApplications.add(app);
            }
        }
        return studentApplications;
    }
    
    public List<Application> getOpportunityApplications(String opportunityID) {
        List<Application> opportunityApplications = new ArrayList<>();
        for (Application app : Database.getApplications()) {
            if (app.getOpportunity().getOpportunityID().equals(opportunityID)) {
                opportunityApplications.add(app);
            }
        }
        return opportunityApplications;
    }
    
    public boolean acceptApplication(String applicationID, String studentID) {
        Application application = Database.getApplication(applicationID);
        if (application == null) return false;
        
        if (!application.getApplicant().getUserID().equals(studentID)) return false;
        
        if (!application.getStatus().equals("Successful")) return false;
        
        application.updateStatus("Confirmed");
        
        // Withdraw other applications
        for (Application app : Database.getApplications()) {
            if (!app.getApplicationID().equals(applicationID) &&
                app.getApplicant().getUserID().equals(studentID) &&
                !app.getStatus().equals("Withdrawn")) {
                app.updateStatus("Withdrawn");
            }
        }
        
        return true;
    }
    
    public boolean requestWithdrawal(String applicationID, String studentID) {
        Application application = Database.getApplication(applicationID);
        if (application == null) return false;
        
        if (!application.getApplicant().getUserID().equals(studentID)) return false;
        
        String currentStatus = application.getStatus();
        if (!currentStatus.equals("Pending") && 
            !currentStatus.equals("Successful") && 
            !currentStatus.equals("Confirmed")) {
            return false;
        }
        
        application.updateStatus("Withdrawal Requested");
        application.setManuallyWithdrawn(true);
        return true;
    }
    
    public boolean approveWithdrawal(String applicationID) {
        Application application = Database.getApplication(applicationID);
        if (application == null) return false;
        
        if (!application.getStatus().equals("Withdrawal Requested")) return false;
        
        application.updateStatus("Withdrawn");
        return true;
    }
    
    public boolean rejectWithdrawal(String applicationID) {
        Application application = Database.getApplication(applicationID);
        if (application == null) return false;
        
        if (!application.getStatus().equals("Withdrawal Requested")) return false;
        
        application.updateStatus("Pending");
        return true;
    }
}