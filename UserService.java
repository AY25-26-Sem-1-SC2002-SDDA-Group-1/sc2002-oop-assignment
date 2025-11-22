import java.io.IOException;

/**
 * Service class for managing user operations like login and registration.
 */
public class UserService implements IUserService {
    private final IUserRepository userRepository;
    private final IInternshipRepository internshipRepository;
    private final IApplicationRepository applicationRepository;
    private ICompanyRepApplicationService applicationService;

    /**
     * Constructs a UserService.
     *
     * @param userRepository the user repository
     * @param internshipRepository the internship repository
     * @param applicationRepository the application repository
     */
    public UserService(IUserRepository userRepository, IInternshipRepository internshipRepository, IApplicationRepository applicationRepository) {
        this.userRepository = userRepository;
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
    }

    /**
     * Sets the company rep application service.
     *
     * @param applicationService the application service
     */
    public void setApplicationService(ICompanyRepApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     * Logs in a user with the given credentials.
     *
     * @param userId the user ID
     * @param password the password
     * @return the User if login successful, null otherwise
     */
    public User login(String userId, String password) {
        User user = userRepository.getUserById(userId);
        if (user != null && user.login(password)) {
            return user;
        }
        return null;
    }

    /**
     * Registers a new student.
     *
     * @param userId the user ID
     * @param name the name
     * @param password the password
     * @param yearOfStudy the year of study
     * @param major the major
     * @param gpa the GPA
     * @return true if registration successful
     */
    public boolean registerStudent(String userId, String name, String password, int yearOfStudy, String major, double gpa) {
        // Input validation
        if (!isValidUserId(userId)) return false;
        if (!isValidName(name)) return false;
        if (!isValidPassword(password)) return false;
        if (!isValidYearOfStudy(yearOfStudy)) return false;
        if (!isValidMajor(major)) return false;
        if (!isValidGpa(gpa)) return false;

        if (userRepository.getUserById(userId) != null) return false;
        Student student = new Student(userId, name, password, yearOfStudy, major, gpa, internshipRepository, applicationRepository);
        userRepository.addUser(student);
        try {
            userRepository.saveUsers();
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Registers a new career center staff member.
     *
     * @param userId the user ID
     * @param name the name
     * @param password the password
     * @param department the department
     * @return true if registration successful
     */
    public boolean registerStaff(String userId, String name, String password, String department) {
        // Input validation
        if (!isValidUserId(userId)) return false;
        if (!isValidName(name)) return false;
        if (!isValidPassword(password)) return false;
        if (!isValidDepartment(department)) return false;

        if (userRepository.getUserById(userId) != null) return false;
        CareerCenterStaff staff = new CareerCenterStaff(userId, name, password, department, userRepository, internshipRepository, applicationRepository);
        userRepository.addUser(staff);
        try {
            userRepository.saveUsers();
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Registers a new company representative.
     *
     * @param userId the user ID
     * @param name the name
     * @param password the password
     * @param company the company name
     * @param department the department
     * @param position the position
     * @param email the email
     * @return true if registration successful
     */
    public boolean registerCompanyRep(String userId, String name, String password, String company, String department, String position, String email) {
        // Input validation
        if (!isValidUserId(userId)) return false;
        if (!isValidName(name)) return false;
        if (!isValidPassword(password)) return false;
        if (!isValidCompanyName(company)) return false;
        if (!isValidDepartment(department)) return false;
        if (!isValidPosition(position)) return false;
        if (!isValidEmail(email)) return false;

        User existing = userRepository.getUserById(userId);
        // Allow username reuse if existing account is a rejected company rep
        if (existing != null) {
            if (existing.isCompanyRepresentative() && existing.asCompanyRepresentative().isRejected()) {
                // Remove rejected account to allow new registration with same ID
                userRepository.removeUser(userId);
            } else {
                // Active or pending account exists—block registration
                return false;
            }
        }
        CompanyRepresentative rep = new CompanyRepresentative(userId, name, password, company, department, position, email, internshipRepository, applicationRepository);
        rep.setApplicationService(applicationService);
        userRepository.addUser(rep);
        try {
            userRepository.saveUsers();
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Approves a company representative.
     *
     * @param repId the representative ID
     */
    public void approveCompanyRep(String repId) {
        User user = userRepository.getUserById(repId);
        if (user.isCompanyRepresentative()) {
            user.asCompanyRepresentative().setApproved(true);
            try {
                userRepository.saveUsers();
            } catch (IOException e) {
                System.err.println("Error saving users: " + e.getMessage());
            }
        }
    }

    /**
     * Gets the user repository.
     *
     * @return the user repository
     */
    public IUserRepository getUserRepository() {
        return userRepository;
    }

    /**
     * Checks if a user ID is available for registration.
     *
     * @param userId the user ID
     * @param allowRejectedCompanyRep whether to allow reuse of rejected company rep IDs
     * @return true if available
     */
    public boolean isUserIdAvailable(String userId, boolean allowRejectedCompanyRep) {
        User existing = userRepository.getUserById(userId);
        if (existing == null) return true;
        // For company rep registration, allow reuse of rejected usernames
        if (allowRejectedCompanyRep && existing.isCompanyRepresentative() && existing.asCompanyRepresentative().isRejected()) {
            return true;
        }
        return false;
    }

    /**
     * Save all user data to persistent storage
     */
    public void saveUsers() throws IOException {
        userRepository.saveUsers();
    }

    // Input validation methods
    private boolean isValidUserId(String userId) {
        return userId != null && !userId.trim().isEmpty() && userId.length() >= 3 && userId.length() <= 20;
    }

    public boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() >= 2 && name.length() <= 50;
    }

    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public boolean isValidYearOfStudy(int year) {
        return year >= 1 && year <= 4;
    }

    private boolean isValidMajor(String major) {
        if (major == null || major.trim().isEmpty()) return false;
        String[] validMajors = {
            "Computer Science", "Computer Engineering", "Data Science & AI",
            "Information Engineering & Media", "Biomedical Engineering"
        };
        for (String validMajor : validMajors) {
            if (validMajor.equalsIgnoreCase(major.trim())) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidGpa(double gpa) {
        return gpa >= 0.0 && gpa <= 5.0;
    }

    public boolean isValidDepartment(String department) {
        return department != null && !department.trim().isEmpty() && department.length() >= 2 && department.length() <= 50;
    }

    public boolean isValidCompanyName(String company) {
        return company != null && !company.trim().isEmpty() && company.length() >= 2 && company.length() <= 50;
    }

    public boolean isValidPosition(String position) {
        return position != null && !position.trim().isEmpty() && position.length() >= 2 && position.length() <= 50;
    }

    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        // Basic email validation
        return email.contains("@") && email.contains(".") && email.length() >= 5 && email.length() <= 100;
    }
}