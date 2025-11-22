import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton manager for generating and displaying internship reports.
 */
public class ReportManager {
    private static ReportManager instance;
    private IInternshipRepository internshipRepository;
    private IApplicationRepository applicationRepository;

    private ReportManager() {}

    /**
     * Gets the singleton instance of ReportManager.
     *
     * @return the instance
     */
    public static ReportManager getInstance() {
        if (instance == null) {
            instance = new ReportManager();
        }
        return instance;
    }

    /**
     * Initializes the ReportManager with repositories.
     *
     * @param internshipRepository the internship repository
     * @param applicationRepository the application repository
     */
    public void initialize(IInternshipRepository internshipRepository, IApplicationRepository applicationRepository) {
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
    }

    /**
     * Generates a report based on filters.
     *
     * @param filters the filters to apply
     * @return the generated report
     */
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

    /**
     * Displays a detailed report.
     *
     * @param report the report to display
     */
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
                int totalApps = 0, pendingApps = 0, successfulApps = 0, confirmedApps = 0, unsuccessfulApps = 0, withdrawnApps = 0, withdrawalRequestedApps = 0;
                for (Application app : applicationRepository.getAllApplications()) {
                    if (app.getOpportunity().getOpportunityID().equals(opp.getOpportunityID())) {
                        totalApps++;
                        switch (app.getStatusEnum()) {
                            case PENDING:
                                pendingApps++;
                                break;
                            case SUCCESSFUL:
                                successfulApps++;
                                break;
                            case CONFIRMED:
                                confirmedApps++;
                                break;
                            case UNSUCCESSFUL:
                                unsuccessfulApps++;
                                break;
                            case WITHDRAWN:
                                withdrawnApps++;
                                break;
                            case WITHDRAWAL_REQUESTED:
                                withdrawalRequestedApps++;
                                break;
                            case WITHDRAWAL_REJECTED:
                                // Withdrawal rejected, status reverted, count as unsuccessful for now
                                unsuccessfulApps++;
                                break;
                        }
                    }
                }
                System.out.println("  Applications: " + totalApps + " (Pending: " + pendingApps + 
                                 ", Successful: " + successfulApps + ", Confirmed: " + confirmedApps +
                                 ", Unsuccessful: " + unsuccessfulApps + ", Withdrawn: " + withdrawnApps +
                                 ", Withdrawal Requested: " + withdrawalRequestedApps + ")");
            }
            System.out.println();
        }
    }

    /**
     * Gets application statistics.
     *
     * @return map of application status counts
     */
    public Map<String, Integer> getApplicationStatistics() {
        if (applicationRepository == null) {
            System.out.println("Error: ReportManager not initialized with repositories.");
            return new HashMap<>();
        }
        
        Map<String, Integer> stats = new HashMap<>();
        int total = 0, pending = 0, successful = 0, unsuccessful = 0, confirmed = 0, withdrawn = 0;
        
        for (Application app : applicationRepository.getAllApplications()) {
            total++;
            switch (app.getStatusEnum()) {
                case PENDING:
                    pending++;
                    break;
                case SUCCESSFUL:
                    successful++;
                    break;
                case UNSUCCESSFUL:
                    unsuccessful++;
                    break;
                case CONFIRMED:
                    confirmed++;
                    break;
                case WITHDRAWN:
                    withdrawn++;
                    break;
                case WITHDRAWAL_REQUESTED:
                    // Count as pending or separate, but for now pending
                    pending++;
                    break;
                case WITHDRAWAL_REJECTED:
                    // Reverted, count as unsuccessful
                    unsuccessful++;
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

    /**
     * Gets internship statistics.
     *
     * @return map of internship status counts
     */
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