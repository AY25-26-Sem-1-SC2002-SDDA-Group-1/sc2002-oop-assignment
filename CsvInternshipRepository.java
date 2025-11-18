import java.util.ArrayList;
import java.util.List;

public class CsvInternshipRepository implements IInternshipRepository {
    private static final List<InternshipOpportunity> internships = new ArrayList<>();
    private static int internshipCounter = 1;

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
    }

    @Override
    public void removeInternship(String opportunityId) {
        internships.removeIf(i -> i.getOpportunityID().equals(opportunityId));
    }

    @Override
    public void saveInternships() {
        // Internships are in memory, no save needed for now
    }

    @Override
    public String generateInternshipId() {
        return "INT" + String.format("%03d", internshipCounter++);
    }
}