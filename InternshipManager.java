import java.util.ArrayList;
import java.util.List;

public class InternshipManager {
    private static InternshipManager instance;
    
    private InternshipManager() {}
    
    public static InternshipManager getInstance() {
        if (instance == null) {
            instance = new InternshipManager();
        }
        return instance;
    }
    
    public boolean createInternship(CompanyRepresentative rep, String title, String description,
                                 String level, String preferredMajor, int maxSlots, double minimumGPA) {
        if (!rep.isApproved()) return false;

        // Check if rep has already created 5 internships
        int internshipCount = 0;
        for (InternshipOpportunity opp : Database.getInternships()) {
            if (opp.getCreatedBy().getUserID().equals(rep.getUserID())) {
                internshipCount++;
            }
        }
        if (internshipCount >= 5) return false;

        java.util.Date openingDate = new java.util.Date();
        java.util.Date closingDate = new java.util.Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);

        InternshipOpportunity opportunity = new InternshipOpportunity(
            Database.generateInternshipID(),
            title,
            description,
            level,
            preferredMajor,
            openingDate,
            closingDate,
            maxSlots,
            minimumGPA,
            rep
        );

        Database.addInternship(opportunity);
        return true;
    }
    
    public boolean approveInternship(String opportunityID) {
        InternshipOpportunity opportunity = Database.getInternship(opportunityID);
        if (opportunity == null) return false;
        
        opportunity.setStatus("Approved");
        return true;
    }
    
    public boolean rejectInternship(String opportunityID) {
        InternshipOpportunity opportunity = Database.getInternship(opportunityID);
        if (opportunity == null) return false;
        
        opportunity.setStatus("Rejected");
        return true;
    }
    
    public boolean toggleVisibility(String opportunityID, String repID, boolean visible) {
        InternshipOpportunity opportunity = Database.getInternship(opportunityID);
        if (opportunity == null) return false;
        
        if (!opportunity.getCreatedBy().getUserID().equals(repID)) return false;
        
        opportunity.setVisibility(visible);
        return true;
    }
    
    public List<InternshipOpportunity> getEligibleInternships(Student student) {
        List<InternshipOpportunity> eligible = new ArrayList<>();
        for (InternshipOpportunity opportunity : Database.getInternships()) {
            if (student.isEligibleForInternship(opportunity)) {
                eligible.add(opportunity);
            }
        }
        return eligible;
    }
    
    public List<InternshipOpportunity> getCompanyInternships(String repID) {
        List<InternshipOpportunity> companyInternships = new ArrayList<>();
        for (InternshipOpportunity opportunity : Database.getInternships()) {
            if (opportunity.getCreatedBy().getUserID().equals(repID)) {
                companyInternships.add(opportunity);
            }
        }
        return companyInternships;
    }
}