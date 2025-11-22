import java.util.Scanner;

/**
 * Main system class for the internship placement application.
 * Manages user authentication, registration, and menu navigation.
 */
public class InternshipPlacementSystem {
    private final Scanner scanner = new Scanner(System.in);
    private User currentUser = null;
    private IMenuHandler currentMenuHandler = null;
    private ServiceFactory serviceFactory;

    /**
     * Constructs the InternshipPlacementSystem and initializes services via DI container.
     */
    public InternshipPlacementSystem() {
        // Initialize DI container
        this.serviceFactory = new ServiceFactory();
        serviceFactory.initialize();
    }

    /**
     * Main entry point of the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        UIHelper.printWelcomeBanner();
        new InternshipPlacementSystem().run();
    }

    /**
     * Runs the main application loop.
     */
    private void run() {
        while (true) {
            if (currentUser == null) {
                showMainMenu();
            } else {
                getMenuHandler().showMenu();
                // Check if user logged out
                if (currentUser != null && !currentUser.isLoggedIn()) {
                    currentUser = null;
                    currentMenuHandler = null; // Clear cached menu handler
                }
            }
        }
    }

    /**
     * Gets the menu handler for the current user.
     *
     * @return the menu handler
     */
    private IMenuHandler getMenuHandler() {
        // Cache the menu handler to preserve state like filters
        if (currentMenuHandler == null || !isCurrentMenuHandlerValid()) {
            currentMenuHandler = MenuHandlerFactory.createMenuHandler(currentUser, serviceFactory.getInternshipService(), serviceFactory.getApplicationService(), serviceFactory.getUserService(), scanner);
        }
        return currentMenuHandler;
    }

    /**
     * Checks if the current menu handler is still valid for the current user.
     *
     * @return true if valid
     */
    private boolean isCurrentMenuHandlerValid() {
        if (currentMenuHandler == null || currentUser == null) {
            return false;
        }
        // For now, assume it's valid. Could add more sophisticated checks if needed.
        return true;
    }

