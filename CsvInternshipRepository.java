import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * CSV-based repository implementation for managing internship opportunities.
 * Loads and saves internship data to/from a CSV file.
 */
public class CsvInternshipRepository implements IInternshipRepository {
    private static final List<InternshipOpportunity> internships = new ArrayList<>();
    private static int internshipCounter = 1;
    private IUserRepository userRepository;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Constructs a CsvInternshipRepository.
     *
     * @param userRepository the user repository
     */
    public CsvInternshipRepository(IUserRepository userRepository) {
        this.userRepository = userRepository;
        loadInternships();
    }

    /**
     * Sets the user repository and loads internships if possible.
     *
     * @param userRepository the user repository
     */
    public void setUserRepository(IUserRepository userRepository) {
        this.userRepository = userRepository;
        if (internships.isEmpty()) {
            loadInternships();
        }
    }

    /**
     * Loads internships from the CSV file.
     */
    private void loadInternships() {
        try {
            File file = new File("data/internships.csv");
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
                        if (creator.isCompanyRepresentative()) {
                            InternshipOpportunity opp = new InternshipOpportunity(
                                oppId, title, description, level, preferredMajor,
                                openingDate, closingDate, maxSlots, minGPA,
                                creator.asCompanyRepresentative()
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

    /**
     * Gets all internships.
     *
     * @return list of all internships
     */
    @Override
    public List<InternshipOpportunity> getAllInternships() {
        return new ArrayList<>(internships);
    }

    /**
     * Gets an internship by ID.
     *
     * @param opportunityId the internship ID
     * @return the internship or null if not found
     */
    @Override
    public InternshipOpportunity getInternshipById(String opportunityId) {
        return internships.stream().filter(i -> i.getOpportunityID().equalsIgnoreCase(opportunityId)).findFirst().orElse(null);
    }

    /**
     * Adds a new internship.
     *
     * @param internship the internship to add
     */
    @Override
    public void addInternship(InternshipOpportunity internship) {
        internships.add(internship);
        saveInternships();
    }

    /**
     * Removes an internship by ID.
     *
     * @param opportunityId the internship ID
     */
    @Override
    public void removeInternship(String opportunityId) {
        internships.removeIf(i -> i.getOpportunityID().equalsIgnoreCase(opportunityId));
        saveInternships();
    }

    /**
     * Saves all internships to the CSV file.
     */
    @Override
    public void saveInternships() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("data/internships.csv"))) {
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

    /**
     * Generates a new unique internship ID.
     *
     * @return the generated ID
     */
    @Override
    public String generateInternshipId() {
        return "INT" + String.format("%03d", internshipCounter++);
    }
}