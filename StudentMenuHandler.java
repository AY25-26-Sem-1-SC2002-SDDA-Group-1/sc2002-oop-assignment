import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class StudentMenuHandler {
    private static final Scanner scanner = new Scanner(System.in);

    public static void showStudentMenu(Student student) {
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

        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                viewEligibleInternships(student);
                break;
            case "2":
                applyForInternship(student);
                break;
            case "3":
                viewMyApplications(student);
                break;
            case "4":
                acceptInternship(student);
                break;
            case "5":
                requestWithdrawal(student);
                break;
            case "6":
                viewStudentStatistics(student);
                break;
            case "7":
                FilterManager.manageFilters();
                break;
            case "8":
                changePassword(student);
                break;
            case "9":
                logout();
                break;
            default:
                UIHelper.printErrorMessage("Invalid choice. Please try again.");
        }
    }

    private static void viewEligibleInternships(Student student) {
        UIHelper.printSectionHeader("ELIGIBLE INTERNSHIPS");

        if (FilterManager.hasActiveFilters()) {
            System.out.println(FilterManager.getFilterSettings().toString());
            System.out.println();
        }

        var internships = student.viewEligibleInternships();
        internships = FilterManager.getFilterSettings().applyFilters(internships);

        if (internships.isEmpty()) {
            System.out.println("No eligible internships found.");
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            for (var internship : internships) {
                System.out.println("ID: " + internship.getOpportunityID());
                System.out.println("Title: " + internship.getTitle());
                System.out.println("Company: " + internship.getCreatedBy().getCompanyName());
                System.out.println("Level: " + internship.getLevel());
                System.out.println("Preferred Major: " + internship.getPreferredMajor());
                System.out.println("Min GPA: " + internship.getMinGPA());
                System.out.println("Max Slots: " + internship.getMaxSlots());
                System.out.println("Opening Date: " + dateFormat.format(internship.getOpeningDate()));
                System.out.println("Closing Date: " + dateFormat.format(internship.getClosingDate()));
                System.out.println("-------------------");
            }
        }
    }

    private static void applyForInternship(Student student) {
        // Display eligible internships first
        viewEligibleInternships(student);

        System.out.print("\nEnter Internship ID(s) (space-separated for multiple): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            System.out.println("Internship ID cannot be empty.");
            return;
        }

        String[] internshipIDs = input.split("\\s+");
        int successCount = 0;
        int failCount = 0;

        for (String internshipID : internshipIDs) {
            internshipID = internshipID.trim();
            boolean result = student.applyForInternship(internshipID);

            if (result) {
                System.out.println("[SUCCESS] " + internshipID + ": Application submitted successfully!");
                successCount++;
            } else {
                InternshipOpportunity opp = Database.getInternship(internshipID);
                if (opp == null) {
                    System.out.println("[FAILED] " + internshipID + ": Internship not found.");
                } else if (!opp.isVisible() || !opp.getStatus().equals("Approved")) {
                    System.out.println("[FAILED] " + internshipID + ": Not available for applications.");
                } else if (!opp.isOpen()) {
                    System.out.println("[FAILED] " + internshipID + ": Not accepting applications (check dates).");
                } else if (!opp.getPreferredMajor().equalsIgnoreCase(student.getMajor())) {
                    System.out.println("[FAILED] " + internshipID + ": Major mismatch.");
                } else if (student.getYearOfStudy() <= 2 && !opp.getLevel().equals("Basic")) {
                    System.out.println("[FAILED] " + internshipID + ": Year 1-2 students can only apply for Basic level.");
                } else if (student.getGpa() < opp.getMinGPA()) {
                    System.out.println("[FAILED] " + internshipID + ": GPA requirement not met (required: " + opp.getMinGPA() + ").");
                } else {
                    System.out.println("[FAILED] " + internshipID + ": Failed (already applied or max 3 applications reached).");
                }
                failCount++;
            }
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.println("Application complete: " + successCount + " succeeded, " + failCount + " failed.");
        System.out.println("=".repeat(50));
    }

    private static void viewMyApplications(Student student) {
        UIHelper.printSectionHeader("MY APPLICATIONS");
        var applications = student.viewApplications();
        if (applications.isEmpty()) {
            System.out.println("No applications found.");
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

    private static void acceptInternship(Student student) {
        // Show successful applications first
        List<Application> successfulApps = new java.util.ArrayList<>();
        for (Application app : student.viewApplications()) {
            if (app.getStatus().equals("Successful")) {
                successfulApps.add(app);
            }
        }

        if (successfulApps.isEmpty()) {
            System.out.println("\nNo successful applications to accept.");
            return;
        }

        System.out.println("\n=== SUCCESSFUL APPLICATIONS ===");
        for (Application app : successfulApps) {
            InternshipOpportunity opp = app.getOpportunity();
            System.out.println("\nApplication ID: " + app.getApplicationID());
            System.out.println("Internship: " + opp.getTitle());
            System.out.println("Company: " + opp.getCreatedBy().getName());
            System.out.println("Level: " + opp.getLevel());
            System.out.println("Max Slots: " + opp.getMaxSlots());
            System.out.println("-".repeat(50));
        }

        System.out.print("\nEnter Application ID to accept: ");
        String applicationID = scanner.nextLine();
        if (applicationID.trim().isEmpty()) {
            System.out.println("Application ID cannot be empty.");
            return;
        }
        student.acceptInternship(applicationID);
    }

    private static void requestWithdrawal(Student student) {
        // Show withdrawable applications (Pending, Successful, or Confirmed)
        List<Application> withdrawableApps = new java.util.ArrayList<>();
        for (Application app : student.viewApplications()) {
            String status = app.getStatus();
            if (status.equals("Pending") || status.equals("Successful") || status.equals("Confirmed")) {
                withdrawableApps.add(app);
            }
        }

        if (withdrawableApps.isEmpty()) {
            System.out.println("\nNo withdrawable applications found (Pending, Successful, or Confirmed).");
            return;
        }

        System.out.println("\n=== WITHDRAWABLE APPLICATIONS ===");
        for (Application app : withdrawableApps) {
            InternshipOpportunity opp = app.getOpportunity();
            System.out.println("\nApplication ID: " + app.getApplicationID());
            System.out.println("Status: " + app.getStatus());
            System.out.println("Internship: " + opp.getTitle());
            System.out.println("Company: " + opp.getCreatedBy().getName());
            System.out.println("Level: " + opp.getLevel());
            System.out.println("Applied Date: " + app.getAppliedDate());
            System.out.println("-".repeat(50));
        }

        System.out.print("\nEnter Application ID to withdraw: ");
        String applicationID = scanner.nextLine();
        if (applicationID.trim().isEmpty()) {
            System.out.println("Application ID cannot be empty.");
            return;
        }
        student.requestWithdrawal(applicationID);
    }

    private static void viewStudentStatistics(Student student) {
        Statistics stats = new Statistics();
        stats.displayStudentStatistics(student);
    }

    private static void changePassword(User user) {
        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine().trim();

        // Verify current password
        if (!user.verifyPassword(currentPassword)) {
            System.out.println("Current password is incorrect.");
            return;
        }

        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine().trim();

        if (newPassword.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return;
        }

        // Check if new password is same as current password
        if (newPassword.equals(currentPassword)) {
            System.out.println("New password cannot be the same as current password.");
            return;
        }

        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine().trim();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }

        user.changePassword(newPassword);
        System.out.println("Password changed successfully!");
    }

    private static void logout() {
        InternshipPlacementSystem.logout();
    }
}
