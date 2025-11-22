import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Menu handler for student operations.
 * Provides interface for viewing internships, applying, and managing applications.
 */
public class StudentMenuHandler implements IMenuHandler {
    private final Student student;
    private final IInternshipService internshipService;
    private final IStudentApplicationService applicationService;
    private final IUserService userService;
    private final Scanner scanner;
    private final FilterManager filterManager;

    /**
     * Constructs a StudentMenuHandler.
     *
     * @param student the student
     * @param internshipService the internship service
     * @param applicationService the application service
     * @param scanner the scanner for input
     */
    public StudentMenuHandler(Student student, IInternshipService internshipService, IStudentApplicationService applicationService, Scanner scanner) {
        this(student, internshipService, applicationService, null, scanner);
    }

    /**
     * Constructs a StudentMenuHandler with user service.
     *
     * @param student the student
     * @param internshipService the internship service
     * @param applicationService the application service
     * @param userService the user service
     * @param scanner the scanner for input
     */
    public StudentMenuHandler(Student student, IInternshipService internshipService, IStudentApplicationService applicationService, IUserService userService, Scanner scanner) {
        this.student = student;
        this.internshipService = internshipService;
        this.applicationService = applicationService;
        this.userService = userService;
        this.scanner = scanner;
        this.filterManager = FilterManagerFactory.createFilterManager(scanner);
    }

    /**
     * Displays the menu and handles user choices.
     */
    @Override
    public void showMenu() {
        UIHelper.printStudentMenu();
        System.out.println("1. View Eligible Internships");
        System.out.println("2. Apply for Internship");
        System.out.println("3. View My Applications");
        System.out.println("4. Accept Internship");
        System.out.println("5. Request Withdrawal");
        System.out.println("6. View My Statistics");
        System.out.println("7. Manage Filters");
        System.out.println("8. Change Password");
        System.out.println("9. Logout");
        System.out.print("\nEnter your choice: ");

        try {
            String choice = scanner.nextLine();

            switch (choice.toLowerCase()) {
                case "1":
                case "v":
                    viewEligibleInternships();
                    break;
                case "2":
                case "a":
                    applyForInternship();
                    break;
                case "3":
                case "m":
                    viewMyApplications();
                    break;
                case "4":
                case "c":
                    acceptInternship();
                    break;
                case "5":
                case "w":
                    requestWithdrawal();
                    break;
                case "6":
                case "s":
                    viewStudentStatistics();
                    break;
                case "7":
                case "f":
                    filterManager.manageFilters();
                    break;
                case "8":
                case "p":
                    changePassword();
                    break;
                case "9":
                case "l":
                    logout();
                    break;
                default:
                    UIHelper.printErrorMessage("Invalid choice. Please try again.");
            }
        } catch (Exception e) {
            UIHelper.printErrorMessage("Error reading input. Please try again.");
        }
    }

    private void viewEligibleInternships() {
        UIHelper.printSectionHeader("ELIGIBLE INTERNSHIPS");

        if (filterManager.hasActiveFilters()) {
            System.out.println(filterManager.getFilterSettings().toString());
            System.out.println();
        }

        // Use ApplicationService to get eligible internships
        List<InternshipOpportunity> internships = applicationService.getEligibleInternshipsForStudent(student.getUserID());
        internships = filterManager.getFilterSettings().applyFilters(internships);

        if (internships.isEmpty()) {
            UIHelper.printWarningMessage("No eligible internships found.");
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            for (var internship : internships) {
                int filledSlots = applicationService.getApplicationsForInternship(internship.getOpportunityID()).stream()
                    .mapToInt(app -> {
                        ApplicationStatus status = app.getStatusEnum();
                        return (status == ApplicationStatus.CONFIRMED || status == ApplicationStatus.SUCCESSFUL || status == ApplicationStatus.WITHDRAWAL_REQUESTED) ? 1 : 0;
                    }).sum();
                System.out.println("ID: " + internship.getOpportunityID());
                System.out.println("Title: " + internship.getTitle());
                System.out.println("Company: " + internship.getCreatedBy().getCompanyName());
                System.out.println("Level: " + internship.getLevel());
                System.out.println("Preferred Major: " + internship.getPreferredMajor());
                System.out.println("Min GPA: " + internship.getMinGPA());
                System.out.println("Slots: " + filledSlots + "/" + internship.getMaxSlots());
                System.out.println("Opening Date: " + dateFormat.format(internship.getOpeningDate()));
                System.out.println("Closing Date: " + dateFormat.format(internship.getClosingDate()));
                System.out.println("-------------------");
            }
        }
    }

