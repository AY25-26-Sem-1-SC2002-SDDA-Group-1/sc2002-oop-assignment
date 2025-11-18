public class FilterManager {
    private static FilterSettings filterSettings = new FilterSettings();
    private static final java.util.Scanner scanner = new java.util.Scanner(System.in);

    public static FilterSettings getFilterSettings() {
        return filterSettings;
    }

    public static boolean hasActiveFilters() {
        return filterSettings.hasActiveFilters();
    }

    public static void manageFilters() {
        UIHelper.printSectionHeader("MANAGE FILTERS");
        System.out.println("Current Settings:");
        System.out.println(filterSettings.toString());
        System.out.println();

        System.out.println("1. Set Status Filter (Pending/Approved/Rejected)");
        System.out.println("2. Set Level Filter (Undergraduate/Graduate/Both)");
        System.out.println("3. Set Major Filter (CS/EEE/BM/All)");
        System.out.println("4. Change Sort By (Title/Company/Level/Closing)");
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
                System.out.print("Enter level (Undergraduate/Graduate/Both) or leave blank: ");
                filterSettings.setLevelFilter(scanner.nextLine().trim());
                UIHelper.printSuccessMessage("Level filter updated!");
                break;
            case "3":
                System.out.print("Enter major (CS/EEE/BM/All) or leave blank: ");
                filterSettings.setMajorFilter(scanner.nextLine().trim());
                UIHelper.printSuccessMessage("Major filter updated!");
                break;
            case "4":
                System.out.print("Sort by (Title/Company/Level/Closing): ");
                String sortBy = scanner.nextLine().trim();
                if (!sortBy.isEmpty()) {
                    filterSettings.setSortBy(sortBy);
                    UIHelper.printSuccessMessage("Sort preference updated!");
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