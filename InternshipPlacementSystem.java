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
        System.out.println("2. Exit");
        System.out.print("Enter your choice: ");
        
        try {
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    login();
                    break;
                case "2":
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
            if (user != null && user.login(password)) {
                currentUser = user;
                System.out.println("Login successful! Welcome, " + user.getName() + "!");
            } else {
                System.out.println("Invalid User ID or Password.");
            }
        } catch (Exception e) {
            System.out.println("Error reading input. Please try again.");
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
        System.out.println("6. Logout");
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
        System.out.println("2. View Applications");
        System.out.println("3. Approve Application");
        System.out.println("4. Reject Application");
        System.out.println("5. Toggle Internship Visibility");
        System.out.println("6. Logout");
        System.out.print("Enter your choice: ");
        
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                createInternship(rep);
                break;
            case "2":
                viewApplications(rep);
                break;
            case "3":
                approveApplication(rep);
                break;
            case "4":
                rejectApplication(rep);
                break;
            case "5":
                toggleVisibility(rep);
                break;
            case "6":
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
        System.out.println("7. Logout");
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
                System.out.println("Status: " + internship.getStatus());
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
        
        if (student.applyForInternship(internshipID)) {
            System.out.println("Application submitted successfully!");
        } else {
            System.out.println("Failed to apply. Please check if you're eligible and haven't already applied.");
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
            System.out.println("Your account is not approved yet.");
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
        
        System.out.print("Enter Max Slots: ");
        try {
            int maxSlots = Integer.parseInt(scanner.nextLine());
            if (maxSlots <= 0) {
                System.out.println("Max slots must be positive.");
                return;
            }
            
            Date openingDate = new Date();
            Date closingDate = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
            
            if (rep.createInternship(title, description, level, preferredMajor, openingDate, closingDate, maxSlots)) {
                System.out.println("Internship created successfully! It's pending approval.");
            } else {
                System.out.println("Failed to create internship.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number for max slots.");
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
        rep.approveApplication(applicationID);
        System.out.println("Application approved.");
    }

    private static void rejectApplication(CompanyRepresentative rep) {
        System.out.print("Enter Application ID: ");
        String applicationID = scanner.nextLine();
        if (applicationID.trim().isEmpty()) {
            System.out.println("Application ID cannot be empty.");
            return;
        }
        rep.rejectApplication(applicationID);
        System.out.println("Application rejected.");
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
        System.out.print("Enter Company Representative ID: ");
        String repID = scanner.nextLine();
        staff.approveCompanyRep(repID);
        System.out.println("Company representative approved.");
    }

    private static void approveInternship(CareerCenterStaff staff) {
        System.out.print("Enter Internship ID: ");
        String internshipID = scanner.nextLine();
        staff.approveInternship(internshipID);
        System.out.println("Internship approved.");
    }

    private static void rejectInternship(CareerCenterStaff staff) {
        System.out.print("Enter Internship ID: ");
        String internshipID = scanner.nextLine();
        staff.rejectInternship(internshipID);
        System.out.println("Internship rejected.");
    }

    private static void approveWithdrawal(CareerCenterStaff staff) {
        System.out.print("Enter Application ID: ");
        String applicationID = scanner.nextLine();
        staff.approveWithdrawal(applicationID);
        System.out.println("Withdrawal approved.");
    }

    private static void rejectWithdrawal(CareerCenterStaff staff) {
        System.out.print("Enter Application ID: ");
        String applicationID = scanner.nextLine();
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