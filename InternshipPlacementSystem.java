import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class InternshipPlacementSystem {
    private static final Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    public static void main(String[] args) {
        UIHelper.printWelcomeBanner();
        
        while (true) {
            if (currentUser == null) {
                showMainMenu();
            } else {
                showUserMenu();
            }
        }
    }

    private static void showMainMenu() {
        UIHelper.printMainMenu();
        
        try {
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    login();
                    break;
                case "2":
                    registerCompanyRep();
                    break;
                case "3":
                    UIHelper.printGoodbyeMessage();
                    Database.saveData();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error reading input. Please try again.");
        }
    }

    private static void login() {
        try {
            System.out.println();
            System.out.println("┌─────────────────────────────────────────────────────────────┐");
            System.out.println("│                      USER LOGIN                             │");
            System.out.println("└─────────────────────────────────────────────────────────────┘");
            System.out.println();
            System.out.print("  User ID: ");
            String userID = scanner.nextLine().trim();
            System.out.print("  Password: ");
            String password = scanner.nextLine().trim();
            
            User user = Database.getUser(userID);
            if (user == null) {
                System.out.println();
                System.out.println(" Invalid user ID.");
                return;
            }
            
            // Check if company representative is approved
            if (user instanceof CompanyRepresentative) {
                CompanyRepresentative rep = (CompanyRepresentative) user;
                if (!rep.isApproved()) {
                    System.out.println();
                    System.out.println("  Your account is pending approval from Career Center Staff.");
                    System.out.println("  Please wait for authorization.");
                    return;
                }
            }
            
            if (user.login(password)) {
                currentUser = user;
                System.out.println();
                System.out.println("  Login successful! Welcome, " + user.getName() + "!");
                System.out.println();
            } else {
                System.out.println();
                System.out.println("  Incorrect password.");
            }
        } catch (Exception e) {
            System.out.println("Error during login. Please try again.");
        }
    }
    
    private static void registerCompanyRep() {
        try {
            UIHelper.printSectionHeader("COMPANY REPRESENTATIVE REGISTRATION");
            
            System.out.print("Enter User ID (this will be used for login): ");
            String userID = scanner.nextLine().trim();
            if (userID.isEmpty()) {
                System.out.println("User ID cannot be empty.");
                return;
            }
            
            // Check if user ID already exists
            if (Database.getUser(userID) != null) {
                System.out.println("User ID already exists. Please choose a different ID.");
                return;
            }
            
            System.out.print("Enter Name: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Name cannot be empty.");
                return;
            }
            
            String email = "";
            boolean validEmail = false;
            while (!validEmail) {
                System.out.print("Enter Email: ");
                email = scanner.nextLine().trim();
                if (email.isEmpty()) {
                    System.out.println("Email cannot be empty. Please try again.");
                } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                    System.out.println("Invalid email format. Please enter a valid email address.");
                } else {
                    validEmail = true;
                }
            }
            
            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();
            if (password.isEmpty()) {
                System.out.println("Password cannot be empty.");
                return;
            }
            
            System.out.print("Enter Company Name: ");
            String companyName = scanner.nextLine().trim();
            if (companyName.isEmpty()) {
                System.out.println("Company name cannot be empty.");
                return;
            }
            
            System.out.print("Enter Department: ");
            String department = scanner.nextLine().trim();
            if (department.isEmpty()) {
                System.out.println("Department cannot be empty.");
                return;
            }
            
            System.out.print("Enter Position: ");
            String position = scanner.nextLine().trim();
            if (position.isEmpty()) {
                System.out.println("Position cannot be empty.");
                return;
            }
            
            CompanyRepresentative newRep = new CompanyRepresentative(
                userID, name, password, companyName, department, position, email
            );
            Database.addUser(newRep);
            Database.saveData();
            System.out.println("Registration successful!");
            System.out.println("Your User ID is: " + userID);
            System.out.println("Your account is pending approval from Career Center Staff.");
        } catch (Exception e) {
            System.out.println("Error during registration. Please try again.");
        }
    }

    private static void showUserMenu() {
        if (currentUser instanceof Student) {
            showStudentMenu();
        } else if (currentUser instanceof CompanyRepresentative) {
            showCompanyRepMenu();
        } else if (currentUser instanceof CareerCenterStaff) {
            showCareerStaffMenu();
        }
    }

    private static void showStudentMenu() {
        Student student = (Student) currentUser;
        UIHelper.printStudentMenu();
        System.out.println("1. View Eligible Internships");
        System.out.println("2. View All Internships");
        System.out.println("3. Apply for Internship");
        System.out.println("4. View My Applications");
        System.out.println("5. Accept Internship");
        System.out.println("6. Request Withdrawal");
        System.out.println("7. Change Password");
        System.out.println("8. Logout");
        System.out.print("\nEnter your choice: ");
        
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                viewEligibleInternships(student);
                break;
            case "2":
                viewAllInternships(student);
                break;
            case "3":
                applyForInternship(student);
                break;
            case "4":
                viewMyApplications(student);
                break;
            case "5":
                acceptInternship(student);
                break;
            case "6":
                requestWithdrawal(student);
                break;
            case "7":
                changePassword(currentUser);
                break;
            case "8":
                logout();
                break;
            default:
                UIHelper.printErrorMessage("Invalid choice. Please try again.");
        }
    }

    private static void showCompanyRepMenu() {
        CompanyRepresentative rep = (CompanyRepresentative) currentUser;
        UIHelper.printCompanyRepMenu();
        System.out.println("1. Create New Internship");
        System.out.println("2. View My Internships");
        System.out.println("3. Edit Internship");
        System.out.println("4. Delete Internship");
        System.out.println("5. Process Applications");
        System.out.println("6. Toggle Internship Visibility");
        System.out.println("7. Change Password");
        System.out.println("8. Logout");
        System.out.print("\nEnter your choice: ");
        
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                createInternship(rep);
                break;
            case "2":
                viewMyInternships(rep);
                break;
            case "3":
                editInternship(rep);
                break;
            case "4":
                deleteInternship(rep);
                break;
            case "5":
                processApplications(rep);
                break;
            case "6":
                toggleVisibility(rep);
                break;
            case "7":
                changePassword(currentUser);
                break;
            case "8":
                logout();
                break;
            default:
                UIHelper.printErrorMessage("Invalid choice. Please try again.");
        }
    }

    private static void showCareerStaffMenu() {
        CareerCenterStaff staff = (CareerCenterStaff) currentUser;
        System.out.println("\n=== CAREER CENTER STAFF MENU ===");
        System.out.println("1. Approve Company Representative");
        System.out.println("2. Approve Internship");
        System.out.println("3. Reject Internship");
        System.out.println("4. Approve Withdrawal");
        System.out.println("5. Reject Withdrawal");
        System.out.println("6. Generate Reports");
        System.out.println("7. Change Password");
        System.out.println("8. Logout");
        System.out.print("Enter your choice: ");
        
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                approveCompanyRep(staff);
                break;
            case "2":
                approveInternship(staff);
                break;
            case "3":
                rejectInternship(staff);
                break;
            case "4":
                approveWithdrawal(staff);
                break;
            case "5":
                rejectWithdrawal(staff);
                break;
            case "6":
                generateReports(staff);
                break;
            case "7":
                changePassword(currentUser);
                break;
            case "8":
                logout();
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private static void logout() {
        currentUser.logout();
        currentUser = null;
        System.out.println("Logged out successfully.");
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
        
        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine().trim();
        
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }
        
        user.changePassword(newPassword);
        System.out.println("Password changed successfully!");
    }

    private static void viewEligibleInternships(Student student) {
        UIHelper.printSectionHeader("ELIGIBLE INTERNSHIPS");
        var internships = student.viewEligibleInternships();
        if (internships.isEmpty()) {
            System.out.println("No eligible internships found.");
        } else {
            for (var internship : internships) {
                System.out.println("ID: " + internship.getOpportunityID());
                System.out.println("Title: " + internship.getTitle());
                System.out.println("Company: " + internship.getCreatedBy().getCompanyName());
                System.out.println("Level: " + internship.getLevel());
                System.out.println("Preferred Major: " + internship.getPreferredMajor());
                System.out.println("-------------------");
            }
        }
    }

    private static void viewAllInternships(Student student) {
        UIHelper.printSectionHeader("ALL INTERNSHIPS");
        var internships = student.viewAllInternships();
        if (internships.isEmpty()) {
            System.out.println("No internships available.");
        } else {
            for (var internship : internships) {
                System.out.println("ID: " + internship.getOpportunityID());
                System.out.println("Title: " + internship.getTitle());
                System.out.println("Company: " + internship.getCreatedBy().getCompanyName());
                System.out.println("Level: " + internship.getLevel());
                System.out.println("Preferred Major: " + internship.getPreferredMajor());
                System.out.println("Status: " + internship.getStatus());
                System.out.println("Visible: " + (internship.isVisible() ? "Yes" : "No (You applied)"));
                System.out.println("-------------------");
            }
        }
    }

    private static void applyForInternship(Student student) {
        System.out.print("Enter Internship ID: ");
        String internshipID = scanner.nextLine();
        if (internshipID.trim().isEmpty()) {
            System.out.println("Internship ID cannot be empty.");
            return;
        }
        
        boolean result = student.applyForInternship(internshipID);
        if (result) {
            System.out.println("Application submitted successfully!");
        } else {
            InternshipOpportunity opp = Database.getInternship(internshipID);
            if (opp == null) {
                System.out.println("Internship not found.");
            } else if (!opp.isVisible() || !opp.getStatus().equals("Approved")) {
                System.out.println("This internship is not available for applications.");
            } else if (!opp.isOpen()) {
                System.out.println("This internship is not accepting applications at this time. Applications are only accepted between the opening and closing dates.");
            } else if (!opp.getPreferredMajor().equalsIgnoreCase(student.getMajor())) {
                System.out.println("Your major does not match the preferred major for this internship.");
            } else if (student.getYearOfStudy() <= 2 && !opp.getLevel().equals("Basic")) {
                System.out.println("Year 1 and 2 students can only apply for Basic level internships.");
            } else {
                System.out.println("Failed to apply. You may have already applied or reached the maximum of 3 active applications.");
            }
        }
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
                System.out.println("  Internship Status: " + opp.getStatus());
                System.out.println("  Currently Visible: " + (opp.isVisible() ? "Yes" : "No"));
                System.out.println("=".repeat(50));
            }
        }
    }

    private static void acceptInternship(Student student) {
        System.out.print("Enter Application ID: ");
        String applicationID = scanner.nextLine();
        if (applicationID.trim().isEmpty()) {
            System.out.println("Application ID cannot be empty.");
            return;
        }
        student.acceptInternship(applicationID);
        System.out.println("Internship acceptance processed.");
    }

    private static void requestWithdrawal(Student student) {
        System.out.print("Enter Application ID: ");
        String applicationID = scanner.nextLine();
        if (applicationID.trim().isEmpty()) {
            System.out.println("Application ID cannot be empty.");
            return;
        }
        student.requestWithdrawal(applicationID);
        System.out.println("Withdrawal request submitted.");
    }

    private static void createInternship(CompanyRepresentative rep) {
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
        
        System.out.print("Enter Level (Basic/Intermediate/Advanced): ");
        String level = scanner.nextLine();
        if (!level.equals("Basic") && !level.equals("Intermediate") && !level.equals("Advanced")) {
            System.out.println("Invalid level. Must be Basic, Intermediate, or Advanced.");
            return;
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
            Date currentDate = new Date();
            
            Date openingDate = null;
            while (openingDate == null) {
                System.out.print("Enter Opening Date (dd/MM/yyyy): ");
                try {
                    openingDate = dateFormat.parse(scanner.nextLine());
                    // Check if opening date is before current date
                    if (openingDate.before(currentDate)) {
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
                    if (closingDate.before(currentDate)) {
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
            
            if (rep.createInternship(title, description, level, preferredMajor, openingDate, closingDate, maxSlots)) {
                System.out.println("Internship created successfully! It's pending approval from Career Center Staff.");
            } else {
                System.out.println("Failed to create internship. Please check all requirements.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number for max slots.");
        }
    }
    
    private static void viewMyInternships(CompanyRepresentative rep) {
        UIHelper.printSectionHeader("MY INTERNSHIPS");
        boolean found = false;
        for (InternshipOpportunity opp : Database.getInternships()) {
            if (opp.getCreatedBy().getUserID().equals(rep.getUserID())) {
                System.out.println("ID: " + opp.getOpportunityID());
                System.out.println("Title: " + opp.getTitle());
                System.out.println("Description: " + opp.getDescription());
                System.out.println("Level: " + opp.getLevel());
                System.out.println("Preferred Major: " + opp.getPreferredMajor());
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
    
    private static void editInternship(CompanyRepresentative rep) {
        UIHelper.printSectionHeader("EDIT INTERNSHIP (Pending Only)");
        // Show only pending internships
        boolean foundPending = false;
        for (InternshipOpportunity opp : Database.getInternships()) {
            if (opp.getCreatedBy().getUserID().equals(rep.getUserID()) && 
                opp.getStatus().equals("Pending")) {
                System.out.println("ID: " + opp.getOpportunityID());
                System.out.println("Title: " + opp.getTitle());
                System.out.println("Status: " + opp.getStatus());
                System.out.println("-------------------");
                foundPending = true;
            }
        }
        
        if (!foundPending) {
            System.out.println("No pending internships to edit. Note: Approved internships cannot be edited.");
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
        
        if (!opp.getStatus().equals("Pending")) {
            System.out.println("Cannot edit internship. Only pending internships can be edited (Status: " + opp.getStatus() + ")");
            return;
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
        
        Database.saveData();
        System.out.println("Internship updated successfully!");
    }
    
    private static void deleteInternship(CompanyRepresentative rep) {
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

    private static void processApplications(CompanyRepresentative rep) {
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
            System.out.println("Internship: " + app.getOpportunity().getTitle());
            System.out.println("Applied Date: " + app.getAppliedDate());
            System.out.println("=".repeat(50));
        }
        
        // Process application
        System.out.print("\nEnter Application ID to process (or 'cancel' to go back): ");
        String applicationID = scanner.nextLine().trim();
        
        if (applicationID.equalsIgnoreCase("cancel")) {
            UIHelper.printWarningMessage("Operation cancelled.");
            return;
        }
        
        if (applicationID.isEmpty()) {
            UIHelper.printErrorMessage("Application ID cannot be empty.");
            return;
        }
        
        // Verify the application exists in pending list
        boolean found = false;
        for (Application app : pendingApplications) {
            if (app.getApplicationID().equals(applicationID)) {
                found = true;
                break;
            }
        }
        
        if (!found) {
            UIHelper.printErrorMessage("Invalid Application ID or application is not pending.");
            return;
        }
        
        System.out.print("Decision (approve/reject): ");
        String decision = scanner.nextLine().trim().toLowerCase();
        
        if (decision.equals("approve")) {
            if (rep.processApplication(applicationID, true)) {
                UIHelper.printSuccessMessage("✓ Application approved successfully.");
            } else {
                UIHelper.printErrorMessage("Failed to approve application.");
            }
        } else if (decision.equals("reject")) {
            if (rep.processApplication(applicationID, false)) {
                UIHelper.printSuccessMessage("✓ Application rejected successfully.");
            } else {
                UIHelper.printErrorMessage("Failed to reject application.");
            }
        } else {
            UIHelper.printErrorMessage("Invalid decision. Please enter 'approve' or 'reject'.");
        }
    }

    private static void toggleVisibility(CompanyRepresentative rep) {
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
        
        System.out.print("\nEnter Internship ID to toggle (or 'cancel' to go back): ");
        String internshipID = scanner.nextLine().trim();
        
        if (internshipID.equalsIgnoreCase("cancel")) {
            UIHelper.printWarningMessage("Operation cancelled.");
            return;
        }
        
        if (internshipID.isEmpty()) {
            UIHelper.printErrorMessage("Internship ID cannot be empty.");
            return;
        }
        
        // Verify internship exists in the list
        InternshipOpportunity targetOpp = null;
        for (InternshipOpportunity opp : myInternships) {
            if (opp.getOpportunityID().equals(internshipID)) {
                targetOpp = opp;
                break;
            }
        }
        
        if (targetOpp == null) {
            UIHelper.printErrorMessage("Invalid Internship ID or internship not approved.");
            return;
        }
        
        System.out.print("Set visibility to (visible/hidden): ");
        String visibilityInput = scanner.nextLine().trim().toLowerCase();
        
        if (visibilityInput.equals("visible")) {
            rep.toggleVisibility(internshipID, true);
            Database.saveData();
            UIHelper.printSuccessMessage("✓ Internship is now visible to students.");
        } else if (visibilityInput.equals("hidden")) {
            rep.toggleVisibility(internshipID, false);
            Database.saveData();
            UIHelper.printSuccessMessage("✓ Internship is now hidden from students.");
        } else {
            UIHelper.printErrorMessage("Invalid input. Please enter 'visible' or 'hidden'.");
        }
    }

    private static void approveCompanyRep(CareerCenterStaff staff) {
        // First, show pending company representatives
        UIHelper.printSectionHeader("PENDING COMPANY REPRESENTATIVES");
        boolean foundPending = false;
        for (User user : Database.getUsers()) {
            if (user instanceof CompanyRepresentative) {
                CompanyRepresentative rep = (CompanyRepresentative) user;
                if (!rep.isApproved()) {
                    System.out.println("ID: " + rep.getUserID());
                    System.out.println("Name: " + rep.getName());
                    System.out.println("Company: " + rep.getCompanyName());
                    System.out.println("Department: " + rep.getDepartment());
                    System.out.println("Position: " + rep.getPosition());
                    System.out.println("-------------------");
                    foundPending = true;
                }
            }
        }
        if (!foundPending) {
            System.out.println("No pending company representatives.");
            return;
        }
        
        System.out.print("Enter Company Representative ID to approve: ");
        String repID = scanner.nextLine();
        if (repID.trim().isEmpty()) {
            System.out.println("ID cannot be empty.");
            return;
        }
        staff.approveCompanyRep(repID);
        Database.saveData();
        System.out.println("Company representative approved.");
    }

    private static void approveInternship(CareerCenterStaff staff) {
        // First, show pending internships
        System.out.println("\n=== PENDING INTERNSHIPS ===");
        boolean foundPending = false;
        for (InternshipOpportunity opp : Database.getInternships()) {
            if (opp.getStatus().equals("Pending")) {
                System.out.println("ID: " + opp.getOpportunityID());
                System.out.println("Title: " + opp.getTitle());
                System.out.println("Company: " + opp.getCreatedBy().getCompanyName());
                System.out.println("Level: " + opp.getLevel());
                System.out.println("Preferred Major: " + opp.getPreferredMajor());
                System.out.println("Max Slots: " + opp.getMaxSlots());
                System.out.println("-------------------");
                foundPending = true;
            }
        }
        if (!foundPending) {
            System.out.println("No pending internships.");
            return;
        }
        
        System.out.print("Enter Internship ID to approve: ");
        String internshipID = scanner.nextLine();
        if (internshipID.trim().isEmpty()) {
            System.out.println("ID cannot be empty.");
            return;
        }
        if (staff.approveInternship(internshipID)) {
            Database.saveData();
            System.out.println("Internship approved successfully.");
        } else {
            System.out.println("Invalid Internship ID or internship is not in Pending status.");
        }
    }

    private static void rejectInternship(CareerCenterStaff staff) {
        // First, show pending internships
        System.out.println("\n=== PENDING INTERNSHIPS ===");
        boolean foundPending = false;
        for (InternshipOpportunity opp : Database.getInternships()) {
            if (opp.getStatus().equals("Pending")) {
                System.out.println("ID: " + opp.getOpportunityID());
                System.out.println("Title: " + opp.getTitle());
                System.out.println("Company: " + opp.getCreatedBy().getCompanyName());
                System.out.println("-------------------");
                foundPending = true;
            }
        }
        if (!foundPending) {
            System.out.println("No pending internships.");
            return;
        }
        
        System.out.print("Enter Internship ID to reject: ");
        String internshipID = scanner.nextLine();
        if (internshipID.trim().isEmpty()) {
            System.out.println("ID cannot be empty.");
            return;
        }
        if (staff.rejectInternship(internshipID)) {
            Database.saveData();
            System.out.println("Internship rejected successfully.");
        } else {
            System.out.println("Invalid Internship ID or internship is not in Pending status.");
        }
    }

    private static void approveWithdrawal(CareerCenterStaff staff) {
        // First, show withdrawal requests
        System.out.println("\n=== WITHDRAWAL REQUESTS ===");
        boolean foundRequests = false;
        for (Application app : Database.getApplications()) {
            if (app.getStatus().equals("Withdrawal Requested")) {
                System.out.println("Application ID: " + app.getApplicationID());
                System.out.println("Student: " + app.getApplicant().getName());
                System.out.println("Internship: " + app.getOpportunity().getTitle());
                System.out.println("Company: " + app.getOpportunity().getCreatedBy().getCompanyName());
                System.out.println("-------------------");
                foundRequests = true;
            }
        }
        if (!foundRequests) {
            System.out.println("No withdrawal requests.");
            return;
        }
        
        System.out.print("Enter Application ID to approve withdrawal: ");
        String applicationID = scanner.nextLine();
        if (applicationID.trim().isEmpty()) {
            System.out.println("ID cannot be empty.");
            return;
        }
        if (staff.approveWithdrawal(applicationID)) {
            Database.saveData();
            System.out.println("Withdrawal approved successfully.");
        } else {
            System.out.println("Invalid Application ID or application does not have a withdrawal request.");
        }
    }

    private static void rejectWithdrawal(CareerCenterStaff staff) {
        // First, show withdrawal requests
        System.out.println("\n=== WITHDRAWAL REQUESTS ===");
        boolean foundRequests = false;
        for (Application app : Database.getApplications()) {
            if (app.getStatus().equals("Withdrawal Requested")) {
                System.out.println("Application ID: " + app.getApplicationID());
                System.out.println("Student: " + app.getApplicant().getName());
                System.out.println("Internship: " + app.getOpportunity().getTitle());
                System.out.println("-------------------");
                foundRequests = true;
            }
        }
        if (!foundRequests) {
            System.out.println("No withdrawal requests.");
            return;
        }
        
        System.out.print("Enter Application ID to reject withdrawal: ");
        String applicationID = scanner.nextLine();
        if (applicationID.trim().isEmpty()) {
            System.out.println("ID cannot be empty.");
            return;
        }
        if (staff.rejectWithdrawal(applicationID)) {
            Database.saveData();
            System.out.println("Withdrawal rejected successfully.");
        } else {
            System.out.println("Invalid Application ID or application does not have a withdrawal request.");
        }
    }

    private static void generateReports(CareerCenterStaff staff) {
        System.out.println("Enter filters (leave blank to skip):");
        HashMap<String, String> filters = new HashMap<>();
        
        System.out.print("Status: ");
        String status = scanner.nextLine();
        if (!status.trim().isEmpty()) filters.put("status", status);
        
        System.out.print("Level: ");
        String level = scanner.nextLine();
        if (!level.trim().isEmpty()) filters.put("level", level);
        
        System.out.print("Preferred Major: ");
        String major = scanner.nextLine();
        if (!major.trim().isEmpty()) filters.put("preferredMajor", major);
        
        Report report = staff.generateReports(filters);
        report.displayReport();
    }
}