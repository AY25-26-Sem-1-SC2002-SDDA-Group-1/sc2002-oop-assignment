/**
 * Manages filtering and sorting settings for internship listings.
 * Supports user-type specific menu customization, such as hiding irrelevant
 * options for company representatives (e.g., no "Company" sort option).
 */
public class FilterManager {
    private final FilterSettings filterSettings;
    private final java.util.Scanner scanner;
    private final String userType;

    /**
     * Constructs a FilterManager.
     *
     * @param scanner the scanner for input
     */
    public FilterManager(java.util.Scanner scanner) {
        this(scanner, "default");
    }

    /**
     * Constructs a FilterManager with user type.
     *
     * @param scanner the scanner for input
     * @param userType the type of user ("companyrep", "staff", "student", "default")
     */
    public FilterManager(java.util.Scanner scanner, String userType) {
        this.filterSettings = new FilterSettings();
        this.scanner = scanner;
        this.userType = userType;
    }

    /**
     * Gets the filter settings.
     *
     * @return the filter settings
     */
    public FilterSettings getFilterSettings() {
        return filterSettings;
    }

    /**
     * Checks if there are active filters.
     *
     * @return true if filters are active
     */
    public boolean hasActiveFilters() {
        return filterSettings.hasActiveFilters();
    }

    /**
     * Displays the filter management menu and handles user input.
     * Provides different menu options based on user type:
     * - Company reps: No "Company" sort option since they only see their own internships
     * - Other users: Full sort options including "Company"
     * Handles setting status, level, major filters and sort preferences.
     */
    public void manageFilters() {
        UIHelper.printSectionHeader("MANAGE FILTERS");
        System.out.println("Current Settings:");
        System.out.println(filterSettings.toString());
        System.out.println();

        System.out.println("1. Set Status Filter (Pending/Approved/Rejected)");
        System.out.println("2. Set Level Filter (Basic/Intermediate/Advanced/All)");
        System.out.println("3. Set Major Filter (CS/EEE/BM/DS/IEM/All)");
        
        // Company reps don't need company filter since they only see their own
        if ("companyrep".equals(userType)) {
            System.out.println("4. Change Sort By (Title/Level/Closing)");
        } else {
            System.out.println("4. Change Sort By (Title/Company/Level/Closing)");
        }
        
        System.out.println("5. Clear All Filters");
        System.out.println("6. Back to Main Menu");
        System.out.print("\nEnter your choice: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                System.out.print("Enter status (Pending/Approved/Rejected) or leave blank: ");
                filterSettings.setStatusFilter(scanner.nextLine().trim());
                UIHelper.printSuccessMessage("Status filter updated!");
                break;
            case "2":
                System.out.print("Enter level (Basic/Intermediate/Advanced/All) or leave blank: ");
                filterSettings.setLevelFilter(scanner.nextLine().trim());
                UIHelper.printSuccessMessage("Level filter updated!");
                break;
            case "3":
                System.out.print("Enter major (CS/EEE/BM/DS/IEM/All) or leave blank: ");
                filterSettings.setMajorFilter(scanner.nextLine().trim());
                UIHelper.printSuccessMessage("Major filter updated!");
                break;
            case "4":
                if ("companyrep".equals(userType)) {
                    System.out.print("Sort by (Title/Level/Closing): ");
                } else {
                    System.out.print("Sort by (Title/Company/Level/Closing): ");
                }
                String sortBy = scanner.nextLine().trim();
                if (!sortBy.isEmpty()) {
                    // Prevent company reps from sorting by company
                    if ("companyrep".equals(userType) && sortBy.equalsIgnoreCase("Company")) {
                        UIHelper.printErrorMessage("Cannot sort by company - you only see your own internships.");
                    } else {
                        filterSettings.setSortBy(sortBy);
                        UIHelper.printSuccessMessage("Sort preference updated!");
                    }
                }
                break;
            case "5":
                filterSettings.clearFilters();
                UIHelper.printSuccessMessage("All filters cleared!");
                break;
            case "6":
                return;
            default:
                UIHelper.printErrorMessage("Invalid choice.");
        }
    }
}