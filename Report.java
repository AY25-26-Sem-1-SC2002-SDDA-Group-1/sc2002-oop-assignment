import java.util.List;
import java.util.Map;

public class Report {
    private final List<InternshipOpportunity> opportunities;
    private final Map<String, String> filters;

    public Report(List<InternshipOpportunity> opportunities, Map<String, String> filters) {
        this.opportunities = opportunities;
        this.filters = filters;
    }

    public void displayReport() {
        System.out.println("=== INTERNSHIP REPORT ===");
        System.out.println("Filters Applied: " + filters);
        System.out.println("Total Opportunities Found: " + opportunities.size());
        System.out.println();

        for (InternshipOpportunity opp : opportunities) {
            System.out.println("Opportunity ID: " + opp.getOpportunityID());
            System.out.println("Title: " + opp.getTitle());
            System.out.println("Company: " + opp.getCreatedBy().getCompanyName());
            System.out.println("Level: " + opp.getLevel());
            System.out.println("Preferred Major: " + opp.getPreferredMajor());
            System.out.println("Status: " + opp.getStatus());
            System.out.println("Max Slots: " + opp.getMaxSlots());
            System.out.println("Visible: " + opp.isVisibility());
            System.out.println("-------------------");
        }
    }

    public List<InternshipOpportunity> getOpportunities() {
        return opportunities;
    }

    public Map<String, String> getFilters() {
        return filters;
    }
}