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
        if (userRepository.getUserById(userId) != null) return false;
        Student student = new Student(userId, name, password, yearOfStudy, major, gpa, internshipRepository, applicationRepository);
        userRepository.addUser(student);
        userRepository.saveUsers();
        return true;
    }

    public boolean registerStaff(String userId, String name, String password, String department) {
        if (userRepository.getUserById(userId) != null) return false;
        CareerCenterStaff staff = new CareerCenterStaff(userId, name, password, department, userRepository, internshipRepository, applicationRepository);
        userRepository.addUser(staff);
        userRepository.saveUsers();
        return true;
    }

    public boolean registerCompanyRep(String userId, String name, String password, String company, String department, String position, String email) {
        User existing = userRepository.getUserById(userId);
        // Allow username reuse if existing account is a rejected company rep
        if (existing != null) {
            if (existing instanceof CompanyRepresentative && ((CompanyRepresentative) existing).isRejected()) {
                // Remove rejected account to allow new registration with same ID
                userRepository.removeUser(userId);
                Database.removeUser(userId); // sync legacy Database
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
        if (user instanceof CompanyRepresentative) {
            ((CompanyRepresentative) user).setApproved(true);
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
        if (allowRejectedCompanyRep && existing instanceof CompanyRepresentative && ((CompanyRepresentative) existing).isRejected()) {
            return true;
        }
        return false;
    }
}