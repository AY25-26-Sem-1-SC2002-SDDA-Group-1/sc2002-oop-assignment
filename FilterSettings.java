import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class FilterSettings {
    private String statusFilter = "";
    private String levelFilter = "";
    private String majorFilter = "";
    private double minGPAFilter = 0.0; // Minimum GPA filter
    private String sortBy = "title"; // Default sort by title (alphabetical)
    
    public void setStatusFilter(String status) { this.statusFilter = status; }
    public void setLevelFilter(String level) { this.levelFilter = level; }
    public void setMajorFilter(String major) { this.majorFilter = major; }
    public void setMinGPAFilter(double minGPA) { this.minGPAFilter = minGPA; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getStatusFilter() { return statusFilter; }
    public String getLevelFilter() { return levelFilter; }
    public String getMajorFilter() { return majorFilter; }
    public double getMinGPAFilter() { return minGPAFilter; }
    public String getSortBy() { return sortBy; }
    
    public boolean hasActiveFilters() {
        return !statusFilter.isEmpty() || !levelFilter.isEmpty() || !majorFilter.isEmpty() || minGPAFilter > 0.0;
    }
    
    public void clearFilters() {
        statusFilter = "";
        levelFilter = "";
        majorFilter = "";
        minGPAFilter = 0.0;
    }
    
    public List<InternshipOpportunity> applyFilters(List<InternshipOpportunity> opportunities) {
        return opportunities.stream()
            .filter(opp -> statusFilter.isEmpty() || opp.getStatus().equalsIgnoreCase(statusFilter))
            .filter(opp -> levelFilter.isEmpty() || opp.getLevel().equalsIgnoreCase(levelFilter))
            .filter(opp -> majorFilter.isEmpty() || opp.getPreferredMajor().equalsIgnoreCase(majorFilter))
            .filter(opp -> minGPAFilter == 0.0 || opp.getMinGPA() >= minGPAFilter)
            .sorted(getComparator())
            .collect(Collectors.toList());
    }
    
    private Comparator<InternshipOpportunity> getComparator() {
        switch (sortBy.toLowerCase()) {
            case "title":
                return Comparator.comparing(InternshipOpportunity::getTitle);
            case "company":
                return Comparator.comparing(opp -> opp.getCreatedBy().getCompanyName());
            case "level":
                return Comparator.comparing(InternshipOpportunity::getLevel);
            case "closing":
                return Comparator.comparing(InternshipOpportunity::getClosingDate);
            default:
                return Comparator.comparing(InternshipOpportunity::getTitle);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Active Filters: ");
        if (!statusFilter.isEmpty()) sb.append("Status=").append(statusFilter).append(" ");
        if (!levelFilter.isEmpty()) sb.append("Level=").append(levelFilter).append(" ");
        if (!majorFilter.isEmpty()) sb.append("Major=").append(majorFilter).append(" ");
        if (minGPAFilter > 0.0) sb.append("Min GPA>=").append(minGPAFilter).append(" ");
        sb.append("| Sort by: ").append(sortBy);
        return sb.toString();
    }
}