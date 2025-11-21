import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Menu handler for company representative operations.
 * Provides interface for managing internships, applications, and statistics.
 */
public class CompanyRepMenuHandler implements IMenuHandler {
    private final CompanyRepresentative rep;
    private final InternshipService internshipService;
    private final ApplicationService applicationService;
    private final UserService userService;
    private final Scanner scanner;
    private final FilterManager filterManager;

    /**
     * Constructs a CompanyRepMenuHandler.
     *
     * @param rep the company representative
     * @param internshipService the internship service
     * @param applicationService the application service
     * @param userService the user service
     * @param scanner the scanner for input
     */
    public CompanyRepMenuHandler(CompanyRepresentative rep, InternshipService internshipService, ApplicationService applicationService, UserService userService, Scanner scanner) {
        this.rep = rep;
        this.internshipService = internshipService;
        this.applicationService = applicationService;
        this.userService = userService;
        this.scanner = scanner;
        this.filterManager = new FilterManager(scanner);
    }

    /**
     * Displays the menu and handles user choices.
     */
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

        try {
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
                    viewCompanyRepresentativeStatistics();
                    break;
                case "9":
                    viewAllInternshipsFiltered();
                    break;
                case "10":
                    filterManager.manageFilters();
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
        } catch (Exception e) {
            UIHelper.printErrorMessage("Error reading input. Please try again.");
        }
    }

    private void createInternship() {
        if (!rep.isApproved()) {
            System.out.println("Your account is not approved yet. Please wait for Career Center Staff approval.");
            return;
        }

        // Check if rep has already created 5 internships
        int internshipCount = 0;
        for (InternshipOpportunity opp : internshipService.getAllInternships()) {
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

        System.out.println("Select Level:");
        System.out.println("1. Basic");
        System.out.println("2. Intermediate");
        System.out.println("3. Advanced");
        String level = null;
        while (level == null) {
            System.out.print("Enter number or Level: ");
            String input = scanner.nextLine().trim();
            try {
                int num = Integer.parseInt(input);
                switch (num) {
                    case 1: level = "Basic"; break;
                    case 2: level = "Intermediate"; break;
                    case 3: level = "Advanced"; break;
                    default: System.out.println("Invalid number. Please try again."); continue;
                }
            } catch (NumberFormatException e) {
                if (input.equals("Basic") || input.equals("Intermediate") || input.equals("Advanced")) {
                    level = input;
                } else {
                    System.out.println("Invalid level. Must be Basic, Intermediate, or Advanced. Please try again.");
                }
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

            if (internshipService.createInternship(rep.getUserID(), title, description, level, preferredMajor, openingDate, closingDate, maxSlots, minimumGPA)) {
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
        for (InternshipOpportunity opp : internshipService.getAllInternships()) {
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
        boolean foundEditable = false;
        for (InternshipOpportunity opp : internshipService.getAllInternships()) {
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
        InternshipOpportunity opp = internshipService.getInternship(internshipID);
        if (opp == null || !opp.getCreatedBy().getUserID().equals(rep.getUserID())) {
            System.out.println("Internship not found or you don't have permission to edit it.");
            return;
        }
        if (!opp.getStatus().equals("Pending") && !opp.getStatus().equals("Rejected")) {
            System.out.println("Cannot edit internship. Only pending or rejected internships can be edited (Status: " + opp.getStatus() + ")");
            return;
        }
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
        System.out.print("Enter new Level (1=Basic, 2=Intermediate, 3=Advanced) [" + opp.getLevel() + "]: ");
        String level = scanner.nextLine().trim();
        if (!level.isEmpty()) {
            String newLevel = null;
            try {
                int num = Integer.parseInt(level);
                switch (num) {
                    case 1: newLevel = "Basic"; break;
                    case 2: newLevel = "Intermediate"; break;
                    case 3: newLevel = "Advanced"; break;
                    default: newLevel = null;
                }
            } catch (NumberFormatException e) {
                if (level.equals("Basic") || level.equals("Intermediate") || level.equals("Advanced")) {
                    newLevel = level;
                }
            }
            if (newLevel != null) {
                opp.setLevel(newLevel);
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
        internshipService.getAllInternships(); // To trigger persistence if needed
        System.out.println("Internship updated successfully!");
    }

    private void deleteInternship() {
        UIHelper.printSectionHeader("DELETE INTERNSHIP");
        boolean found = false;
        for (InternshipOpportunity opp : internshipService.getAllInternships()) {
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
        InternshipOpportunity opp = internshipService.getInternship(internshipID);
        if (opp == null || !opp.getCreatedBy().getUserID().equals(rep.getUserID())) {
            System.out.println("Internship not found or you don't have permission to delete it.");
            return;
        }
        System.out.print("Are you sure you want to delete '" + opp.getTitle() + "'? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (confirm.equals("yes")) {
            internshipService.deleteInternship(internshipID);
            System.out.println("Internship deleted successfully!");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void viewApplicationDetails() {
        UIHelper.printSectionHeader("VIEW APPLICATION DETAILS");

        // Show rep's internships
        List<InternshipOpportunity> myInternships = new java.util.ArrayList<>();
        for (InternshipOpportunity opp : internshipService.getAllInternships()) {
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
        System.out.println("APPLICATIONS FOR: " + internshipService.getInternship(internshipID).getTitle());
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
        while (true) {
            UIHelper.printSectionHeader("PROCESS APPLICATIONS & MANAGE WAITLIST");
            
            // Get rep's internships
            List<InternshipOpportunity> myInternships = new ArrayList<>();
            for (InternshipOpportunity opp : internshipService.getAllInternships()) {
                if (opp.getCreatedBy().getUserID().equals(rep.getUserID()) &&
                    opp.getStatus().equals("Approved")) {
                    myInternships.add(opp);
                }
            }
            
            if (myInternships.isEmpty()) {
                UIHelper.printWarningMessage("You have no approved internships.");
                return;
            }
            
            // Display internships with slot status
            System.out.println("\nYour Internships:");
            int index = 1;
            for (InternshipOpportunity opp : myInternships) {
                // Count both Confirmed and Successful as filled slots
                long filledSlots = applicationService.getApplicationsForInternship(opp.getOpportunityID()).stream()
                    .filter(app -> app.getStatus().equals("Confirmed") || app.getStatus().equals("Successful"))
                    .count();
                long pendingCount = applicationService.getApplicationsForInternship(opp.getOpportunityID()).stream()
                    .filter(app -> app.getStatus().equals("Applied"))
                    .count();


                System.out.println(index + ". [" + opp.getOpportunityID() + "] " + opp.getTitle());
                System.out.println("   Slots: " + filledSlots + "/" + opp.getMaxSlots() + " filled" +
                    (filledSlots >= opp.getMaxSlots() ? " [FULL]" : " [" + (opp.getMaxSlots() - filledSlots) + " available]"));
                System.out.println("   Pending: " + pendingCount);
                index++;
            }

            System.out.print("\nEnter number or Internship ID (e.g., 1 or INT001) or 'back' to return: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("back")) {
                return;
            }

            // Find internship by number or ID
            InternshipOpportunity selectedOpp = null;
            try {
                int num = Integer.parseInt(input);
                if (num >= 1 && num <= myInternships.size()) {
                    selectedOpp = myInternships.get(num - 1);
                } else {
                    UIHelper.printErrorMessage("Invalid number.");
                    continue;
                }
            } catch (NumberFormatException e) {
                // treat as ID
                selectedOpp = myInternships.stream()
                    .filter(opp -> opp.getOpportunityID().equalsIgnoreCase(input))
                    .findFirst()
                    .orElse(null);
            }

            if (selectedOpp == null) {
                UIHelper.printErrorMessage("Invalid internship selection.");
                continue;
            }
            
            if (selectedOpp == null) {
                UIHelper.printErrorMessage("Invalid internship ID. Please enter a valid ID like INT001.");
                continue;
            }
            
            manageInternshipApplications(selectedOpp);
        }
    }
    
    private void manageInternshipApplications(InternshipOpportunity opp) {
        while (true) {
            System.out.println("\n" + "=".repeat(70));
            System.out.println("Managing: " + opp.getTitle());
            System.out.println("=".repeat(70));
            
            // Get counts - note: Confirmed = accepted by student, Successful = approved but not accepted
            long confirmedCount = applicationService.getApplicationsForInternship(opp.getOpportunityID()).stream()
                .filter(app -> app.getStatus().equals("Confirmed"))
                .count();
            long successfulCount = applicationService.getApplicationsForInternship(opp.getOpportunityID()).stream()
                .filter(app -> app.getStatus().equals("Successful"))
                .count();
            long pendingCount = applicationService.getApplicationsForInternship(opp.getOpportunityID()).stream()
                .filter(app -> app.getStatus().equals("Applied"))
                .count();

            // Available slots = max - (confirmed + successful)
            long filledSlots = confirmedCount + successfulCount;
            int availableSlots = (int)(opp.getMaxSlots() - filledSlots);
            
            System.out.println("Slots: " + filledSlots + "/" + opp.getMaxSlots() + 
                " (" + confirmedCount + " confirmed, " + successfulCount + " awaiting acceptance)" +
                (filledSlots >= opp.getMaxSlots() ? " [FULL]" : " [" + availableSlots + " available]"));
            System.out.println("Applied: " + pendingCount);
            
            // Show pending applications
            List<Application> pendingApps = applicationService.getApplicationsForInternship(opp.getOpportunityID()).stream()
                .filter(app -> app.getStatus().equals("Pending"))
                .toList();
            
            if (!pendingApps.isEmpty()) {
                System.out.println("\n" + "-".repeat(70));
                System.out.println("PENDING APPLICATIONS");
                System.out.println("-".repeat(70));
                int appIndex = 1;
                for (Application app : pendingApps) {
                    Student student = app.getApplicant();
                    System.out.println(appIndex + ". [" + app.getApplicationID() + "] " + student.getName() +
                        " | GPA: " + student.getGpa() + " | " + student.getMajor() + " (Year " + student.getYearOfStudy() + ")");
                    appIndex++;
                }
                System.out.println("-".repeat(70));
            }
            
            // Show available actions
            System.out.println("\nActions:");
            if (pendingCount > 0) {
                System.out.println("1. Approve/Reject Applications (enter App IDs)");
            }
            System.out.println("0. Back");

            System.out.print("\nChoose action: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("0")) {
                return;
            }

            switch (choice) {
                case "1":
                    if (pendingCount > 0) {
                        processApplicationsBatch(opp, pendingApps);
                    } else {
                        UIHelper.printErrorMessage("No pending applications.");
                    }
                    break;
                default:
                    UIHelper.printErrorMessage("Invalid choice.");
            }
        }
    }
    
    private void processApplicationsBatch(InternshipOpportunity opp, List<Application> pendingApps) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("APPROVE/REJECT APPLICATIONS");
        System.out.println("=".repeat(70));
        System.out.println("Max Slots: " + opp.getMaxSlots());
        System.out.println("\nNote: You can approve or reject multiple applications.");
        System.out.println("      All selected applications will be processed.");
        
        System.out.print("\nEnter Application IDs or numbers (space-separated, e.g., 1 2 or APP001 APP002): ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            UIHelper.printWarningMessage("No application IDs entered.");
            return;
        }

        String[] appIds = input.split("\\s+");

        // Validate all IDs exist and are pending
        List<Application> validApps = new ArrayList<>();
        for (String appId : appIds) {
            Application app = null;
            try {
                int num = Integer.parseInt(appId.trim());
                if (num >= 1 && num <= pendingApps.size()) {
                    app = pendingApps.get(num - 1);
                } else {
                    System.out.println("[SKIP] " + appId + ": Invalid number");
                    continue;
                }
            } catch (NumberFormatException e) {
                // treat as ID
                app = pendingApps.stream()
                    .filter(a -> a.getApplicationID().equalsIgnoreCase(appId.trim()))
                    .findFirst()
                    .orElse(null);
                if (app == null) {
                    System.out.println("[SKIP] " + appId + ": Not found or not pending");
                    continue;
                }
            }
            validApps.add(app);
        }
        
        if (validApps.isEmpty()) {
            UIHelper.printErrorMessage("No valid application IDs found.");
            return;
        }
        
        // Show summary
        System.out.println("\nApplications to process:");
        for (Application app : validApps) {
            Student student = app.getApplicant();
            System.out.println("  [" + app.getApplicationID() + "] " + student.getName() + 
                " | GPA: " + student.getGpa() + " | " + student.getMajor());
        }
        
        System.out.print("\nDecision (approve/reject): ");
        String decision = scanner.nextLine().trim().toLowerCase();

        if (!decision.equals("approve") && !decision.equals("a") && !decision.equals("reject") && !decision.equals("r")) {
            UIHelper.printErrorMessage("Invalid decision. Use 'approve'/'a' or 'reject'/'r'.");
            return;
        }

        boolean isApprove = decision.equals("approve") || decision.equals("a");
        
        // No slot limits - all selected applications will be processed
        
        System.out.print("\nConfirm " + decision + " for " + validApps.size() + " application(s)? (yes/no): ");
        String confirm = scanner.nextLine().trim();
        
        if (!confirm.equalsIgnoreCase("yes") && !confirm.equalsIgnoreCase("y")) {
            UIHelper.printWarningMessage("Operation cancelled.");
            return;
        }
        
        // Process applications
        int successCount = 0;

        
        for (int i = 0; i < validApps.size(); i++) {
            Application app = validApps.get(i);
            boolean success = rep.processApplication(app.getApplicationID(), isApprove);
            
            if (success) {
                if (isApprove) {
                    System.out.println("[ACCEPTED] " + app.getApplicationID() + ": " + app.getApplicant().getName());
                    successCount++;
                } else {
                    System.out.println("[REJECTED] " + app.getApplicationID() + ": " + app.getApplicant().getName());
                    successCount++;
                }
            } else {
                System.out.println("[FAILED] " + app.getApplicationID() + ": Could not process");
            }
        }
        
        System.out.println("\n" + "=".repeat(70));
        if (isApprove) {
            UIHelper.printSuccessMessage(successCount + " accepted");
        } else {
            UIHelper.printSuccessMessage(successCount + " rejected");
        }
        System.out.println("=".repeat(70));
        
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void toggleVisibility() {
        System.out.println("\n=== TOGGLE INTERNSHIP VISIBILITY ===");
        System.out.print("Enter Internship ID: ");
        String internshipID = scanner.nextLine().trim();

        InternshipOpportunity opp = internshipService.getInternship(internshipID);
        if (opp == null) {
            UIHelper.printErrorMessage("Internship not found.");
            return;
        }

        if (!opp.getCreatedBy().getUserID().equals(rep.getUserID())) {
            UIHelper.printErrorMessage("You can only modify your own internships.");
            return;
        }

        System.out.println("Current visibility: " + (opp.isVisible() ? "Visible" : "Hidden"));
        System.out.print("Set to Visible? (y/n): ");
        boolean setVisible = scanner.nextLine().trim().toLowerCase().startsWith("y");

        rep.toggleVisibility(internshipID, setVisible);
        UIHelper.printSuccessMessage("Visibility updated successfully!");
    }

    private void viewCompanyRepresentativeStatistics() {
        System.out.println("\n=== COMPANY REPRESENTATIVE STATISTICS ===");
        Statistics stats = new Statistics(applicationService.getApplicationRepository(),
                                        internshipService.getInternshipRepository(),
                                        userService.getUserRepository());
        stats.displayCompanyRepresentativeStatistics(rep);
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewAllInternshipsFiltered() {
        System.out.println("\n=== VIEW ALL INTERNSHIPS ===");
        List<InternshipOpportunity> internships = internshipService.getAllInternships();
        for (InternshipOpportunity opp : internships) {
            System.out.println(opp.getOpportunityID() + " - " + opp.getTitle() + " (" + opp.getStatus() + ")");
        }
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void changePassword() {
        System.out.println("\n=== CHANGE PASSWORD ===");
        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine();

        if (!rep.verifyPassword(currentPassword)) {
            UIHelper.printErrorMessage("Current password is incorrect.");
            return;
        }

        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();

        if (newPassword.trim().isEmpty()) {
            UIHelper.printErrorMessage("Password cannot be empty.");
            return;
        }

        rep.changePassword(newPassword);
        UIHelper.printSuccessMessage("Password changed successfully!");
    }

    private void logout() {
        System.out.println("\n=== LOGOUT ===");
        System.out.print("Are you sure you want to logout? (y/n): ");
        if (scanner.nextLine().trim().toLowerCase().startsWith("y")) {
            rep.logout();
            System.out.println("Logged out successfully!");
        }
    }
}
