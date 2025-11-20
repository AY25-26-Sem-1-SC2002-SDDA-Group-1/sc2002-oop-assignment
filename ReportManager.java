import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportManager {
    private static ReportManager instance;
    private IInternshipRepository internshipRepository;
    private IApplicationRepository applicationRepository;
    
    private ReportManager() {}
    
    public static ReportManager getInstance() {
        if (instance == null) {
            instance = new ReportManager();
        }
        return instance;
    }
    
    // Initialize with repositories (must be called before using the manager)
    public void initialize(IInternshipRepository internshipRepository, IApplicationRepository applicationRepository) {
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
    }
    
    public Report generateReport(Map<String, String> filters) {
        if (internshipRepository == null) {
            System.out.println("Error: ReportManager not initialized with repositories.");
            return new Report(new ArrayList<>(), filters);
        }
        
        List<InternshipOpportunity> filteredOpportunities = new ArrayList<>();
        
        for (InternshipOpportunity opportunity : internshipRepository.getAllInternships()) {
            boolean matches = true;
            
            if (filters.containsKey("status") && 
                !opportunity.getStatus().equalsIgnoreCase(filters.get("status"))) {
                matches = false;
            }
            
            if (filters.containsKey("level") && 
                !opportunity.getLevel().equalsIgnoreCase(filters.get("level"))) {
                matches = false;
            }
            
            if (filters.containsKey("preferredMajor") && 
                !opportunity.getPreferredMajor().equalsIgnoreCase(filters.get("preferredMajor"))) {
                matches = false;
            }
            
            if (filters.containsKey("company") && 
                !opportunity.getCreatedBy().getCompanyName().equalsIgnoreCase(filters.get("company"))) {
                matches = false;
            }
            
            if (matches) {
                filteredOpportunities.add(opportunity);
            }
        }
        
        return new Report(filteredOpportunities, filters);
    }
    
    public void displayDetailedReport(Report report) {
        System.out.println("\n=== DETAILED INTERNSHIP REPORT ===");
        System.out.println("Filters Applied: " + report.getFilters());
        System.out.println("Total Opportunities Found: " + report.getOpportunities().size());
        System.out.println();

        if (report.getOpportunities().isEmpty()) {
            System.out.println("No internships match the specified criteria.");
            return;
        }

        for (InternshipOpportunity opp : report.getOpportunities()) {
            System.out.println("┌─────────────────────────────────────────────────────────────┐");
            System.out.println("│ " + opp.getTitle());
            System.out.println("├─────────────────────────────────────────────────────────────┤");
            System.out.println("│ ID: " + opp.getOpportunityID());
            System.out.println("│ Company: " + opp.getCreatedBy().getCompanyName());
            System.out.println("│ Level: " + opp.getLevel());
            System.out.println("│ Major: " + opp.getPreferredMajor());
            System.out.println("│ Min GPA: " + opp.getMinGPA());
            System.out.println("│ Status: " + opp.getStatus());
            System.out.println("│ Slots: " + opp.getMaxSlots());
            System.out.println("│ Visible: " + (opp.isVisibility() ? "Yes" : "No"));
            System.out.println("│ Opening: " + opp.getOpeningDate());
            System.out.println("│ Closing: " + opp.getClosingDate());
            System.out.println("└─────────────────────────────────────────────────────────────┘");
            System.out.println();
            
            // Show application statistics for this internship
            if (applicationRepository != null) {
                int totalApps = 0, pendingApps = 0, successfulApps = 0, confirmedApps = 0;
                for (Application app : applicationRepository.getAllApplications()) {
                    if (app.getOpportunity().getOpportunityID().equals(opp.getOpportunityID())) {
                        totalApps++;
                        switch (app.getStatus()) {
                            case "Pending":
                                pendingApps++;
                                break;
                            case "Successful":
                                successfulApps++;
                                break;
                            case "Confirmed":
                                confirmedApps++;
                                break;
                        }
                    }
                }
                System.out.println("  Applications: " + totalApps + " (Pending: " + pendingApps + 
                                 ", Successful: " + successfulApps + ", Confirmed: " + confirmedApps + ")");
            }
            System.out.println();
        }
    }
    
    public Map<String, Integer> getApplicationStatistics() {
        if (applicationRepository == null) {
            System.out.println("Error: ReportManager not initialized with repositories.");
            return new HashMap<>();
        }
        
        Map<String, Integer> stats = new HashMap<>();
        int total = 0, pending = 0, successful = 0, unsuccessful = 0, confirmed = 0, withdrawn = 0;
        
        for (Application app : applicationRepository.getAllApplications()) {
            total++;
            switch (app.getStatus()) {
                case "Pending":
                    pending++;
                    break;
                case "Successful":
                    successful++;
                    break;
                case "Unsuccessful":
                    unsuccessful++;
                    break;
                case "Confirmed":
                    confirmed++;
                    break;
                case "Withdrawn":
                    withdrawn++;
                    break;
            }
        }
        
        stats.put("Total", total);
        stats.put("Pending", pending);
        stats.put("Successful", successful);
        stats.put("Unsuccessful", unsuccessful);
        stats.put("Confirmed", confirmed);
        stats.put("Withdrawn", withdrawn);
        
        return stats;
    }
    
    public Map<String, Integer> getInternshipStatistics() {
        if (internshipRepository == null) {
            System.out.println("Error: ReportManager not initialized with repositories.");
            return new HashMap<>();
        }
        
        Map<String, Integer> stats = new HashMap<>();
        int total = 0, pending = 0, approved = 0, rejected = 0, filled = 0;
        
        for (InternshipOpportunity opp : internshipRepository.getAllInternships()) {
            total++;
            switch (opp.getStatus()) {
                case "Pending":
                    pending++;
                    break;
                case "Approved":
                    approved++;
                    break;
                case "Rejected":
                    rejected++;
                    break;
                case "Filled":
                    filled++;
                    break;
            }
        }
        
        stats.put("Total", total);
        stats.put("Pending", pending);
        stats.put("Approved", approved);
        stats.put("Rejected", rejected);
        stats.put("Filled", filled);
        
        return stats;
    }
}