    private void applyForInternship() {
        UIHelper.printSectionHeader("APPLY FOR INTERNSHIP");

        // Get eligible internships
        List<InternshipOpportunity> internships = internshipService.getAllInternships().stream()
            .filter(i -> i.isVisible() && i.getStatus().equals("Approved") && student.isEligibleForInternship(i))
            .collect(Collectors.toList());
        internships = filterManager.getFilterSettings().applyFilters(internships);

        if (internships.isEmpty()) {
            System.out.println("No eligible internships found.");
            return;
        }

        // Display eligible internships with numbers
        System.out.println("\nEligible Internships:");
        int index = 1;
        for (InternshipOpportunity internship : internships) {
            int filledSlots = applicationService.getApplicationsForInternship(internship.getOpportunityID()).stream()
                .mapToInt(app -> {
                    ApplicationStatus status = app.getStatusEnum();
                    return (status == ApplicationStatus.CONFIRMED || status == ApplicationStatus.SUCCESSFUL || status == ApplicationStatus.WITHDRAWAL_REQUESTED) ? 1 : 0;
                }).sum();
            System.out.println(index + ". ID: " + internship.getOpportunityID() + " - " + internship.getTitle() + " (Filled: " + filledSlots + "/" + internship.getMaxSlots() + ")");
            index++;
        }

        System.out.print("\nEnter Internship numbers or IDs (space-separated, e.g., 1 2 or INT001 INT002): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            System.out.println("Input cannot be empty.");
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
                if (num >= 1 && num <= internships.size()) {
                    internshipID = internships.get(num - 1).getOpportunityID();
                } else {
                    System.out.println("[SKIP] " + inp + ": Invalid number");
                    failCount++;
                    continue;
                }
            } catch (NumberFormatException e) {
                // treat as ID
                internshipID = inp;
            }

            OperationResult result = applicationService.applyForInternship(student.getUserID(), internshipID);

            if (result.isSuccess()) {
                System.out.println("[SUCCESS] " + internshipID + ": " + result.getMessage());
                successCount++;
            } else {
                System.out.println("[FAILED] " + internshipID + ": " + result.getMessage());
                failCount++;
            }
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.println("Application complete: " + successCount + " succeeded, " + failCount + " failed.");
        System.out.println("=".repeat(50));
    }

    private void viewMyApplications() {
        UIHelper.printSectionHeader("MY APPLICATIONS");
        var applications = applicationService.getApplicationsForStudent(student.getUserID());
        if (applications.isEmpty()) {
            UIHelper.printWarningMessage("No applications found.");
        } else {
            for (var app : applications) {
                InternshipOpportunity opp = app.getOpportunity();
                System.out.println("\nApplication ID: " + app.getApplicationID());
                System.out.println("Status: " + app.getStatus());
                System.out.println("Applied Date: " + app.getAppliedDate());
                System.out.println("\nInternship Details:");
                System.out.println("  ID: " + opp.getOpportunityID());
                System.out.println("  Title: " + opp.getTitle());
                System.out.println("  Description: " + opp.getDescription());
                System.out.println("  Company: " + opp.getCreatedBy().getCompanyName());
                System.out.println("  Level: " + opp.getLevel());
                System.out.println("  Preferred Major: " + opp.getPreferredMajor());
                System.out.println("  Min GPA Required: " + opp.getMinGPA());
                System.out.println("  Max Slots: " + opp.getMaxSlots());
                System.out.println("  Currently Visible: " + (opp.isVisible() ? "Yes" : "No"));
                System.out.println("=".repeat(50));
            }
        }
    }

