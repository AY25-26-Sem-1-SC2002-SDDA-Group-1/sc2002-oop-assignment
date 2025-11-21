import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Holds and applies filtering and sorting settings for internship opportunities.
 */
class FilterSettings {
    private String statusFilter = "";
    private String levelFilter = "";
    private String majorFilter = "";
    private double minGPAFilter = 0.0; // Minimum GPA filter
    private String sortBy = "title"; // Default sort by title (alphabetical)

    /**
     * Sets the status filter.
     *
     * @param status the status to filter by
     */
     /**
      * Sets the status filter.
      *
      * @param status the status to filter by
      */
     public void setStatusFilter(String status) { this.statusFilter = status; }

     /**
      * Sets the level filter.
      *
      * @param level the level to filter by
      */
     public void setLevelFilter(String level) { this.levelFilter = level; }

    /**
     * Sets the major filter.
     *
     * @param major the major to filter by
     */
    public void setMajorFilter(String major) { this.majorFilter = major; }

    /**
     * Sets the minimum GPA filter.
     *
     * @param minGPA the minimum GPA
     */
    public void setMinGPAFilter(double minGPA) { this.minGPAFilter = minGPA; }

    /**
     * Sets the sort by field.
     *
     * @param sortBy the field to sort by
     */
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    /**
     * Gets the status filter.
     *
     * @return the status filter
     */
    public String getStatusFilter() { return statusFilter; }

    /**
     * Gets the level filter.
     *
     * @return the level filter
     */
    public String getLevelFilter() { return levelFilter; }

    /**
     * Gets the major filter.
     *
     * @return the major filter
     */
    public String getMajorFilter() { return majorFilter; }

    /**
     * Gets the minimum GPA filter.
     *
     * @return the minimum GPA filter
     */
    public double getMinGPAFilter() { return minGPAFilter; }

    /**
     * Gets the sort by field.
     *
     * @return the sort by field
     */
    public String getSortBy() { return sortBy; }

    /**
     * Checks if there are active filters.
     *
     * @return true if filters are active
     */
    public boolean hasActiveFilters() {
        return !statusFilter.isEmpty() || (!levelFilter.isEmpty() && !levelFilter.equalsIgnoreCase("All")) || (!majorFilter.isEmpty() && !majorFilter.equalsIgnoreCase("All")) || minGPAFilter > 0.0;
    }

    /**
     * Clears all filters.
     */
    public void clearFilters() {
        statusFilter = "";
        levelFilter = "";
        majorFilter = "";
        minGPAFilter = 0.0;
    }

    /**
     * Applies the filters and sorting to a list of opportunities.
     *
     * @param opportunities the list of opportunities
     * @return the filtered and sorted list
     */
    public List<InternshipOpportunity> applyFilters(List<InternshipOpportunity> opportunities) {
        return opportunities.stream()
            .filter(opp -> statusFilter.isEmpty() || opp.getStatus().equalsIgnoreCase(statusFilter))
            .filter(opp -> levelFilter.isEmpty() || levelFilter.equalsIgnoreCase("All") || opp.getLevel().equalsIgnoreCase(levelFilter))
            .filter(opp -> majorFilter.isEmpty() || majorFilter.equalsIgnoreCase("All") || getMappedMajor(majorFilter).equalsIgnoreCase(opp.getPreferredMajor()))
            .filter(opp -> minGPAFilter == 0.0 || opp.getMinGPA() >= minGPAFilter)
            .sorted(getComparator())
            .collect(Collectors.toList());
    }

    /**
     * Gets the comparator for sorting.
     *
     * @return the comparator
     */
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

    /**
     * Maps major abbreviation to full name.
     *
     * @param abbr the abbreviation
     * @return the full name
     */
    private String getMappedMajor(String abbr) {
        switch (abbr.toUpperCase()) {
            case "CS": return "Computer Science";
            case "EEE": return "Computer Engineering";
            case "BM": return "Biomedical Engineering";
            case "DS": return "Data Science & AI";
            case "IEM": return "Information Engineering & Media";
            default: return abbr;
        }
    }

    /**
     * Returns a string representation of the filter settings.
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Active Filters: ");
        if (!statusFilter.isEmpty()) sb.append("Status=").append(statusFilter).append(" ");
        if (!levelFilter.isEmpty() && !levelFilter.equalsIgnoreCase("All")) sb.append("Level=").append(levelFilter).append(" ");
        if (!majorFilter.isEmpty() && !majorFilter.equalsIgnoreCase("All")) sb.append("Major=").append(majorFilter).append(" ");
        if (minGPAFilter > 0.0) sb.append("Min GPA>=").append(minGPAFilter).append(" ");
        sb.append("| Sort by: ").append(sortBy);
        return sb.toString();
    }
}