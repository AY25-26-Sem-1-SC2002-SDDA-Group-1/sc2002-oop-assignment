import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class FilterSettings {
    private String statusFilter = "";
    private String levelFilter = "";
    private String majorFilter = "";
    private double minGPAFilter = 0.0; // Minimum GPA filter
    private String sortBy = "title"; // Default sort by title (alphabetical)
    
    public void setStatusFilter(String status) { this.statusFilter = status; }
    public void setLevelFilter(String level) { this.levelFilter = level; }
    public void setMajorFilter(String major) { this.majorFilter = major; }
    public void setMinGPAFilter(double minGPA) { this.minGPAFilter = minGPA; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getStatusFilter() { return statusFilter; }
    public String getLevelFilter() { return levelFilter; }
    public String getMajorFilter() { return majorFilter; }
    public double getMinGPAFilter() { return minGPAFilter; }
    public String getSortBy() { return sortBy; }
    
    public boolean hasActiveFilters() {
        return !statusFilter.isEmpty() || !levelFilter.isEmpty() || !majorFilter.isEmpty() || minGPAFilter > 0.0;
    }
    
    public void clearFilters() {
        statusFilter = "";
        levelFilter = "";
        majorFilter = "";
        minGPAFilter = 0.0;
    }
    
    public List<InternshipOpportunity> applyFilters(List<InternshipOpportunity> opportunities) {
        return opportunities.stream()
            .filter(opp -> statusFilter.isEmpty() || opp.getStatus().equalsIgnoreCase(statusFilter))
            .filter(opp -> levelFilter.isEmpty() || opp.getLevel().equalsIgnoreCase(levelFilter))
            .filter(opp -> majorFilter.isEmpty() || opp.getPreferredMajor().equalsIgnoreCase(majorFilter))
            .filter(opp -> minGPAFilter == 0.0 || opp.getMinGPA() >= minGPAFilter)
            .sorted(getComparator())
            .collect(Collectors.toList());
    }
    
    private Comparator<InternshipOpportunity> getComparator() {
        switch (sortBy.toLowerCase()) {
            case "title":
                return Comparator.comparing(InternshipOpportunity::getTitle);
            case "company":
                return Comparator.comparing(opp -> opp.getCreatedBy().getCompanyName());
            case "level":
                return Comparator.comparing(InternshipOpportunity::getLevel);
            case "closing":
                return Comparator.comparing(InternshipOpportunity::getClosingDate);
            default:
                return Comparator.comparing(InternshipOpportunity::getTitle);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Active Filters: ");
        if (!statusFilter.isEmpty()) sb.append("Status=").append(statusFilter).append(" ");
        if (!levelFilter.isEmpty()) sb.append("Level=").append(levelFilter).append(" ");
        if (!majorFilter.isEmpty()) sb.append("Major=").append(majorFilter).append(" ");
        if (minGPAFilter > 0.0) sb.append("Min GPA>=").append(minGPAFilter).append(" ");
        sb.append("| Sort by: ").append(sortBy);
        return sb.toString();
    }
}

public class InternshipPlacementSystem {
    private static final Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;
    private static FilterSettings userFilters = new FilterSettings(); // Persistent filter settings

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
                    registerStudent();
                    break;
                case "3":
                    registerStaff();
                    break;
                case "4":
                    registerCompanyRep();
                    break;
                case "5":
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
    
    private static void registerStudent() {
        try {
            UIHelper.printSectionHeader("STUDENT REGISTRATION");

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

            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();
            if (password.isEmpty()) {
                System.out.println("Password cannot be empty.");
                return;
            }

            System.out.print("Enter Year of Study (1-4): ");
            int yearOfStudy;
            try {
                yearOfStudy = Integer.parseInt(scanner.nextLine().trim());
                if (yearOfStudy < 1 || yearOfStudy > 4) {
                    System.out.println("Year of study must be between 1 and 4.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid year of study. Please enter a number between 1 and 4.");
                return;
            }

            System.out.print("Enter Major (CS/EEE/BM): ");
            String major = scanner.nextLine().trim().toUpperCase();
            if (!major.equals("CS") && !major.equals("EEE") && !major.equals("BM")) {
                System.out.println("Invalid major. Please enter CS, EEE, or BM.");
                return;
            }

            System.out.print("Enter GPA (0.0-4.0): ");
            double gpa;
            try {
                gpa = Double.parseDouble(scanner.nextLine().trim());
                if (gpa < 0.0 || gpa > 4.0) {
                    System.out.println("GPA must be between 0.0 and 4.0.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid GPA format. Please enter a number between 0.0 and 4.0.");
                return;
            }

            Student newStudent = new Student(userID, name, password, yearOfStudy, major, gpa);
            Database.addUser(newStudent);
            Database.saveData();
            System.out.println("Registration successful!");
            System.out.println("Your User ID is: " + userID);
            System.out.println("You can now login to access the system.");
        } catch (Exception e) {
            System.out.println("Error during registration. Please try again.");
        }
    }

    private static void registerStaff() {
        try {
            UIHelper.printSectionHeader("CAREER CENTER STAFF REGISTRATION");

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

            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();
            if (password.isEmpty()) {
                System.out.println("Password cannot be empty.");
                return;
            }

            System.out.print("Enter Staff Department: ");
            String staffDepartment = scanner.nextLine().trim();
            if (staffDepartment.isEmpty()) {
                System.out.println("Staff department cannot be empty.");
                return;
            }

            CareerCenterStaff newStaff = new CareerCenterStaff(userID, name, password, staffDepartment);
            Database.addUser(newStaff);
            Database.saveData();
            System.out.println("Registration successful!");
            System.out.println("Your User ID is: " + userID);
            System.out.println("You can now login to access the system.");
        } catch (Exception e) {
            System.out.println("Error during registration. Please try again.");
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
        System.out.println("2. Apply for Internship");
        System.out.println("3. View My Applications");
        System.out.println("4. Accept Internship");
        System.out.println("5. Request Withdrawal");
        System.out.println("6. Manage Filters");
        System.out.println("7. Change Password");
        System.out.println("8. View Statistics");
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
                manageFilters();
                break;
            case "7":
                changePassword(currentUser);
                break;
            case "8":
                viewStudentStatistics(student);
                break;
            case "9":
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
        System.out.println("5. View Application Details");
        System.out.println("6. Process Applications");
        System.out.println("7. Toggle Internship Visibility");
        System.out.println("8. View All Internships (Filtered)");
        System.out.println("9. Manage Filters");
        System.out.println("10. Change Password");
        System.out.println("11. View Statistics");
        System.out.println("12. Logout");
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
                viewApplicationDetails(rep);
                break;
            case "6":
                processApplications(rep);
                break;
            case "7":
                toggleVisibility(rep);
                break;
            case "8":
                viewAllInternshipsFiltered();
                break;
            case "9":
                manageFilters();
                break;
            case "10":
                changePassword(currentUser);
                break;
            case "11":
                viewCompanyRepStatistics(rep);
                break;
            case "12":
                logout();
                break;
            default:
                UIHelper.printErrorMessage("Invalid choice. Please try again.");
        }
    }

    private static void showCareerStaffMenu() {
        CareerCenterStaff staff = (CareerCenterStaff) currentUser;
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
        
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                processCompanyReps(staff);
                break;
            case "2":
                processInternships(staff);
                break;
            case "3":
                processWithdrawals(staff);
                break;
            case "4":
                viewAllInternshipsFiltered();
                break;
            case "5":
                manageFilters();
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
                UIHelper.printErrorMessage("Invalid choice. Please try again.");
        }
    }

    public static void logout() {
        if (currentUser != null) {
            currentUser.logout();
            currentUser = null;
            System.out.println("Logged out successfully.");
        }
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

    private static void viewEligibleInternships(Student student) {
        UIHelper.printSectionHeader("ELIGIBLE INTERNSHIPS");
        
        if (userFilters.hasActiveFilters()) {
            System.out.println(userFilters.toString());
            System.out.println();
        }
        
        var internships = student.viewEligibleInternships();
        internships = userFilters.applyFilters(internships);
        
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
                System.out.println("Minimum GPA: " + internship.getMinGPA());
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

            System.out.print("Enter Minimum GPA (0.0-4.0): ");
            double minGPA = Double.parseDouble(scanner.nextLine());
            if (minGPA < 0.0 || minGPA > 4.0) {
                System.out.println("Minimum GPA must be between 0.0 and 4.0.");
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
            
            if (rep.createInternship(title, description, level, preferredMajor, openingDate, closingDate, maxSlots, minGPA)) {
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
    
    private static void viewApplicationDetails(CompanyRepresentative rep) {
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
            System.out.println("-".repeat(70));
        }
        
        System.out.println("\nTotal Applications: " + applications.size());
        System.out.println("=".repeat(70));
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

    private static void processCompanyReps(CareerCenterStaff staff) {
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
            UIHelper.printWarningMessage("Company representative registration rejected. (Account remains pending)");
        } else {
            UIHelper.printErrorMessage("Invalid decision. Please enter 'approve' or 'reject'.");
        }
    }

    private static void processInternships(CareerCenterStaff staff) {
        UIHelper.printSectionHeader("PROCESS INTERNSHIPS");
        
        List<InternshipOpportunity> pendingInternships = staff.getPendingInternships();
        
        if (pendingInternships.isEmpty()) {
            UIHelper.printWarningMessage("No pending internships to process.");
            return;
        }
        
        // Display all pending internships
        System.out.println("\nPending Internships:");
        for (InternshipOpportunity opp : pendingInternships) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("ID: " + opp.getOpportunityID());
            System.out.println("Title: " + opp.getTitle());
            System.out.println("Description: " + opp.getDescription());
            System.out.println("Company: " + opp.getCreatedBy().getCompanyName());
            System.out.println("Requested by: " + opp.getCreatedBy().getName() + " (" + opp.getCreatedBy().getPosition() + ")");
            System.out.println("Level: " + opp.getLevel());
            System.out.println("Preferred Major: " + opp.getPreferredMajor());
            System.out.println("Max Slots: " + opp.getMaxSlots());
            System.out.println("Opening Date: " + opp.getOpeningDate());
            System.out.println("Closing Date: " + opp.getClosingDate());
            System.out.println("=".repeat(50));
        }
        
        System.out.print("\nEnter Internship ID(s) to process (space-separated for multiple, or 'cancel' to go back): ");
        String input = scanner.nextLine().trim();
        
        if (input.equalsIgnoreCase("cancel")) {
            UIHelper.printWarningMessage("Operation cancelled.");
            return;
        }
        
        if (input.isEmpty()) {
            UIHelper.printErrorMessage("Internship ID cannot be empty.");
            return;
        }
        
        // Split by spaces for mass processing
        String[] internshipIDs = input.split("\\s+");
        
        System.out.print("Decision (approve/reject): ");
        String decision = scanner.nextLine().trim().toLowerCase();
        
        if (!decision.equals("approve") && !decision.equals("reject")) {
            UIHelper.printErrorMessage("Invalid decision. Please enter 'approve' or 'reject'.");
            return;
        }
        
        boolean isApprove = decision.equals("approve");
        int successCount = 0;
        int failCount = 0;
        
        for (String internshipID : internshipIDs) {
            internshipID = internshipID.trim();
            
            // Verify internship exists in pending list
            boolean found = false;
            for (InternshipOpportunity opp : pendingInternships) {
                if (opp.getOpportunityID().equals(internshipID)) {
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                System.out.println("Skipping " + internshipID + ": Invalid ID or not pending.");
                failCount++;
                continue;
            }
            
            if (staff.processInternship(internshipID, isApprove)) {
                System.out.println(internshipID + ": " + (isApprove ? "Approved and set to visible" : "Rejected"));
                successCount++;
            } else {
                System.out.println(internshipID + ": Failed to process");
                failCount++;
            }
        }
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Processing complete: " + successCount + " succeeded, " + failCount + " failed.");
        System.out.println("=".repeat(50));
    }

    private static void processWithdrawals(CareerCenterStaff staff) {
        UIHelper.printSectionHeader("PROCESS WITHDRAWAL REQUESTS");
        
        List<Application> withdrawalRequests = staff.getWithdrawalRequests();
        
        if (withdrawalRequests.isEmpty()) {
            UIHelper.printWarningMessage("No withdrawal requests to process.");
            return;
        }
        
        // Display all withdrawal requests
        System.out.println("\nWithdrawal Requests:");
        for (Application app : withdrawalRequests) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("Application ID: " + app.getApplicationID());
            System.out.println("Student: " + app.getApplicant().getName());
            System.out.println("Student ID: " + app.getApplicant().getUserID());
            System.out.println("Internship: " + app.getOpportunity().getTitle());
            System.out.println("Company: " + app.getOpportunity().getCreatedBy().getCompanyName());
            System.out.println("Applied Date: " + app.getAppliedDate());
            System.out.println("=".repeat(50));
        }
        
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
        
        // Verify application exists in withdrawal requests list
        boolean found = false;
        for (Application app : withdrawalRequests) {
            if (app.getApplicationID().equals(applicationID)) {
                found = true;
                break;
            }
        }
        
        if (!found) {
            UIHelper.printErrorMessage("Invalid Application ID or no withdrawal request exists.");
            return;
        }
        
        System.out.print("Decision (approve/reject): ");
        String decision = scanner.nextLine().trim().toLowerCase();
        
        if (decision.equals("approve")) {
            if (staff.processWithdrawal(applicationID, true)) {
                UIHelper.printSuccessMessage(" Withdrawal request approved. Application status changed to Withdrawn.");
            } else {
                UIHelper.printErrorMessage("Failed to approve withdrawal.");
            }
        } else if (decision.equals("reject")) {
            if (staff.processWithdrawal(applicationID, false)) {
                UIHelper.printSuccessMessage(" Withdrawal request rejected. Application remains Pending.");
            } else {
                UIHelper.printErrorMessage("Failed to reject withdrawal.");
            }
        } else {
            UIHelper.printErrorMessage("Invalid decision. Please enter 'approve' or 'reject'.");
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
    
    // ============================================
    // FILTER MANAGEMENT METHODS
    // ============================================
    
    private static void manageFilters() {
        UIHelper.printSectionHeader("MANAGE FILTERS");
        System.out.println("Current Settings:");
        System.out.println(userFilters.toString());
        System.out.println();
        
        System.out.println("1. Set Status Filter (Pending/Approved/Rejected)");
        System.out.println("2. Set Level Filter (Undergraduate/Graduate/Both)");
        System.out.println("3. Set Major Filter (CS/EEE/BM/All)");
        System.out.println("4. Set Minimum GPA Filter (0.0-4.0)");
        System.out.println("5. Change Sort By (Title/Company/Level/Closing)");
        System.out.println("6. Clear All Filters");
        System.out.println("7. Back to Main Menu");
        System.out.print("\nEnter your choice: ");
        
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                System.out.print("Enter status (Pending/Approved/Rejected) or leave blank: ");
                userFilters.setStatusFilter(scanner.nextLine().trim());
                UIHelper.printSuccessMessage("Status filter updated!");
                break;
            case "2":
                System.out.print("Enter level (Undergraduate/Graduate/Both) or leave blank: ");
                userFilters.setLevelFilter(scanner.nextLine().trim());
                UIHelper.printSuccessMessage("Level filter updated!");
                break;
            case "3":
                System.out.print("Enter major (CS/EEE/BM/All) or leave blank: ");
                userFilters.setMajorFilter(scanner.nextLine().trim());
                UIHelper.printSuccessMessage("Major filter updated!");
                break;
            case "4":
                System.out.print("Enter minimum GPA (0.0-4.0) or 0 to disable: ");
                try {
                    double minGPA = Double.parseDouble(scanner.nextLine().trim());
                    if (minGPA < 0.0 || minGPA > 4.0) {
                        UIHelper.printErrorMessage("GPA must be between 0.0 and 4.0.");
                    } else {
                        userFilters.setMinGPAFilter(minGPA);
                        UIHelper.printSuccessMessage("GPA filter updated!");
                    }
                } catch (NumberFormatException e) {
                    UIHelper.printErrorMessage("Invalid GPA format.");
                }
                break;
            case "5":
                System.out.print("Sort by (Title/Company/Level/Closing): ");
                String sortBy = scanner.nextLine().trim();
                if (!sortBy.isEmpty()) {
                    userFilters.setSortBy(sortBy);
                    UIHelper.printSuccessMessage("Sort preference updated!");
                }
                break;
            case "6":
                userFilters.clearFilters();
                UIHelper.printSuccessMessage("All filters cleared!");
                break;
            case "7":
                return;
            default:
                UIHelper.printErrorMessage("Invalid choice.");
        }
    }
    
    private static void viewAllInternshipsFiltered() {
        UIHelper.printSectionHeader("ALL INTERNSHIPS (FILTERED)");
        
        if (userFilters.hasActiveFilters()) {
            System.out.println(userFilters.toString());
            System.out.println();
        }
        
        List<InternshipOpportunity> allInternships = Database.getInternships();
        allInternships = userFilters.applyFilters(allInternships);
        
        if (allInternships.isEmpty()) {
            System.out.println("No internships match your filters.");
        } else {
            for (InternshipOpportunity internship : allInternships) {
                System.out.println("ID: " + internship.getOpportunityID());
                System.out.println("Title: " + internship.getTitle());
                System.out.println("Company: " + internship.getCreatedBy().getCompanyName());
                System.out.println("Level: " + internship.getLevel());
                System.out.println("Preferred Major: " + internship.getPreferredMajor());
                System.out.println("Status: " + internship.getStatus());
                System.out.println("Closing Date: " + internship.getClosingDate());
                System.out.println("Visible: " + (internship.isVisible() ? "Yes" : "No"));
                System.out.println("-------------------");
            }
        }
    }

    private static void viewStudentStatistics(Student student) {
        UIHelper.printSectionHeader("STUDENT STATISTICS");

        int totalApplications = 0;
        int pendingApplications = 0;
        int successfulApplications = 0;
        int unsuccessfulApplications = 0;
        int confirmedPlacements = 0;
        int withdrawnApplications = 0;

        for (Application app : Database.getApplications()) {
            if (app.getApplicant().getUserID().equals(student.getUserID())) {
                totalApplications++;
                switch (app.getStatus()) {
                    case "Pending":
                        pendingApplications++;
                        break;
                    case "Successful":
                        successfulApplications++;
                        break;
                    case "Unsuccessful":
                        unsuccessfulApplications++;
                        break;
                    case "Confirmed":
                        confirmedPlacements++;
                        break;
                    case "Withdrawn":
                        withdrawnApplications++;
                        break;
                }
            }
        }

        System.out.println("Personal Information:");
        System.out.println("  Name: " + student.getName());
        System.out.println("  Student ID: " + student.getUserID());
        System.out.println("  Year of Study: Year " + student.getYearOfStudy());
        System.out.println("  Major: " + student.getMajor());
        System.out.println("  GPA: " + student.getGpa());
        System.out.println();

        System.out.println("Application Statistics:");
        System.out.println("  Total Applications: " + totalApplications);
        System.out.println("  Pending: " + pendingApplications);
        System.out.println("  Successful: " + successfulApplications);
        System.out.println("  Unsuccessful: " + unsuccessfulApplications);
        System.out.println("  Confirmed Placements: " + confirmedPlacements);
        System.out.println("  Withdrawn: " + withdrawnApplications);
        System.out.println();

        double successRate = totalApplications > 0 ? (double) (successfulApplications + confirmedPlacements) / totalApplications * 100 : 0;
        System.out.println("Success Rate: " + String.format("%.1f", successRate) + "%");
    }

    private static void viewCompanyRepStatistics(CompanyRepresentative rep) {
        UIHelper.printSectionHeader("COMPANY REPRESENTATIVE STATISTICS");

        int totalInternships = 0;
        int pendingInternships = 0;
        int approvedInternships = 0;
        int rejectedInternships = 0;
        int filledInternships = 0;
        int visibleInternships = 0;

        int totalApplications = 0;
        int pendingApplications = 0;
        int successfulApplications = 0;
        int unsuccessfulApplications = 0;
        int confirmedPlacements = 0;

        for (InternshipOpportunity opp : Database.getInternships()) {
            if (opp.getCreatedBy().getUserID().equals(rep.getUserID())) {
                totalInternships++;
                switch (opp.getStatus()) {
                    case "Pending":
                        pendingInternships++;
                        break;
                    case "Approved":
                        approvedInternships++;
                        break;
                    case "Rejected":
                        rejectedInternships++;
                        break;
                    case "Filled":
                        filledInternships++;
                        break;
                }
                if (opp.isVisible()) {
                    visibleInternships++;
                }

                // Count applications for this internship
                for (Application app : Database.getApplications()) {
                    if (app.getOpportunity().getOpportunityID().equals(opp.getOpportunityID())) {
                        totalApplications++;
                        switch (app.getStatus()) {
                            case "Pending":
                                pendingApplications++;
                                break;
                            case "Successful":
                                successfulApplications++;
                                break;
                            case "Unsuccessful":
                                unsuccessfulApplications++;
                                break;
                            case "Confirmed":
                                confirmedPlacements++;
                                break;
                        }
                    }
                }
            }
        }

        System.out.println("Personal Information:");
        System.out.println("  Name: " + rep.getName());
        System.out.println("  Company: " + rep.getCompanyName());
        System.out.println("  Position: " + rep.getPosition());
        System.out.println("  Department: " + rep.getDepartment());
        System.out.println();

        System.out.println("Internship Statistics:");
        System.out.println("  Total Internships Created: " + totalInternships);
        System.out.println("  Pending Approval: " + pendingInternships);
        System.out.println("  Approved: " + approvedInternships);
        System.out.println("  Rejected: " + rejectedInternships);
        System.out.println("  Filled: " + filledInternships);
        System.out.println("  Currently Visible: " + visibleInternships);
        System.out.println();

        System.out.println("Application Statistics:");
        System.out.println("  Total Applications Received: " + totalApplications);
        System.out.println("  Pending Review: " + pendingApplications);
        System.out.println("  Approved: " + successfulApplications);
        System.out.println("  Rejected: " + unsuccessfulApplications);
        System.out.println("  Confirmed Placements: " + confirmedPlacements);
        System.out.println();

        double fillRate = approvedInternships > 0 ? (double) filledInternships / approvedInternships * 100 : 0;
        System.out.println("Internship Fill Rate: " + String.format("%.1f", fillRate) + "%");
    }
}