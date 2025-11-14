import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CareerCenterStaff extends User {
    private String staffDepartment;

    public CareerCenterStaff(String userID, String name, String password, String staffDepartment) {
        super(userID, name, password);
        this.staffDepartment = staffDepartment;
    }

    public void approveCompanyRep(String repID) {
        User user = Database.getUser(repID);
        if (user instanceof CompanyRepresentative) {
            ((CompanyRepresentative) user).setApproved(true);
        }
    }

    public void approveInternship(String opportunityID) {
        InternshipOpportunity opportunity = Database.getInternship(opportunityID);
        if (opportunity != null) {
            opportunity.setStatus("Approved");
        }
    }

    public void rejectInternship(String opportunityID) {
        InternshipOpportunity opportunity = Database.getInternship(opportunityID);
        if (opportunity != null) {
            opportunity.setStatus("Rejected");
        }
    }

    public void approveWithdrawal(String applicationID) {
        Application application = Database.getApplication(applicationID);
        if (application != null && application.getStatus().equals("Withdrawal Requested")) {
            application.updateStatus("Withdrawn");
        }
    }

    public void rejectWithdrawal(String applicationID) {
        Application application = Database.getApplication(applicationID);
        if (application != null && application.getStatus().equals("Withdrawal Requested")) {
            application.updateStatus("Pending");
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