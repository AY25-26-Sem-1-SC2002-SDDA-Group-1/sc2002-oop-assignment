public class UserService {
    private final IUserRepository userRepository;
    private final IInternshipRepository internshipRepository;
    private final IApplicationRepository applicationRepository;

    public UserService(IUserRepository userRepository, IInternshipRepository internshipRepository, IApplicationRepository applicationRepository) {
        this.userRepository = userRepository;
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
    }

    public User login(String userId, String password) {
        User user = userRepository.getUserById(userId);
        if (user != null && user.login(password)) {
            return user;
        }
        return null;
    }

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
        userRepository.saveUsers();
        return true;
    }

    public boolean registerStaff(String userId, String name, String password, String department) {
        // Input validation
        if (!isValidUserId(userId)) return false;
        if (!isValidName(name)) return false;
        if (!isValidPassword(password)) return false;
        if (!isValidDepartment(department)) return false;

        if (userRepository.getUserById(userId) != null) return false;
        CareerCenterStaff staff = new CareerCenterStaff(userId, name, password, department, userRepository, internshipRepository, applicationRepository);
        userRepository.addUser(staff);
        userRepository.saveUsers();
        return true;
    }

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
        userRepository.addUser(rep);
        userRepository.saveUsers();
        return true;
    }

    public void approveCompanyRep(String repId) {
        User user = userRepository.getUserById(repId);
        if (user.isCompanyRepresentative()) {
            user.asCompanyRepresentative().setApproved(true);
            userRepository.saveUsers();
        }
    }

    public IUserRepository getUserRepository() {
        return userRepository;
    }

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
    public void saveUsers() {
        userRepository.saveUsers();
    }

    // Input validation methods
    private boolean isValidUserId(String userId) {
        return userId != null && !userId.trim().isEmpty() && userId.length() >= 3 && userId.length() <= 20;
    }

    private boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() >= 2 && name.length() <= 50;
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    private boolean isValidYearOfStudy(int year) {
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

    private boolean isValidGpa(double gpa) {
        return gpa >= 0.0 && gpa <= 5.0;
    }

    private boolean isValidDepartment(String department) {
        return department != null && !department.trim().isEmpty() && department.length() >= 2 && department.length() <= 50;
    }

    private boolean isValidCompanyName(String company) {
        return company != null && !company.trim().isEmpty() && company.length() >= 2 && company.length() <= 50;
    }

    private boolean isValidPosition(String position) {
        return position != null && !position.trim().isEmpty() && position.length() >= 2 && position.length() <= 50;
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        // Basic email validation
        return email.contains("@") && email.contains(".") && email.length() >= 5 && email.length() <= 100;
    }
}