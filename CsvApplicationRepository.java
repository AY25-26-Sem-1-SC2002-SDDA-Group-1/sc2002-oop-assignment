import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CsvApplicationRepository implements IApplicationRepository {
    private static final List<Application> applications = new ArrayList<>();
    private static int applicationCounter = 1;

    static {
        loadApplications();
    }

    private static void loadApplications() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("applications.csv"));
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    // Need to get student and internship from repositories, but for now, assume they exist
                    // This is tricky, need to refactor later
                    // For now, create dummy or skip
                }
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error loading applications: " + e.getMessage());
        }
    }

    @Override
    public List<Application> getAllApplications() {
        return new ArrayList<>(applications);
    }

    @Override
    public Application getApplicationById(String applicationId) {
        return applications.stream().filter(a -> a.getApplicationID().equals(applicationId)).findFirst().orElse(null);
    }

    @Override
    public void addApplication(Application application) {
        applications.add(application);
    }

    @Override
    public void saveApplications() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("applications.csv"));
            writer.println("ApplicationID,StudentID,InternshipID,Status,AppliedDate,ManuallyWithdrawn");
            for (Application app : applications) {
                writer.println(app.getApplicationID() + "," + app.getApplicant().getUserID() + "," + app.getOpportunity().getOpportunityID() + "," + app.getStatus() + "," + app.getAppliedDate() + "," + app.isManuallyWithdrawn());
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("Error saving applications: " + e.getMessage());
        }
    }

    @Override
    public String generateApplicationId() {
        return "APP" + String.format("%03d", applicationCounter++);
    }
}