import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CsvInternshipRepository implements IInternshipRepository {
    private static final List<InternshipOpportunity> internships = new ArrayList<>();
    private static int internshipCounter = 1;
    private IUserRepository userRepository;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public CsvInternshipRepository(IUserRepository userRepository) {
        this.userRepository = userRepository;
        if (userRepository != null) {
            loadInternships();
        }
    }

    // Method to set user repository after construction and load data
    public void setUserRepository(IUserRepository userRepository) {
        this.userRepository = userRepository;
        if (userRepository != null && internships.isEmpty()) {
            loadInternships();
        }
    }

    private void loadInternships() {
        try {
            File file = new File("internships.csv");
            if (!file.exists()) {
                return; // No file to load
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                String[] parts = line.split(",");
                if (parts.length >= 11) {
                    try {
                        String oppId = parts[0].trim();
                        String title = parts[1].trim();
                        String description = parts[2].trim();
                        String level = parts[3].trim();
                        String preferredMajor = parts[4].trim();
                        Date openingDate = dateFormat.parse(parts[5].trim());
                        Date closingDate = dateFormat.parse(parts[6].trim());
                        String status = parts[7].trim();
                        int maxSlots = Integer.parseInt(parts[8].trim());
                        boolean visibility = Boolean.parseBoolean(parts[9].trim());
                        double minGPA = Double.parseDouble(parts[10].trim());
                        String creatorId = parts[11].trim();
                        
                        User creator = userRepository.getUserById(creatorId);
                        if (creator instanceof CompanyRepresentative) {
                            InternshipOpportunity opp = new InternshipOpportunity(
                                oppId, title, description, level, preferredMajor,
                                openingDate, closingDate, maxSlots, minGPA,
                                (CompanyRepresentative) creator
                            );
                            opp.setStatus(status);
                            opp.setVisibility(visibility);
                            internships.add(opp);
                            
                            // Update counter
                            int id = Integer.parseInt(oppId.substring(3));
                            if (id >= internshipCounter) {
                                internshipCounter = id + 1;
                            }
                        }
                    } catch (ParseException | NumberFormatException e) {
                        System.err.println("Error parsing internship line: " + line);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error loading internships: " + e.getMessage());
        }
    }

    @Override
    public List<InternshipOpportunity> getAllInternships() {
        return new ArrayList<>(internships);
    }

    @Override
    public InternshipOpportunity getInternshipById(String opportunityId) {
        return internships.stream().filter(i -> i.getOpportunityID().equals(opportunityId)).findFirst().orElse(null);
    }

    @Override
    public void addInternship(InternshipOpportunity internship) {
        internships.add(internship);
        saveInternships();
    }

    @Override
    public void removeInternship(String opportunityId) {
        internships.removeIf(i -> i.getOpportunityID().equals(opportunityId));
        saveInternships();
    }

    @Override
    public void saveInternships() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("internships.csv"))) {
            writer.println("InternshipID,Title,Description,Level,PreferredMajor,OpeningDate,ClosingDate,Status,MaxSlots,Visibility,MinGPA,CreatedBy");
            for (InternshipOpportunity opp : internships) {
                writer.println(
                    opp.getOpportunityID() + "," +
                    opp.getTitle() + "," +
                    opp.getDescription() + "," +
                    opp.getLevel() + "," +
                    opp.getPreferredMajor() + "," +
                    dateFormat.format(opp.getOpeningDate()) + "," +
                    dateFormat.format(opp.getClosingDate()) + "," +
                    opp.getStatus() + "," +
                    opp.getMaxSlots() + "," +
                    opp.isVisibility() + "," +
                    opp.getMinGPA() + "," +
                    opp.getCreatedBy().getUserID()
                );
            }
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error saving internships: " + e.getMessage());
        }
    }

    @Override
    public String generateInternshipId() {
        return "INT" + String.format("%03d", internshipCounter++);
    }
}