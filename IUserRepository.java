import java.util.List;

public interface IUserRepository {
    List<User> getAllUsers();
    User getUserById(String userId);
    void addUser(User user);
    void saveUsers();
    String generateCompanyRepId();
}