    private void acceptInternship() {
        // Show successful applications first
        List<Application> successfulApps = applicationService.getApplicationsForStudent(student.getUserID()).stream()
            .filter(app -> app.getStatusEnum() == ApplicationStatus.SUCCESSFUL)
            .toList();

        if (successfulApps.isEmpty()) {
            UIHelper.printWarningMessage("No successful applications to accept.");
            return;
        }

        System.out.println("\n=== SUCCESSFUL APPLICATIONS ===");
        for (Application app : successfulApps) {
            InternshipOpportunity opp = app.getOpportunity();
            System.out.println("\nApplication ID: " + app.getApplicationID());
            System.out.println("Internship: " + opp.getTitle());
            System.out.println("Company: " + opp.getCreatedBy().getCompanyName());
            System.out.println("Level: " + opp.getLevel());
            System.out.println("Max Slots: " + opp.getMaxSlots());
            System.out.println("-".repeat(50));
        }

        System.out.print("\nEnter Application ID to accept (or 'back' to return): ");
        String applicationID = scanner.nextLine().trim();
        if (applicationID.isEmpty() || applicationID.equalsIgnoreCase("back")) {
            return;
        }
        OperationResult result = applicationService.acceptInternship(student.getUserID(), applicationID);
        if (result.isSuccess()) {
            UIHelper.printSuccessMessage(result.getMessage());
        } else {
            UIHelper.printWarningMessage(result.getMessage());
        }
    }

    private void requestWithdrawal() {
        // Show withdrawable applications (Pending, Successful, or Confirmed)
        List<Application> withdrawableApps = applicationService.getApplicationsForStudent(student.getUserID()).stream()
            .filter(app -> {
                ApplicationStatus status = app.getStatusEnum();
                return status == ApplicationStatus.PENDING || status == ApplicationStatus.SUCCESSFUL || status == ApplicationStatus.CONFIRMED;
            })
            .toList();

        if (withdrawableApps.isEmpty()) {
            UIHelper.printWarningMessage("No withdrawable applications found (Pending, Successful, or Confirmed).");
            return;
        }

        System.out.println("\n=== WITHDRAWABLE APPLICATIONS ===");
        for (Application app : withdrawableApps) {
            InternshipOpportunity opp = app.getOpportunity();
            System.out.println("\nApplication ID: " + app.getApplicationID());
            System.out.println("Status: " + app.getStatus());
            System.out.println("Internship: " + opp.getTitle());
            System.out.println("Company: " + opp.getCreatedBy().getCompanyName());
            System.out.println("Level: " + opp.getLevel());
            System.out.println("Applied Date: " + app.getAppliedDate());
            System.out.println("-".repeat(50));
        }

        System.out.print("\nEnter Application ID to withdraw (or 'back' to return): ");
        String applicationID = scanner.nextLine().trim();
        if (applicationID.isEmpty() || applicationID.equalsIgnoreCase("back")) {
            return;
        }
        OperationResult result = applicationService.requestWithdrawal(student.getUserID(), applicationID);
        if (result.isSuccess()) {
            UIHelper.printSuccessMessage(result.getMessage());
        } else {
            UIHelper.printWarningMessage(result.getMessage());
        }
    }

    private void viewStudentStatistics() {
        IUserRepository userRepo = (userService != null) ? userService.getUserRepository() : null;
        if (userRepo == null) {
            System.out.println("Error: User repository not available.");
            return;
        }
        Statistics stats = new Statistics(applicationService.getApplicationRepository(), applicationService, internshipService.getInternshipRepository(), userRepo);
        stats.displayStudentStatistics(student);
    }

    private void changePassword() {
        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine().trim();

        // Verify current password
        if (!student.verifyPassword(currentPassword)) {
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

        String oldPasswordHash = student.getPasswordHash();
        String oldSalt = student.getSalt();
        student.changePassword(newPassword);
        try {
            userService.saveUsers();
            UIHelper.printSuccessMessage("Password changed successfully!");
        } catch (Exception e) {
            student.setPasswordHash(oldPasswordHash);
            student.setSalt(oldSalt);
            UIHelper.printErrorMessage("Failed to save password change: " + e.getMessage());
        }
    }

    private void logout() {
        UIHelper.printSuccessMessage("Logged out successfully!");
        student.logout();
    }
    
}
