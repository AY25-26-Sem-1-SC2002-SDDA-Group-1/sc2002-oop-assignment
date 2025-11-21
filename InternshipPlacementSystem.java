import java.util.Scanner;

/**
 * Main system class for the internship placement application.
 * Manages user authentication, registration, and menu navigation.
 */
public class InternshipPlacementSystem {
    private final Scanner scanner = new Scanner(System.in);
    private User currentUser = null;
    private IUserRepository userRepository;
    private IInternshipRepository internshipRepository;
    private IApplicationRepository applicationRepository;

    private UserService userService;
    private InternshipService internshipService;
    private ApplicationService applicationService;

    /**
     * Constructs the InternshipPlacementSystem and initializes repositories and services.
     */
    public InternshipPlacementSystem() {
        // Initialize repositories with proper dependency injection
        initializeRepositories();
        initializeServices();
    }

    /**
     * Initializes the repositories with proper dependency injection.
     */
    private void initializeRepositories() {
        // Initialize repositories in dependency order to avoid circular dependencies

        // 1. Create user repository first (it can load users without dependencies)
        this.userRepository = new CsvUserRepository(null, null);

        // 2. Create internship repository with user repository
        this.internshipRepository = new CsvInternshipRepository(userRepository);

        // 3. Create application repository with both dependencies
        this.applicationRepository = new CsvApplicationRepository(userRepository, internshipRepository);

        // 4. Update user repository with the other repositories (for consistency)
        ((CsvUserRepository) this.userRepository).setInternshipRepository(internshipRepository);
        ((CsvUserRepository) this.userRepository).setApplicationRepository(applicationRepository);
    }

    /**
     * Initializes the services with the repositories.
     */
    private void initializeServices() {
        this.userService = new UserService(userRepository, internshipRepository, applicationRepository);
        this.internshipService = new InternshipService(internshipRepository, userRepository);
        this.applicationService = new ApplicationService(applicationRepository, internshipRepository, userRepository);
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
        return currentUser.createMenuHandler(internshipService, applicationService, userService, scanner);
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
                    userRepository.saveUsers();
                    applicationRepository.saveApplications();
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

            User user = userService.login(userID, password);
            if (user == null) {
                System.out.println("Invalid user ID or password.");
                return;
            }

            // Check if company representative is approved
            if (user.isCompanyRepresentative()) {
                CompanyRepresentative rep = user.asCompanyRepresentative();
                if (rep.isRejected()) {
                    System.out.println("Your account is rejected.");
                    return;
                }
                if (!rep.isApproved()) {
                    System.out.println("Your account is pending approval.");
                    return;
                }
            }

            currentUser = user;
            System.out.println("Login successful! Welcome, " + user.getName() + "!");
        } catch (Exception e) {
            System.out.println("Error during login. Please try again.");
        }
    }

    private void registerStudent() {
        try {
            UIHelper.printSectionHeader("STUDENT REGISTRATION");
            System.out.print("Enter User ID: ");
            String userID = scanner.nextLine().trim();

            // Validate User ID format for students
            if (!isValidStudentUserId(userID)) {
                System.out.println("Invalid User ID format. Student IDs must start with 'U', followed by 7 digits, and end with a letter (e.g., U2345123F).");
                return;
            }

            // Check for duplicate User ID immediately
            if (!userService.isUserIdAvailable(userID, false)) {
                System.out.println("User ID already exists. Registration cancelled.");
                return;
            }
            
            System.out.print("Enter Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();
            System.out.print("Enter Year of Study (1-4): ");
            int yearOfStudy = Integer.parseInt(scanner.nextLine().trim());
            System.out.println("Select Major:");
            System.out.println("1. Computer Science");
            System.out.println("2. Computer Engineering");
            System.out.println("3. Data Science & AI");
            System.out.println("4. Information Engineering & Media");
            System.out.println("5. Biomedical Engineering");
            System.out.print("Enter number or Major: ");
            String majorInput = scanner.nextLine().trim();
            String major = null;
            try {
                int num = Integer.parseInt(majorInput);
                switch (num) {
                    case 1: major = "Computer Science"; break;
                    case 2: major = "Computer Engineering"; break;
                    case 3: major = "Data Science & AI"; break;
                    case 4: major = "Information Engineering & Media"; break;
                    case 5: major = "Biomedical Engineering"; break;
                    default: major = null;
                }
            } catch (NumberFormatException e) {
                if (majorInput.equals("Computer Science") || majorInput.equals("Computer Engineering") || majorInput.equals("Data Science & AI") || majorInput.equals("Information Engineering & Media") || majorInput.equals("Biomedical Engineering")) {
                    major = majorInput;
                }
            }
            if (major == null) {
                System.out.println("Invalid major. Registration cancelled.");
                return;
            }
            System.out.print("Enter GPA (0.0-5.0): ");
            double gpa = Double.parseDouble(scanner.nextLine().trim());

            if (userService.registerStudent(userID, name, password, yearOfStudy, major, gpa)) {
                System.out.println("Registration successful!");
            } else {
                System.out.println("Registration failed.");
            }
        } catch (Exception e) {
            System.out.println("Error during registration. Please try again.");
        }
    }

    private void registerStaff() {
        try {
            UIHelper.printSectionHeader("CAREER CENTER STAFF REGISTRATION");
            System.out.print("Enter User ID: ");
            String userID = scanner.nextLine().trim();
            
            // Check for duplicate User ID immediately
            if (!userService.isUserIdAvailable(userID, false)) {
                System.out.println("User ID already exists. Registration cancelled.");
                return;
            }
            
            System.out.print("Enter Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();
            System.out.print("Enter Staff Department: ");
            String department = scanner.nextLine().trim();

            if (userService.registerStaff(userID, name, password, department)) {
                System.out.println("Registration successful!");
            } else {
                System.out.println("Registration failed.");
            }
        } catch (Exception e) {
            System.out.println("Error during registration. Please try again.");
        }
    }

    private void registerCompanyRep() {
        try {
            UIHelper.printSectionHeader("COMPANY REPRESENTATIVE REGISTRATION");
            System.out.print("Enter User ID: ");
            String userID = scanner.nextLine().trim();
            
            // Check for duplicate User ID immediately (allows rejected usernames to be reused)
            if (!userService.isUserIdAvailable(userID, true)) {
                System.out.println("User ID already exists. Registration cancelled.");
                return;
            }
            
            System.out.print("Enter Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();
            System.out.print("Enter Company Name: ");
            String company = scanner.nextLine().trim();
            System.out.print("Enter Department: ");
            String department = scanner.nextLine().trim();
            System.out.print("Enter Position: ");
            String position = scanner.nextLine().trim();
            System.out.print("Enter Email: ");
            String email = scanner.nextLine().trim();

            if (userService.registerCompanyRep(userID, name, password, company, department, position, email)) {
                System.out.println("Registration successful! Pending approval.");
            } else {
                System.out.println("Registration failed.");
            }
        } catch (Exception e) {
            System.out.println("Error during registration. Please try again.");
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