import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class InternshipPlacementSystem {
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    public static void main(String[] args) {
        System.out.println("=== Internship Placement System ===");
        
        while (true) {
            if (currentUser == null) {
                showMainMenu();
            } else {
                showUserMenu();
            }
        }
    }

    private static void showMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Login");
        System.out.println("2. Register as Company Representative");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
        
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
                    System.out.println("Goodbye!");
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
            System.out.print("Enter User ID: ");
            String userID = scanner.nextLine().trim();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();
            
            User user = Database.getUser(userID);
            if (user == null) {
                System.out.println("Invalid user ID.");
                return;
            }
            
            // Check if company representative is approved
            if (user instanceof CompanyRepresentative) {
                CompanyRepresentative rep = (CompanyRepresentative) user;
                if (!rep.isApproved()) {
                    System.out.println("Your account is pending approval from Career Center Staff. Please wait for authorization.");
                    return;
                }
            }
            
            if (user.login(password)) {
                currentUser = user;
                System.out.println("Login successful! Welcome, " + user.getName());
            } else {
                System.out.println("Incorrect password.");
            }
        } catch (Exception e) {
            System.out.println("Error during login. Please try again.");
        }
    }
    
    private static void registerCompanyRep() {
        try {
            System.out.println("\n=== COMPANY REPRESENTATIVE REGISTRATION ===");
            
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
            
            System.out.print("Enter Email: ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) {
                System.out.println("Email cannot be empty.");
                return;
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
        System.out.println("\n=== STUDENT MENU ===");
        System.out.println("1. View Eligible Internships");
        System.out.println("2. Apply for Internship");
        System.out.println("3. View My Applications");
        System.out.println("4. Accept Internship");
        System.out.println("5. Request Withdrawal");
        System.out.println("6. Change Password");
        System.out.println("7. Logout");
        System.out.print("Enter your choice: ");
        
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
                changePassword(currentUser);
                break;
            case "7":
                logout();
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private static void showCompanyRepMenu() {
        CompanyRepresentative rep = (CompanyRepresentative) currentUser;
        System.out.println("\n=== COMPANY REPRESENTATIVE MENU ===");
        System.out.println("1. Create Internship");
        System.out.println("2. View My Internships");
        System.out.println("3. Edit Internship (Before Approval)");
        System.out.println("4. Delete Internship");
        System.out.println("5. View Applications");
        System.out.println("6. Approve Application");
        System.out.println("7. Reject Application");
        System.out.println("8. Toggle Internship Visibility");
        System.out.println("9. Change Password");
        System.out.println("10. Logout");
        System.out.print("Enter your choice: ");
        
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
                viewApplications(rep);
                break;
            case "6":
                approveApplication(rep);
                break;
            case "7":
                rejectApplication(rep);
                break;
            case "8":
                toggleVisibility(rep);
                break;
            case "9":
                changePassword(currentUser);
                break;
            case "10":
                logout();
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
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
        System.out.println("\n=== ELIGIBLE INTERNSHIPS ===");
        var internships = student.viewEligibleInternships();
        if (internships.isEmpty()) {
            System.out.println("No eligible internships found.");
        } else {
            for (var internship : internships) {
                System.out.println("ID: " + internship.getOpportunityID());
                System.out.println("Title: " + internship.getTitle());
                System.out.println("Company: " + internship.getCreatedBy().getCompanyName());
                System.out.println("Level: " + internship.getLevel());
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
        System.out.println("\n=== MY APPLICATIONS ===");
        var applications = student.viewApplications();
        if (applications.isEmpty()) {
            System.out.println("No applications found.");
        } else {
            for (var app : applications) {
                System.out.println("Application ID: " + app.getApplicationID());
                System.out.println("Internship: " + app.getOpportunity().getTitle());
                System.out.println("Company: " + app.getOpportunity().getCreatedBy().getCompanyName());
                System.out.println("Status: " + app.getStatus());
                System.out.println("Applied Date: " + app.getAppliedDate());
                System.out.println("-------------------");
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
            
            Date openingDate = null;
            while (openingDate == null) {
                System.out.print("Enter Opening Date (dd/MM/yyyy): ");
                try {
                    openingDate = dateFormat.parse(scanner.nextLine());
                } catch (ParseException e) {
                    System.out.println("Invalid date format. Please use dd/MM/yyyy.");
                }
            }
            
            Date closingDate = null;
            while (closingDate == null) {
                System.out.print("Enter Closing Date (dd/MM/yyyy): ");
                try {
                    closingDate = dateFormat.parse(scanner.nextLine());
                    if (closingDate.before(openingDate)) {
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
        System.out.println("\n=== MY INTERNSHIPS ===");
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
        System.out.println("\n=== EDIT INTERNSHIP (Pending Only) ===");
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
            Database.updateInternshipTitle(internshipID, title);
        }
        
        System.out.print("Enter new Description [" + opp.getDescription() + "]: ");
        String description = scanner.nextLine().trim();
        if (!description.isEmpty()) {
            Database.updateInternshipDescription(internshipID, description);
        }
        
        System.out.println("Internship updated successfully!");
    }
    
    private static void deleteInternship(CompanyRepresentative rep) {
        System.out.println("\n=== DELETE INTERNSHIP ===");
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

    private static void viewApplications(CompanyRepresentative rep) {
        System.out.println("\n=== APPLICATIONS FOR YOUR INTERNSHIPS ===");
        var applications = rep.viewApplications();
        if (applications.isEmpty()) {
            System.out.println("No applications found.");
        } else {
            for (var app : applications) {
                System.out.println("Application ID: " + app.getApplicationID());
                System.out.println("Student: " + app.getApplicant().getName());
                System.out.println("Internship: " + app.getOpportunity().getTitle());
                System.out.println("Status: " + app.getStatus());
                System.out.println("-------------------");
            }
        }
    }

    private static void approveApplication(CompanyRepresentative rep) {
        System.out.print("Enter Application ID: ");
        String applicationID = scanner.nextLine();
        if (applicationID.trim().isEmpty()) {
            System.out.println("Application ID cannot be empty.");
            return;
        }
        if (rep.approveApplication(applicationID)) {
            System.out.println("Application approved successfully.");
        } else {
            System.out.println("Invalid Application ID or you don't have permission to approve this application.");
        }
    }

    private static void rejectApplication(CompanyRepresentative rep) {
        System.out.print("Enter Application ID: ");
        String applicationID = scanner.nextLine();
        if (applicationID.trim().isEmpty()) {
            System.out.println("Application ID cannot be empty.");
            return;
        }
        if (rep.rejectApplication(applicationID)) {
            System.out.println("Application rejected successfully.");
        } else {
            System.out.println("Invalid Application ID or you don't have permission to reject this application.");
        }
    }

    private static void toggleVisibility(CompanyRepresentative rep) {
        System.out.print("Enter Internship ID: ");
        String internshipID = scanner.nextLine();
        if (internshipID.trim().isEmpty()) {
            System.out.println("Internship ID cannot be empty.");
            return;
        }
        System.out.print("Set visibility (true/false): ");
        String visibilityInput = scanner.nextLine();
        if (!visibilityInput.equals("true") && !visibilityInput.equals("false")) {
            System.out.println("Invalid input. Please enter true or false.");
            return;
        }
        boolean visible = Boolean.parseBoolean(visibilityInput);
        rep.toggleVisibility(internshipID, visible);
        System.out.println("Visibility updated.");
    }

    private static void approveCompanyRep(CareerCenterStaff staff) {
        // First, show pending company representatives
        System.out.println("\n=== PENDING COMPANY REPRESENTATIVES ===");
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
        staff.approveInternship(internshipID);
        System.out.println("Internship approved.");
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
        staff.rejectInternship(internshipID);
        System.out.println("Internship rejected.");
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
        staff.approveWithdrawal(applicationID);
        System.out.println("Withdrawal approved.");
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
        staff.rejectWithdrawal(applicationID);
        System.out.println("Withdrawal rejected.");
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