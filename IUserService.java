/**
 * Interface for user management operations.
 */
public interface IUserService {
    User login(String userId, String password);
    boolean registerStudent(String userId, String name, String password, int yearOfStudy, String major, double gpa);
    boolean registerStaff(String userId, String name, String password, String department);
    boolean registerCompanyRep(String userId, String name, String password, String company, String department, String position, String email);
    void approveCompanyRep(String repId);
    void saveUsers() throws java.io.IOException;
    boolean isUserIdAvailable(String userId, boolean allowRejectedCompanyRep);
    IUserRepository getUserRepository();

    // Validation methods
    boolean isValidName(String name);
    boolean isValidPassword(String password);
    boolean isValidYearOfStudy(int year);
    boolean isValidGpa(double gpa);
    boolean isValidDepartment(String department);
    boolean isValidCompanyName(String company);
    boolean isValidPosition(String position);
    boolean isValidEmail(String email);
}