    /**
     * Displays the main menu for login and registration.
     */
    private void showMainMenu() {
        UIHelper.printMainMenu();
        try {
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    login();
                    break;
                case "2":
                    showRegistrationMenu();
                    break;
                case "3":
                    UIHelper.printGoodbyeMessage();
                    try {
                        serviceFactory.getUserRepository().saveUsers();
                        serviceFactory.getApplicationRepository().saveApplications();
                    } catch (Exception e) {
                        System.err.println("Error saving data on exit: " + e.getMessage());
                    }
                    System.exit(0);
                    break;
                 default:
                     UIHelper.printErrorMessage("Invalid choice. Please try again.");
             }
         } catch (Exception e) {
             UIHelper.printErrorMessage("Error reading input. Please try again.");
         }
     }

     private void showRegistrationMenu() {
        UIHelper.printRegistrationMenu();
        try {
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    registerStudent();
                    break;
                case "2":
                    registerStaff();
                    break;
                case "3":
                    registerCompanyRep();
                    break;
                case "4":
                    // Back to main menu - do nothing, loop continues
                    break;
                 default:
                     UIHelper.printErrorMessage("Invalid choice. Please try again.");
             }
         } catch (Exception e) {
             UIHelper.printErrorMessage("Error reading input. Please try again.");
         }
    }

    private void login() {
        try {
            UIHelper.printSectionHeader("USER LOGIN");
            System.out.print("  User ID: ");
            String userID = scanner.nextLine().trim();
            System.out.print("  Password: ");
            String password = scanner.nextLine().trim();

            User user = serviceFactory.getUserService().login(userID, password);
            if (user == null) {
                UIHelper.printErrorMessage("Invalid user ID or password.");
                return;
            }

            // Check if company representative is approved
            if (user.isCompanyRepresentative()) {
                CompanyRepresentative rep = user.asCompanyRepresentative();
                if (rep.isRejected()) {
                    UIHelper.printErrorMessage("Your account is rejected.");
                    return;
                }
                if (!rep.isApproved()) {
                    UIHelper.printWarningMessage("Your account is pending approval.");
                    return;
                }
            }

            currentUser = user;
            UIHelper.printSuccessMessage("Login successful! Welcome, " + user.getName() + "!");
        } catch (Exception e) {
            UIHelper.printErrorMessage("Error during login. Please try again.");
        }
    }

    /**
     * Handles student registration process, using MajorCatalog for consistent major selection.
     */
    private void registerStudent() {
        try {
            UIHelper.printSectionHeader("STUDENT REGISTRATION");
            System.out.print("Enter User ID: ");
            String userID = scanner.nextLine().trim();

            // Validate User ID format for students
            if (!isValidStudentUserId(userID)) {
                UIHelper.printErrorMessage("Invalid User ID format. Student IDs must start with 'U', followed by 7 digits, and end with a letter (e.g., U2345123F).");
                return;
            }

            // Check for duplicate User ID immediately
            if (!serviceFactory.getUserService().isUserIdAvailable(userID, false)) {
                UIHelper.printErrorMessage("User ID already exists. Registration cancelled.");
                return;
            }
            
            System.out.print("Enter Name: ");
            String name = scanner.nextLine().trim();
            if (!serviceFactory.getUserService().isValidName(name)) {
                UIHelper.printErrorMessage("Invalid name. Name must be 2-50 characters long.");
                return;
            }

            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();
            if (!serviceFactory.getUserService().isValidPassword(password)) {
                UIHelper.printErrorMessage("Invalid password. Password must be at least 6 characters long.");
                return;
            }

            System.out.print("Enter Year of Study (1-4): ");
            int yearOfStudy;
            try {
                yearOfStudy = Integer.parseInt(scanner.nextLine().trim());
                if (!serviceFactory.getUserService().isValidYearOfStudy(yearOfStudy)) {
                    UIHelper.printErrorMessage("Invalid year of study. Must be between 1 and 4.");
                    return;
                }
            } catch (NumberFormatException e) {
                UIHelper.printErrorMessage("Invalid year of study. Please enter a number between 1 and 4.");
                return;
            }

            MajorCatalog.displayMajors();
            System.out.print("Enter number or Major: ");
            String majorInput = scanner.nextLine().trim();
            String major = MajorCatalog.resolveMajor(majorInput);
            
            if (major == null) {
                UIHelper.printErrorMessage("Invalid major. Registration cancelled.");
                return;
            }

            System.out.print("Enter GPA (0.0-5.0): ");
            double gpa;
            try {
                gpa = Double.parseDouble(scanner.nextLine().trim());
                if (!serviceFactory.getUserService().isValidGpa(gpa)) {
                    UIHelper.printErrorMessage("Invalid GPA. GPA must be between 0.0 and 5.0.");
                    return;
                }
            } catch (NumberFormatException e) {
                UIHelper.printErrorMessage("Invalid GPA. Please enter a number between 0.0 and 5.0.");
                return;
            }

            if (serviceFactory.getUserService().registerStudent(userID, name, password, yearOfStudy, major, gpa)) {
                UIHelper.printSuccessMessage("Registration successful!");
            } else {
                UIHelper.printErrorMessage("Registration failed.");
            }
        } catch (Exception e) {
            UIHelper.printErrorMessage("Error during registration. Please try again.");
        }
    }

    private void registerStaff() {
        try {
            UIHelper.printSectionHeader("CAREER CENTER STAFF REGISTRATION");
            System.out.print("Enter User ID: ");
            String userID = scanner.nextLine().trim();
            
            // Check for duplicate User ID immediately
            if (!serviceFactory.getUserService().isUserIdAvailable(userID, false)) {
                UIHelper.printErrorMessage("User ID already exists. Registration cancelled.");
                return;
            }
            
            System.out.print("Enter Name: ");
            String name = scanner.nextLine().trim();
            if (!serviceFactory.getUserService().isValidName(name)) {
                UIHelper.printErrorMessage("Invalid name. Name must be 2-50 characters long.");
                return;
            }

            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();
            if (!serviceFactory.getUserService().isValidPassword(password)) {
                UIHelper.printErrorMessage("Invalid password. Password must be at least 6 characters long.");
                return;
            }

            System.out.print("Enter Staff Department: ");
            String department = scanner.nextLine().trim();
            if (!serviceFactory.getUserService().isValidDepartment(department)) {
                UIHelper.printErrorMessage("Invalid department. Department must be 2-50 characters long.");
                return;
            }

            if (serviceFactory.getUserService().registerStaff(userID, name, password, department)) {
                UIHelper.printSuccessMessage("Registration successful!");
            } else {
                UIHelper.printErrorMessage("Registration failed.");
            }
        } catch (Exception e) {
            UIHelper.printErrorMessage("Error during registration. Please try again.");
        }
    }

    private void registerCompanyRep() {
        try {
            UIHelper.printSectionHeader("COMPANY REPRESENTATIVE REGISTRATION");
            System.out.print("Enter User ID: ");
            String userID = scanner.nextLine().trim();
            
            // Check for duplicate User ID immediately (allows rejected usernames to be reused)
            if (!serviceFactory.getUserService().isUserIdAvailable(userID, true)) {
                UIHelper.printErrorMessage("User ID already exists. Registration cancelled.");
                return;
            }
            
            System.out.print("Enter Name: ");
            String name = scanner.nextLine().trim();
            if (!serviceFactory.getUserService().isValidName(name)) {
                UIHelper.printErrorMessage("Invalid name. Name must be 2-50 characters long.");
                return;
            }

            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();
            if (!serviceFactory.getUserService().isValidPassword(password)) {
                UIHelper.printErrorMessage("Invalid password. Password must be at least 6 characters long.");
                return;
            }

            System.out.print("Enter Company Name: ");
            String company = scanner.nextLine().trim();
            if (!serviceFactory.getUserService().isValidCompanyName(company)) {
                UIHelper.printErrorMessage("Invalid company name. Company name must be 2-50 characters long.");
                return;
            }

            System.out.print("Enter Department: ");
            String department = scanner.nextLine().trim();
            if (!serviceFactory.getUserService().isValidDepartment(department)) {
                UIHelper.printErrorMessage("Invalid department. Department must be 2-50 characters long.");
                return;
            }

            System.out.print("Enter Position: ");
            String position = scanner.nextLine().trim();
            if (!serviceFactory.getUserService().isValidPosition(position)) {
                UIHelper.printErrorMessage("Invalid position. Position must be 2-50 characters long.");
                return;
            }

            System.out.print("Enter Email: ");
            String email = scanner.nextLine().trim();
            if (!serviceFactory.getUserService().isValidEmail(email)) {
                UIHelper.printErrorMessage("Invalid email format. Email must contain '@' and '.' and be 5-100 characters long.");
                return;
            }

            if (serviceFactory.getUserService().registerCompanyRep(userID, name, password, company, department, position, email)) {
                UIHelper.printSuccessMessage("Registration successful! Pending approval.");
            } else {
                UIHelper.printErrorMessage("Registration failed.");
            }
        } catch (Exception e) {
            UIHelper.printErrorMessage("Error during registration. Please try again.");
        }
    }

    private boolean isValidStudentUserId(String userId) {
        if (userId == null || userId.length() != 9) return false;
        if (!userId.startsWith("U")) return false;
        for (int i = 1; i <= 7; i++) {
            if (!Character.isDigit(userId.charAt(i))) return false;
        }
        char lastChar = userId.charAt(8);
        return Character.isLetter(lastChar) && Character.isUpperCase(lastChar);
    }
}