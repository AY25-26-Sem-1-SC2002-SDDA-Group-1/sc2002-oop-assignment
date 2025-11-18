import java.util.List;

public interface IApplicationRepository {
    List<Application> getAllApplications();
    Application getApplicationById(String applicationId);
    void addApplication(Application application);
    void saveApplications();
    String generateApplicationId();
}