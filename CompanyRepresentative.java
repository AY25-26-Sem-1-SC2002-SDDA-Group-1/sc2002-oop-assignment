import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CompanyRepresentative extends User {
    private String companyName;
    private String department;
    private String position;
    private String email;
    private boolean isApproved;

    public CompanyRepresentative(String userID, String name, String password, 
                                String companyName, String department, String position, String email) {
        super(userID, name, password);
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.email = email;
        this.isApproved = false;
    }

    public boolean createInternship(String title, String description, String level, 
                                  String preferredMajor, Date openingDate, Date closingDate, 
                                  int maxSlots) {
        if (!isApproved) return false;
        
        // Check if company rep has already created 5 internships
        int internshipCount = 0;
        for (InternshipOpportunity opp : Database.getInternships()) {
            if (opp.getCreatedBy().getUserID().equals(this.userID)) {
                internshipCount++;
            }
        }
        if (internshipCount >= 5) return false;
        
        // Validate max slots (max of 10)
        if (maxSlots > 10 || maxSlots < 1) return false;

        InternshipOpportunity opportunity = new InternshipOpportunity(
            Database.generateInternshipID(),
            title,
            description,
            level,
            preferredMajor,
            openingDate,
            closingDate,
            maxSlots,
            this
        );

        Database.addInternship(opportunity);
        return true;
    }

    public List<Application> viewApplications() {
        List<Application> myApplications = new ArrayList<>();
        for (Application app : Database.getApplications()) {
            if (app.getOpportunity().getCreatedBy().getUserID().equals(this.userID)) {
                myApplications.add(app);
            }
        }
        return myApplications;
    }

    public List<Application> viewApplications(String opportunityID) {
        List<Application> opportunityApplications = new ArrayList<>();
        for (Application app : Database.getApplications()) {
            if (app.getOpportunity().getOpportunityID().equals(opportunityID) &&
                app.getOpportunity().getCreatedBy().getUserID().equals(this.userID)) {
                opportunityApplications.add(app);
            }
        }
        return opportunityApplications;
    }

    public boolean approveApplication(String applicationID) {
        Application application = Database.getApplication(applicationID);
        if (application != null && 
            application.getOpportunity().getCreatedBy().getUserID().equals(this.userID)) {
            application.updateStatus("Successful");
            return true;
        }
        return false;
    }

    public boolean rejectApplication(String applicationID) {
        Application application = Database.getApplication(applicationID);
        if (application != null && 
            application.getOpportunity().getCreatedBy().getUserID().equals(this.userID)) {
            application.updateStatus("Unsuccessful");
            return true;
        }
        return false;
    }

    public void toggleVisibility(String opportunityID, boolean visible) {
        InternshipOpportunity opportunity = Database.getInternship(opportunityID);
        if (opportunity != null && 
            opportunity.getCreatedBy().getUserID().equals(this.userID)) {
            opportunity.setVisibility(visible);
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

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }
}