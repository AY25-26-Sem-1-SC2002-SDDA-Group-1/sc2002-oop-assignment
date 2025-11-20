import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CsvApplicationRepository implements IApplicationRepository {
    private static final List<Application> applications = new ArrayList<>();
    private static int applicationCounter = 1;
    private IUserRepository userRepository;
    private IInternshipRepository internshipRepository;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

    public CsvApplicationRepository(IUserRepository userRepository, IInternshipRepository internshipRepository) {
        this.userRepository = userRepository;
        this.internshipRepository = internshipRepository;
        loadApplications();
    }

    // Method to set user repository after construction and load data
    public void setUserRepository(IUserRepository userRepository) {
        this.userRepository = userRepository;
        if (internshipRepository != null && applications.isEmpty()) {
            loadApplications();
        }
    }

    // Method to set internship repository after construction
    public void setInternshipRepository(IInternshipRepository internshipRepository) {
        this.internshipRepository = internshipRepository;
        if (userRepository != null && applications.isEmpty()) {
            loadApplications();
        }
    }

    private void loadApplications() {
        try {
            File file = new File("data/applications.csv");
            if (!file.exists()) {
                return; // No file to load
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                String[] parts = line.split(",", -1); // Use -1 to preserve trailing empty fields
                if (parts.length >= 5) {
                    try {
                        String appId = parts[0].trim();
                        String studentId = parts[1].trim();
                        String opportunityId = parts[2].trim();
                        String status = parts[3].trim();
                        Date appliedDate = dateFormat.parse(parts[4].trim());
                        boolean manuallyWithdrawn = parts.length > 5 ? Boolean.parseBoolean(parts[5].trim()) : false;
                        String previousStatus = (parts.length > 6 && !parts[6].trim().isEmpty()) ? parts[6].trim() : null;
                        Date queuedDate = null;
                        if (parts.length > 7 && !parts[7].trim().isEmpty()) {
                            queuedDate = dateFormat.parse(parts[7].trim());
                        }
                        
                        User student = userRepository.getUserById(studentId);
                        InternshipOpportunity internship = internshipRepository.getInternshipById(opportunityId);
                        
                        if (student.isStudent() && internship != null) {
                            Application app = new Application(appId, student.asStudent(), internship, status, appliedDate);
                            app.setManuallyWithdrawn(manuallyWithdrawn);
                            app.setPreviousStatus(previousStatus);
                            app.setQueuedDate(queuedDate);
                            applications.add(app);
                            
                            // Update counter
                            int id = Integer.parseInt(appId.substring(3));
                            if (id >= applicationCounter) {
                                applicationCounter = id + 1;
                            }
                        }
                    } catch (ParseException | NumberFormatException e) {
                        System.err.println("Error parsing application line: " + line);
                    }
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
        saveApplications();
    }

    @Override
    public void saveApplications() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("data/applications.csv"))) {
            writer.println("ApplicationID,StudentID,OpportunityID,Status,AppliedDate,ManuallyWithdrawn,PreviousStatus,QueuedDate");
            for (Application app : applications) {
                // Format date consistently for parsing
                String formattedDate = app.getAppliedDate().toString();
                String prevStatus = (app.getPreviousStatus() != null) ? app.getPreviousStatus() : "";
                String queuedDateStr = (app.getQueuedDate() != null) ? app.getQueuedDate().toString() : "";
                writer.println(
                    app.getApplicationID() + "," +
                    app.getApplicant().getUserID() + "," +
                    app.getOpportunity().getOpportunityID() + "," +
                    app.getStatus() + "," +
                    formattedDate + "," +
                    app.isManuallyWithdrawn() + "," +
                    prevStatus + "," +
                    queuedDateStr
                );
            }
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error saving applications: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String generateApplicationId() {
        return "APP" + String.format("%03d", applicationCounter++);
    }
}