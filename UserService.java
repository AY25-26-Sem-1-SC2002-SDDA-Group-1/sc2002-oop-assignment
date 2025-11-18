public class UserService {
    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
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
        Student student = new Student(userId, name, password, yearOfStudy, major, gpa);
        userRepository.addUser(student);
        userRepository.saveUsers();
        return true;
    }

    public boolean registerStaff(String userId, String name, String password, String department) {
        if (userRepository.getUserById(userId) != null) return false;
        CareerCenterStaff staff = new CareerCenterStaff(userId, name, password, department);
        userRepository.addUser(staff);
        userRepository.saveUsers();
        return true;
    }

    public boolean registerCompanyRep(String userId, String name, String password, String company, String department, String position, String email) {
        if (userRepository.getUserById(userId) != null) return false;
        CompanyRepresentative rep = new CompanyRepresentative(userId, name, password, company, department, position, email);
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
}