import java.util.Date;
import java.util.List;

/**
 * Service class for managing internship opportunities.
 * Handles creation, approval, rejection, and visibility of internships.
 */
public class InternshipService implements IInternshipService {
    private final IInternshipRepository internshipRepository;
    private final IUserRepository userRepository;

    /**
     * Constructs an InternshipService.
     *
     * @param internshipRepository the internship repository
     * @param userRepository the user repository
     */
    public InternshipService(IInternshipRepository internshipRepository, IUserRepository userRepository) {
        this.internshipRepository = internshipRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new internship opportunity.
     *
     * @param userId the ID of the company representative creating the internship
     * @param title the title
     * @param description the description
     * @param level the level
     * @param preferredMajor the preferred major
     * @param openingDate the opening date
     * @param closingDate the closing date
     * @param maxSlots the maximum slots
     * @param minGPA the minimum GPA
     * @return true if created successfully
     */
    @Override
    public boolean createInternship(String userId, String title, String description, String level, String preferredMajor, Date openingDate, Date closingDate, int maxSlots, double minGPA) {
        User user = userRepository.getUserById(userId);
        if (!user.isCompanyRepresentative()) return false;
        CompanyRepresentative rep = user.asCompanyRepresentative();
        if (!rep.isApproved()) return false;

        // Check count
        long count = internshipRepository.getAllInternships().stream().filter(i -> i.getCreatedBy().getUserID().equals(userId)).count();
        if (count >= 5) return false;

        if (maxSlots < 1 || maxSlots > 10 || minGPA < 0 || minGPA > 5.0) return false;

        InternshipOpportunity opp = new InternshipOpportunity(
            internshipRepository.generateInternshipId(),
            title, description, level, preferredMajor, openingDate, closingDate, maxSlots, minGPA, rep
        );
        internshipRepository.addInternship(opp);
        return true;
    }

    /**
     * Approves an internship opportunity.
     *
     * @param opportunityId the opportunity ID
     */
    @Override
    public void approveInternship(String opportunityId) {
        InternshipOpportunity opp = internshipRepository.getInternshipById(opportunityId);
        if (opp != null) {
            opp.setStatus("Approved");
            opp.setVisibility(true);
            internshipRepository.saveInternships();
        }
    }

    /**
     * Rejects an internship opportunity.
     *
     * @param opportunityId the opportunity ID
     */
    @Override
    public void rejectInternship(String opportunityId) {
        InternshipOpportunity opp = internshipRepository.getInternshipById(opportunityId);
        if (opp != null) {
            opp.setStatus("Rejected");
            internshipRepository.removeInternship(opportunityId);
        }
    }

    /**
     * Deletes an internship opportunity.
     *
     * @param opportunityId the opportunity ID
     */
    @Override
    public void deleteInternship(String opportunityId) {
        internshipRepository.removeInternship(opportunityId);
    }

    /**
     * Gets all internships.
     *
     * @return list of all internships
     */
    @Override
    public List<InternshipOpportunity> getAllInternships() {
        return internshipRepository.getAllInternships();
    }

    /**
     * Gets an internship by ID.
     *
     * @param id the internship ID
     * @return the internship or null
     */
    @Override
    public InternshipOpportunity getInternship(String id) {
        return internshipRepository.getInternshipById(id);
    }

    /**
     * Toggles the visibility of an internship.
     *
     * @param opportunityId the opportunity ID
     * @param visible true to make visible
     */
    @Override
    public void toggleVisibility(String opportunityId, boolean visible) {
        InternshipOpportunity opp = internshipRepository.getInternshipById(opportunityId);
        if (opp != null) {
            opp.setVisibility(visible);
            internshipRepository.saveInternships();
        }
    }

    /**
     * Gets the internship repository.
     *
     * @return the repository
     */
    @Override
    public IInternshipRepository getInternshipRepository() {
        return internshipRepository;
    }
}