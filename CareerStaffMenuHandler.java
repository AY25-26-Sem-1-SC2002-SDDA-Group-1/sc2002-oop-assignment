import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Menu handler for career center staff operations.
 * Provides interface for processing company reps, internships, withdrawals, and reports.
 */
public class CareerStaffMenuHandler implements IMenuHandler {
    private final CareerCenterStaff staff;
    private final IUserService userService;
    private final IInternshipService internshipService;
    private final IApplicationService applicationService;
    private final Scanner scanner;
    private final FilterManager filterManager;

    /**
     * Constructs a CareerStaffMenuHandler.
     *
     * @param staff the career center staff
     * @param userService the user service
     * @param internshipService the internship service
     * @param applicationService the application service
     * @param scanner the scanner for input
     */
    public CareerStaffMenuHandler(CareerCenterStaff staff, IUserService userService, IInternshipService internshipService, IApplicationService applicationService, Scanner scanner) {
        this.staff = staff;
        this.userService = userService;
        this.internshipService = internshipService;
        this.applicationService = applicationService;
        this.scanner = scanner;
        this.filterManager = new FilterManager(scanner);
    }

    /**
     * Displays the menu and handles user choices.
     */
    @Override
    public void showMenu() {
        UIHelper.printCareerStaffMenu();
        System.out.println("1. Process Company Representatives");
        System.out.println("2. Process Internships");
        System.out.println("3. Process Withdrawal Requests");
        System.out.println("4. View All Internships (Filtered)");
        System.out.println("5. Manage Filters");
        System.out.println("6. Generate Reports");
        System.out.println("7. Change Password");
        System.out.println("8. Logout");
        System.out.print("\nEnter your choice: ");

        try {
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    processCompanyReps();
                    break;
                case "2":
                    processInternships();
                    break;
                case "3":
                    processWithdrawals();
                    break;
                case "4":
                    viewAllInternshipsFiltered();
                    break;
                case "5":
                    filterManager.manageFilters();
                    break;
                case "6":
                    generateReports();
                    break;
                case "7":
                    changePassword();
                    break;
                case "8":
                    logout();
                    break;
                default:
                    UIHelper.printErrorMessage("Invalid choice. Please try again.");
            }
        } catch (Exception e) {
            UIHelper.printErrorMessage("Error reading input. Please try again.");
        }
    }

    private void processCompanyReps() {
        UIHelper.printSectionHeader("PROCESS COMPANY REPRESENTATIVES");

        List<CompanyRepresentative> pendingReps = staff.getPendingCompanyReps();

        if (pendingReps.isEmpty()) {
            UIHelper.printWarningMessage("No pending company representatives to process.");
            return;
        }

        // Display all pending company representatives
        System.out.println("\nPending Company Representatives:");
        for (CompanyRepresentative rep : pendingReps) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("ID: " + rep.getUserID());
            System.out.println("Name: " + rep.getName());
            System.out.println("Company: " + rep.getCompanyName());
            System.out.println("Department: " + rep.getDepartment());
            System.out.println("Position: " + rep.getPosition());
            System.out.println("Email: " + rep.getEmail());
            System.out.println("=".repeat(50));
        }

        System.out.print("\nEnter Company Representative ID to process (or 'cancel' to go back): ");
        String repID = scanner.nextLine().trim();

        if (repID.equalsIgnoreCase("cancel")) {
            UIHelper.printWarningMessage("Operation cancelled.");
            return;
        }

        if (repID.isEmpty()) {
            UIHelper.printErrorMessage("ID cannot be empty.");
            return;
        }

        // Verify the rep exists in pending list
        boolean found = false;
        for (CompanyRepresentative rep : pendingReps) {
            if (rep.getUserID().equals(repID)) {
                found = true;
                break;
            }
        }

        if (!found) {
            UIHelper.printErrorMessage("Invalid ID or company representative is not pending.");
            return;
        }

        System.out.print("Decision (approve/reject): ");
        String decision = scanner.nextLine().trim().toLowerCase();

        if (decision.equals("approve")) {
            if (staff.processCompanyRep(repID, true)) {
                UIHelper.printSuccessMessage("Company representative approved successfully.");
            } else {
                UIHelper.printErrorMessage("Failed to approve company representative.");
            }
        } else if (decision.equals("reject")) {
            if (staff.processCompanyRep(repID, false)) {
                UIHelper.printWarningMessage("Company representative registration rejected");
            } else {
                UIHelper.printErrorMessage("Failed to reject company representative.");
            }
        } else {
            UIHelper.printErrorMessage("Invalid decision. Please enter 'approve' or 'reject'.");
        }
    }

    private void processInternships() {
        UIHelper.printSectionHeader("PROCESS INTERNSHIPS");

        // Get pending internships from service layer instead of domain object
        List<InternshipOpportunity> pendingInternships = internshipService.getAllInternships().stream()
            .filter(opp -> opp.getStatus().equals("Pending"))
            .toList();

        if (pendingInternships.isEmpty()) {
            UIHelper.printWarningMessage("No pending internships to process.");
            return;
        }

        // Display all pending internships
        System.out.println("\nPending Internships:");
        int index = 1;
        for (InternshipOpportunity opp : pendingInternships) {
            System.out.println("\n" + index + ". " + "=".repeat(50));
            System.out.println("ID: " + opp.getOpportunityID());
            System.out.println("Title: " + opp.getTitle());
            System.out.println("Description: " + opp.getDescription());
            System.out.println("Company: " + opp.getCreatedBy().getCompanyName());
            System.out.println("Requested by: " + opp.getCreatedBy().getName() + " (" + opp.getCreatedBy().getPosition() + ")");
            System.out.println("Level: " + opp.getLevel());
            System.out.println("Preferred Major: " + opp.getPreferredMajor());
            System.out.println("Min GPA Required: " + opp.getMinGPA());
            System.out.println("Max Slots: " + opp.getMaxSlots());
            System.out.println("Opening Date: " + opp.getOpeningDate());
            System.out.println("Closing Date: " + opp.getClosingDate());
            System.out.println("=".repeat(50));
            index++;
        }

        System.out.print("\nEnter Internship numbers or IDs (space-separated, e.g., 1 2 or INT001 INT002) or 'cancel' to go back: ");
        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("cancel")) {
            UIHelper.printWarningMessage("Operation cancelled.");
            return;
        }

        if (input.isEmpty()) {
            UIHelper.printErrorMessage("Input cannot be empty.");
            return;
        }

        String[] inputs = input.split("\\s+");
        int successCount = 0;
        int failCount = 0;

        for (String inp : inputs) {
            inp = inp.trim();
            String internshipID = null;
            try {
                int num = Integer.parseInt(inp);
                if (num >= 1 && num <= pendingInternships.size()) {
                    internshipID = pendingInternships.get(num - 1).getOpportunityID();
                } else {
                    System.out.println("[SKIP] " + inp + ": Invalid number");
                    failCount++;
                    continue;
                }
            } catch (NumberFormatException e) {
                // treat as ID
                internshipID = inp;
            }

            // Verify internship exists in pending list
            boolean found = false;
            for (InternshipOpportunity opp : pendingInternships) {
                if (opp.getOpportunityID().equalsIgnoreCase(internshipID)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("[SKIP] " + internshipID + ": Invalid ID or not pending.");
                failCount++;
                continue;
            }

            System.out.print("Decision for " + internshipID + " (approve/reject): ");
            String decision = scanner.nextLine().trim().toLowerCase();

            if (!decision.equals("approve") && !decision.equals("a") && !decision.equals("reject") && !decision.equals("r")) {
                UIHelper.printErrorMessage("Invalid decision. Please enter 'approve'/'a' or 'reject'/'r'.");
                failCount++;
                continue;
            }

            boolean isApprove = decision.equals("approve") || decision.equals("a");

            if (isApprove) {
                internshipService.approveInternship(internshipID);
                System.out.println("[APPROVED] " + internshipID + ": Internship approved and published.");
            } else {
                internshipService.rejectInternship(internshipID);
                System.out.println("[REJECTED] " + internshipID + ": Internship rejected.");
            }

            successCount++;
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.println("Processing complete: " + successCount + " succeeded, " + failCount + " failed.");
        System.out.println("=".repeat(50));
    }

    private void processWithdrawals() {
        UIHelper.printSectionHeader("PROCESS WITHDRAWAL REQUESTS");

        List<Application> withdrawalRequests = staff.getWithdrawalRequests();

        if (withdrawalRequests.isEmpty()) {
            UIHelper.printWarningMessage("No withdrawal requests to process.");
            return;
        }

        // Display all withdrawal requests
        System.out.println("\nWithdrawal Requests:");
        int index = 1;
        for (Application app : withdrawalRequests) {
            System.out.println("\n" + index + ". " + "=".repeat(50));
            System.out.println("Application ID: " + app.getApplicationID());
            System.out.println("Student: " + app.getApplicant().getName());
            System.out.println("Student ID: " + app.getApplicant().getUserID());
            System.out.println("Internship: " + app.getOpportunity().getTitle());
            System.out.println("Company: " + app.getOpportunity().getCreatedBy().getCompanyName());
            System.out.println("Applied Date: " + app.getAppliedDate());
            System.out.println("=".repeat(50));
            index++;
        }

        System.out.print("\nEnter number or Application ID (e.g., 1 or APP001) or 'cancel' to go back: ");
        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("cancel")) {
            UIHelper.printWarningMessage("Operation cancelled.");
            return;
        }

        if (input.isEmpty()) {
            UIHelper.printErrorMessage("Input cannot be empty.");
            return;
        }

    Application selectedApp = null;
    try {
        int num = Integer.parseInt(input);
        if (num >= 1 && num <= withdrawalRequests.size()) {
            selectedApp = withdrawalRequests.get(num - 1);
        } else {
            UIHelper.printErrorMessage("Invalid number.");
            return;
        }
    } catch (NumberFormatException e) {
        // treat as ID
        selectedApp = withdrawalRequests.stream()
            .filter(a -> a.getApplicationID().equalsIgnoreCase(input))
            .findFirst()
            .orElse(null);
    }

    if (selectedApp == null) {
        UIHelper.printErrorMessage("Invalid application selection.");
        return;
    }

    String applicationID = selectedApp.getApplicationID();

        System.out.print("Decision for " + applicationID + " (approve/reject): ");
        String decision = scanner.nextLine().trim().toLowerCase();

        if (!decision.equals("approve") && !decision.equals("a") && !decision.equals("reject") && !decision.equals("r")) {
            UIHelper.printErrorMessage("Invalid decision. Please enter 'approve'/'a' or 'reject'/'r'.");
            return;
        }

        boolean isApprove = decision.equals("approve") || decision.equals("a");

        if (isApprove) {
            staff.processWithdrawal(applicationID, true);
            UIHelper.printSuccessMessage("[APPROVED] " + applicationID + ": Withdrawal approved.");
        } else {
            staff.processWithdrawal(applicationID, false);
            UIHelper.printWarningMessage("[REJECTED] " + applicationID + ": Withdrawal rejected.");
        }








    }

    private void viewAllInternshipsFiltered() {
        UIHelper.printSectionHeader("ALL INTERNSHIPS (FILTERED)");

        if (filterManager.hasActiveFilters()) {
            System.out.println(filterManager.getFilterSettings().toString());
            System.out.println();
        }

        List<InternshipOpportunity> allInternships = internshipService.getAllInternships();
        allInternships = filterManager.getFilterSettings().applyFilters(allInternships);

        if (allInternships.isEmpty()) {
            UIHelper.printWarningMessage("No internships match your filters.");
        } else {
            for (InternshipOpportunity internship : allInternships) {
                System.out.println("ID: " + internship.getOpportunityID());
                System.out.println("Title: " + internship.getTitle());
                System.out.println("Company: " + internship.getCreatedBy().getCompanyName());
                System.out.println("Level: " + internship.getLevel());
                System.out.println("Preferred Major: " + internship.getPreferredMajor());
                System.out.println("Min GPA: " + internship.getMinGPA());
                System.out.println("Status: " + internship.getStatus());
                System.out.println("Closing Date: " + internship.getClosingDate());
                System.out.println("Visible: " + (internship.isVisible() ? "Yes" : "No"));
                System.out.println("-------------------");
            }
        }
    }

    private void generateReports() {
        UIHelper.printSectionHeader("GENERATE REPORTS");

        System.out.println("Enter filters (leave blank to skip):");
        Map<String, String> filters = new java.util.HashMap<>();

        System.out.print("Status (Pending/Approved/Rejected/Filled): ");
        String status = scanner.nextLine();
        if (!status.trim().isEmpty()) filters.put("status", status);

        System.out.print("Level (Basic/Intermediate/Advanced): ");
        String level = scanner.nextLine();
        if (!level.trim().isEmpty()) filters.put("level", level);

        System.out.print("Preferred Major: ");
        String major = scanner.nextLine();
        if (!major.trim().isEmpty()) filters.put("preferredMajor", major);

        System.out.print("Company Name: ");
        String company = scanner.nextLine();
        if (!company.trim().isEmpty()) filters.put("company", company);

        // Initialize ReportManager with repositories
        ReportManager reportManager = ReportManager.getInstance();
        reportManager.initialize(internshipService.getInternshipRepository(), applicationService.getApplicationRepository());
        
        Report report = reportManager.generateReport(filters);
        reportManager.displayDetailedReport(report);

        // Show summary statistics
        System.out.println("\n=== SYSTEM STATISTICS ===");
        Map<String, Integer> appStats = reportManager.getApplicationStatistics();
        Map<String, Integer> intStats = reportManager.getInternshipStatistics();

        System.out.println("Applications:");
        for (Map.Entry<String, Integer> entry : appStats.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("\nInternships:");
        for (Map.Entry<String, Integer> entry : intStats.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
    }

    private void changePassword() {
        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine().trim();

        // Verify current password
        if (!staff.verifyPassword(currentPassword)) {
            UIHelper.printErrorMessage("Current password is incorrect.");
            return;
        }

        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine().trim();

        if (newPassword.isEmpty()) {
            UIHelper.printErrorMessage("Password cannot be empty.");
            return;
        }

        // Check if new password is same as current password
        if (newPassword.equals(currentPassword)) {
            UIHelper.printErrorMessage("New password cannot be the same as current password.");
            return;
        }

        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine().trim();

        if (!newPassword.equals(confirmPassword)) {
            UIHelper.printErrorMessage("Passwords do not match.");
            return;
        }

        String oldPasswordHash = staff.getPasswordHash();
        String oldSalt = staff.getSalt();
        staff.changePassword(newPassword);
        try {
            userService.saveUsers();
            UIHelper.printSuccessMessage("Password changed successfully!");
        } catch (Exception e) {
            staff.setPasswordHash(oldPasswordHash);
            staff.setSalt(oldSalt);
            UIHelper.printErrorMessage("Failed to save password change: " + e.getMessage());
        }
    }

    private void logout() {
        UIHelper.printSuccessMessage("Logged out successfully!");
        staff.logout();
    }
}
