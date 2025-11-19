import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class CompanyRepMenuHandler implements IMenuHandler {
    private final CompanyRepresentative rep;
    private final InternshipService internshipService;
    private final ApplicationService applicationService;
    private final Scanner scanner;

    public CompanyRepMenuHandler(CompanyRepresentative rep, InternshipService internshipService, ApplicationService applicationService, Scanner scanner) {
        this.rep = rep;
        this.internshipService = internshipService;
        this.applicationService = applicationService;
        this.scanner = scanner;
    }

    @Override
    public void showMenu() {
        UIHelper.printCompanyRepMenu();
        System.out.println("1. Create New Internship");
        System.out.println("2. View My Internships");
        System.out.println("3. Edit Internship");
        System.out.println("4. Delete Internship");
        System.out.println("5. View Application Details");
        System.out.println("6. Process Applications");
        System.out.println("7. Toggle Internship Visibility");
        System.out.println("8. View My Statistics");
        System.out.println("9. View All Internships (Filtered)");
        System.out.println("10. Manage Filters");
        System.out.println("11. Change Password");
        System.out.println("12. Logout");
        System.out.print("\nEnter your choice: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                createInternship();
                break;
            case "2":
                viewMyInternships();
                break;
            case "3":
                editInternship();
                break;
            case "4":
                deleteInternship();
                break;
            case "5":
                viewApplicationDetails();
                break;
            case "6":
                processApplications();
                break;
            case "7":
                toggleVisibility();
                break;
            case "8":
                viewCompanyRepStatistics();
                break;
            case "9":
                viewAllInternshipsFiltered();
                break;
            case "10":
                FilterManager.manageFilters();
                break;
            case "11":
                changePassword();
                break;
            case "12":
                logout();
                break;
            default:
                UIHelper.printErrorMessage("Invalid choice. Please try again.");
        }
    }

    private void createInternship() {
        if (!rep.isApproved()) {
            System.out.println("Your account is not approved yet. Please wait for Career Center Staff approval.");
            return;
        }

        // Check if rep has already created 5 internships
        int internshipCount = 0;
        for (InternshipOpportunity opp : Database.getInternships()) {
            if (opp.getCreatedBy().getUserID().equals(rep.getUserID())) {
                internshipCount++;
            }
        }
        if (internshipCount >= 5) {
            System.out.println("You have reached the maximum limit of 5 internships.");
            return;
        }

        System.out.print("Enter Title: ");
        String title = scanner.nextLine();
        if (title.trim().isEmpty()) {
            System.out.println("Title cannot be empty.");
            return;
        }

        System.out.print("Enter Description: ");
        String description = scanner.nextLine();
        if (description.trim().isEmpty()) {
            System.out.println("Description cannot be empty.");
            return;
        }

        String level = null;
        while (level == null) {
            System.out.print("Enter Level (Basic/Intermediate/Advanced): ");
            String input = scanner.nextLine().trim();
            if (input.equals("Basic") || input.equals("Intermediate") || input.equals("Advanced")) {
                level = input;
            } else {
                System.out.println("Invalid level. Must be Basic, Intermediate, or Advanced. Please try again.");
            }
        }

        System.out.print("Enter Preferred Major: ");
        String preferredMajor = scanner.nextLine();
        if (preferredMajor.trim().isEmpty()) {
            System.out.println("Preferred major cannot be empty.");
            return;
        }

        System.out.print("Enter Max Slots (1-10): ");
        try {
            int maxSlots = Integer.parseInt(scanner.nextLine());
            if (maxSlots <= 0 || maxSlots > 10) {
                System.out.println("Max slots must be between 1 and 10.");
                return;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateFormat.setLenient(false);

            // Get start of today (midnight) for date comparison
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
            cal.set(java.util.Calendar.MINUTE, 0);
            cal.set(java.util.Calendar.SECOND, 0);
            cal.set(java.util.Calendar.MILLISECOND, 0);
            Date startOfToday = cal.getTime();

            Date openingDate = null;
            while (openingDate == null) {
                System.out.print("Enter Opening Date (dd/MM/yyyy): ");
                try {
                    openingDate = dateFormat.parse(scanner.nextLine());
                    // Check if opening date is before today (allow today)
                    if (openingDate.before(startOfToday)) {
                        System.out.println("Opening date cannot be in the past. Please enter a date from today onwards.");
                        openingDate = null;
                    }
                } catch (ParseException e) {
                    System.out.println("Invalid date format. Please use dd/MM/yyyy.");
                }
            }

            Date closingDate = null;
            while (closingDate == null) {
                System.out.print("Enter Closing Date (dd/MM/yyyy): ");
                try {
                    closingDate = dateFormat.parse(scanner.nextLine());
                    if (closingDate.before(startOfToday)) {
                        System.out.println("Closing date cannot be in the past. Please enter a date from today onwards.");
                        closingDate = null;
                    } else if (closingDate.before(openingDate) || closingDate.equals(openingDate)) {
                        System.out.println("Closing date must be after opening date.");
                        closingDate = null;
                    }
                } catch (ParseException e) {
                    System.out.println("Invalid date format. Please use dd/MM/yyyy.");
                }
            }

            System.out.print("Enter Minimum GPA Required (0.0-5.0): ");
            double minimumGPA = 0.0;
            try {
                minimumGPA = Double.parseDouble(scanner.nextLine().trim());
                if (minimumGPA < 0.0 || minimumGPA > 5.0) {
                    System.out.println("Minimum GPA must be between 0.0 and 5.0.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid GPA format.");
                return;
            }

            if (rep.createInternship(title, description, level, preferredMajor, openingDate, closingDate, maxSlots, minimumGPA)) {
                System.out.println("Internship created successfully! It's pending approval from Career Center Staff.");
            } else {
                System.out.println("Failed to create internship. Please check all requirements.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number for max slots.");
        }
    }

    private void viewMyInternships() {
        UIHelper.printSectionHeader("MY INTERNSHIPS");
        boolean found = false;
        for (InternshipOpportunity opp : Database.getInternships()) {
            if (opp.getCreatedBy().getUserID().equals(rep.getUserID())) {
                System.out.println("ID: " + opp.getOpportunityID());
                System.out.println("Title: " + opp.getTitle());
                System.out.println("Description: " + opp.getDescription());
                System.out.println("Level: " + opp.getLevel());
                System.out.println("Preferred Major: " + opp.getPreferredMajor());
                System.out.println("Min GPA: " + opp.getMinGPA());
                System.out.println("Status: " + opp.getStatus());
                System.out.println("Max Slots: " + opp.getMaxSlots());
                System.out.println("Visible: " + opp.isVisibility());
                System.out.println("-------------------");
                found = true;
            }
        }
        if (!found) {
            System.out.println("No internships created yet.");
        }
    }

    private void editInternship() {
        UIHelper.printSectionHeader("EDIT INTERNSHIP (Pending/Rejected Only)");
        // Show only pending and rejected internships
        boolean foundEditable = false;
        for (InternshipOpportunity opp : Database.getInternships()) {
            if (opp.getCreatedBy().getUserID().equals(rep.getUserID()) &&
                (opp.getStatus().equals("Pending") || opp.getStatus().equals("Rejected"))) {
                System.out.println("ID: " + opp.getOpportunityID());
                System.out.println("Title: " + opp.getTitle());
                System.out.println("Status: " + opp.getStatus());
                System.out.println("-------------------");
                foundEditable = true;
            }
        }

        if (!foundEditable) {
            System.out.println("No editable internships found. Note: Only pending or rejected internships can be edited.");
            return;
        }

        System.out.print("Enter Internship ID to edit: ");
        String internshipID = scanner.nextLine().trim();
        if (internshipID.isEmpty()) {
            System.out.println("ID cannot be empty.");
            return;
        }

        InternshipOpportunity opp = Database.getInternship(internshipID);
        if (opp == null || !opp.getCreatedBy().getUserID().equals(rep.getUserID())) {
            System.out.println("Internship not found or you don't have permission to edit it.");
            return;
        }

        if (!opp.getStatus().equals("Pending") && !opp.getStatus().equals("Rejected")) {
            System.out.println("Cannot edit internship. Only pending or rejected internships can be edited (Status: " + opp.getStatus() + ")");
            return;
        }

        // If editing a rejected internship, change status back to Pending
        if (opp.getStatus().equals("Rejected")) {
            opp.setStatus("Pending");
            System.out.println("Note: This internship status has been changed from Rejected to Pending for re-review.");
        }

        System.out.println("Leave field blank to keep current value.");

        System.out.print("Enter new Title [" + opp.getTitle() + "]: ");
        String title = scanner.nextLine().trim();
        if (!title.isEmpty()) {
            opp.setTitle(title);
        }

        System.out.print("Enter new Description [" + opp.getDescription() + "]: ");
        String description = scanner.nextLine().trim();
        if (!description.isEmpty()) {
            opp.setDescription(description);
        }

        System.out.print("Enter new Level (Basic/Intermediate/Advanced) [" + opp.getLevel() + "]: ");
        String level = scanner.nextLine().trim();
        if (!level.isEmpty()) {
            if (level.equals("Basic") || level.equals("Intermediate") || level.equals("Advanced")) {
                opp.setLevel(level);
            } else {
                System.out.println("Invalid level. Keeping current value.");
            }
        }

        System.out.print("Enter new Preferred Major [" + opp.getPreferredMajor() + "]: ");
        String preferredMajor = scanner.nextLine().trim();
        if (!preferredMajor.isEmpty()) {
            opp.setPreferredMajor(preferredMajor);
        }

        System.out.print("Enter new Max Slots (1-10) [" + opp.getMaxSlots() + "]: ");
        String maxSlotsStr = scanner.nextLine().trim();
        if (!maxSlotsStr.isEmpty()) {
            try {
                int maxSlots = Integer.parseInt(maxSlotsStr);
                if (maxSlots >= 1 && maxSlots <= 10) {
                    opp.setMaxSlots(maxSlots);
                } else {
                    System.out.println("Max slots must be between 1 and 10. Keeping current value.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Keeping current value.");
            }
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);

        System.out.print("Enter new Opening Date (dd/MM/yyyy) [" + dateFormat.format(opp.getOpeningDate()) + "]: ");
        String openingDateStr = scanner.nextLine().trim();
        if (!openingDateStr.isEmpty()) {
            try {
                Date openingDate = dateFormat.parse(openingDateStr);
                opp.setOpeningDate(openingDate);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Keeping current value.");
            }
        }

        System.out.print("Enter new Closing Date (dd/MM/yyyy) [" + dateFormat.format(opp.getClosingDate()) + "]: ");
        String closingDateStr = scanner.nextLine().trim();
        if (!closingDateStr.isEmpty()) {
            try {
                Date closingDate = dateFormat.parse(closingDateStr);
                if (closingDate.after(opp.getOpeningDate())) {
                    opp.setClosingDate(closingDate);
                } else {
                    System.out.println("Closing date must be after opening date. Keeping current value.");
                }
            } catch (ParseException e) {
                System.out.println("Invalid date format. Keeping current value.");
            }
        }

        System.out.print("Enter new Minimum GPA (0.0-5.0) [" + opp.getMinGPA() + "]: ");
        String minimumGPAStr = scanner.nextLine().trim();
        if (!minimumGPAStr.isEmpty()) {
            try {
                double minimumGPA = Double.parseDouble(minimumGPAStr);
                if (minimumGPA >= 0.0 && minimumGPA <= 5.0) {
                    opp.setMinGPA(minimumGPA);
                } else {
                    System.out.println("Minimum GPA must be between 0.0 and 5.0. Keeping current value.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid GPA format. Keeping current value.");
            }
        }

        Database.saveData();
        System.out.println("Internship updated successfully!");
    }

    private void deleteInternship() {
        UIHelper.printSectionHeader("DELETE INTERNSHIP");
        // Show all internships created by rep
        boolean found = false;
        for (InternshipOpportunity opp : Database.getInternships()) {
            if (opp.getCreatedBy().getUserID().equals(rep.getUserID())) {
                System.out.println("ID: " + opp.getOpportunityID());
                System.out.println("Title: " + opp.getTitle());
                System.out.println("Status: " + opp.getStatus());
                System.out.println("-------------------");
                found = true;
            }
        }

        if (!found) {
            System.out.println("No internships to delete.");
            return;
        }

        System.out.print("Enter Internship ID to delete: ");
        String internshipID = scanner.nextLine().trim();
        if (internshipID.isEmpty()) {
            System.out.println("ID cannot be empty.");
            return;
        }

        InternshipOpportunity opp = Database.getInternship(internshipID);
        if (opp == null || !opp.getCreatedBy().getUserID().equals(rep.getUserID())) {
            System.out.println("Internship not found or you don't have permission to delete it.");
            return;
        }

        System.out.print("Are you sure you want to delete '" + opp.getTitle() + "'? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (confirm.equals("yes")) {
            Database.removeInternship(internshipID);
            System.out.println("Internship deleted successfully.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void viewApplicationDetails() {
        UIHelper.printSectionHeader("VIEW APPLICATION DETAILS");

        // Show rep's internships
        List<InternshipOpportunity> myInternships = new java.util.ArrayList<>();
        for (InternshipOpportunity opp : Database.getInternships()) {
            if (opp.getCreatedBy().getUserID().equals(rep.getUserID())) {
                myInternships.add(opp);
            }
        }

        if (myInternships.isEmpty()) {
            UIHelper.printWarningMessage("No internships found.");
            return;
        }

        System.out.println("\nYour Internships:");
        for (InternshipOpportunity opp : myInternships) {
            System.out.println("ID: " + opp.getOpportunityID() + " - " + opp.getTitle() + " (" + opp.getStatus() + ")");
        }

        System.out.print("\nEnter Internship ID to view applications (or 'cancel'): ");
        String internshipID = scanner.nextLine().trim();

        if (internshipID.equalsIgnoreCase("cancel")) {
            return;
        }

        // Get applications for this internship
        List<Application> applications = rep.viewApplications(internshipID);

        if (applications.isEmpty()) {
            System.out.println("No applications found for this internship.");
            return;
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println("APPLICATIONS FOR: " + Database.getInternship(internshipID).getTitle());
        System.out.println("=".repeat(70));

        for (Application app : applications) {
            Student student = app.getApplicant();
            System.out.println("\nApplication ID: " + app.getApplicationID());
            System.out.println("Status: " + app.getStatus());
            System.out.println("Applied Date: " + app.getAppliedDate());
            System.out.println("\nStudent Information:");
            System.out.println("  Name: " + student.getName());
            System.out.println("  Student ID: " + student.getUserID());
            System.out.println("  Year of Study: Year " + student.getYearOfStudy());
            System.out.println("  Major: " + student.getMajor());
            System.out.println("  GPA: " + student.getGpa());
            System.out.println("-".repeat(70));
        }

        System.out.println("\nTotal Applications: " + applications.size());
        System.out.println("=".repeat(70));
    }

    private void processApplications() {
        UIHelper.printSectionHeader("PROCESS APPLICATIONS");

        List<Application> pendingApplications = rep.getPendingApplications();

        if (pendingApplications.isEmpty()) {
            UIHelper.printWarningMessage("No applications to process.");
            return;
        }

        // Display all pending applications
        System.out.println("\nPending Applications:");
        for (Application app : pendingApplications) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("Application ID: " + app.getApplicationID());
            System.out.println("Student Name: " + app.getApplicant().getName());
            System.out.println("Student ID: " + app.getApplicant().getUserID());
            System.out.println("Student Year: Year " + app.getApplicant().getYearOfStudy());
            System.out.println("Student Major: " + app.getApplicant().getMajor());
            System.out.println("Student GPA: " + app.getApplicant().getGpa());
            System.out.println("Internship: " + app.getOpportunity().getTitle());
            System.out.println("Applied Date: " + app.getAppliedDate());
            System.out.println("=".repeat(50));
        }

        // Process applications (space-separated for batch processing)
        System.out.print("\nEnter Application ID(s) to process (space-separated for multiple, or 'cancel' to go back): ");
        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("cancel")) {
            UIHelper.printWarningMessage("Operation cancelled.");
            return;
        }

        if (input.isEmpty()) {
            UIHelper.printErrorMessage("Application ID cannot be empty.");
            return;
        }

        // Get decision first
        System.out.print("Decision for all selected applications (approve/reject): ");
        String decision = scanner.nextLine().trim().toLowerCase();

        if (!decision.equals("approve") && !decision.equals("reject")) {
            UIHelper.printErrorMessage("Invalid decision. Please enter 'approve' or 'reject'.");
            return;
        }

        // Process each application ID
        String[] applicationIDs = input.split("\\s+");
        int successCount = 0;
        int failCount = 0;

        for (String applicationID : applicationIDs) {
            applicationID = applicationID.trim();
            if (applicationID.isEmpty()) continue;

            // Verify the application exists in pending list
            boolean found = false;
            for (Application app : pendingApplications) {
                if (app.getApplicationID().equals(applicationID)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("Skipped " + applicationID + ": Invalid or not pending");
                failCount++;
                continue;
            }

            boolean success;
            if (decision.equals("approve")) {
                success = rep.processApplication(applicationID, true);
            } else {
                success = rep.processApplication(applicationID, false);
            }

            if (success) {
                successCount++;
            } else {
                System.out.println("Failed to process: " + applicationID);
                failCount++;
            }
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.println("Processing complete: " + successCount + " succeeded, " + failCount + " failed");
        System.out.println("=".repeat(50));
    }

    private void toggleVisibility() {
        UIHelper.printSectionHeader("TOGGLE INTERNSHIP VISIBILITY");

        // Show rep's approved internships
        List<InternshipOpportunity> myInternships = new java.util.ArrayList<>();
        for (InternshipOpportunity opp : Database.getInternships()) {
            if (opp.getCreatedBy().getUserID().equals(rep.getUserID()) &&
                opp.getStatus().equals("Approved")) {
                myInternships.add(opp);
            }
        }

        if (myInternships.isEmpty()) {
            UIHelper.printWarningMessage("No approved internships to toggle visibility.");
            return;
        }

        System.out.println("\nYour Approved Internships:");
        for (InternshipOpportunity opp : myInternships) {
            System.out.println("\nID: " + opp.getOpportunityID());
            System.out.println("Title: " + opp.getTitle());
            System.out.println("Current Visibility: " + (opp.isVisibility() ? "Visible" : "Hidden"));
            System.out.println("-".repeat(50));
        }

        System.out.print("\nEnter Internship ID(s) to toggle (space-separated for multiple, or 'cancel'): ");
        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("cancel")) {
            UIHelper.printWarningMessage("Operation cancelled.");
            return;
        }

        if (input.isEmpty()) {
            UIHelper.printErrorMessage("Internship ID cannot be empty.");
            return;
        }

        String[] internshipIDs = input.split("\\s+");

        System.out.print("Set visibility to (visible/hidden): ");
        String visibilityInput = scanner.nextLine().trim().toLowerCase();

        if (!visibilityInput.equals("visible") && !visibilityInput.equals("hidden")) {
            UIHelper.printErrorMessage("Invalid input. Please enter 'visible' or 'hidden'.");
            return;
        }

        boolean setVisible = visibilityInput.equals("visible");
        int successCount = 0;
        int failCount = 0;

        for (String internshipID : internshipIDs) {
            internshipID = internshipID.trim();

            // Verify internship exists in the list
            InternshipOpportunity targetOpp = null;
            for (InternshipOpportunity opp : myInternships) {
                if (opp.getOpportunityID().equals(internshipID)) {
                    targetOpp = opp;
                    break;
                }
            }

            if (targetOpp == null) {
                System.out.println("✗ " + internshipID + ": Invalid ID or not approved.");
                failCount++;
            } else {
                rep.toggleVisibility(internshipID, setVisible);
                System.out.println("✓ " + internshipID + ": Set to " + (setVisible ? "visible" : "hidden"));
                successCount++;
            }
        }

        Database.saveData();
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Toggle complete: " + successCount + " succeeded, " + failCount + " failed.");
        System.out.println("=".repeat(50));
    }

    private void viewCompanyRepStatistics() {
        Statistics stats = new Statistics();
        stats.displayCompanyRepresentativeStatistics(rep);
    }

    private void viewAllInternshipsFiltered() {
        UIHelper.printSectionHeader("ALL INTERNSHIPS (FILTERED)");

        if (FilterManager.hasActiveFilters()) {
            System.out.println(FilterManager.getFilterSettings().toString());
            System.out.println();
        }

        List<InternshipOpportunity> allInternships = Database.getInternships();
        allInternships = FilterManager.getFilterSettings().applyFilters(allInternships);

        if (allInternships.isEmpty()) {
            System.out.println("No internships match your filters.");
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

    private void changePassword() {
        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine().trim();

        // Verify current password
        if (!rep.verifyPassword(currentPassword)) {
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

        rep.changePassword(newPassword);
        System.out.println("Password changed successfully!");
    }

    private void logout() {
        System.out.println("Logging out...");
        rep.logout();
    }
}
