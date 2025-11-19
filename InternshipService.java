import java.util.Date;
import java.util.List;

public class InternshipService {
    private final IInternshipRepository internshipRepository;
    private final IUserRepository userRepository;

    public InternshipService(IInternshipRepository internshipRepository, IUserRepository userRepository) {
        this.internshipRepository = internshipRepository;
        this.userRepository = userRepository;
    }

    public boolean createInternship(String userId, String title, String description, String level, String preferredMajor, Date openingDate, Date closingDate, int maxSlots, double minGPA) {
        User user = userRepository.getUserById(userId);
        if (!(user instanceof CompanyRepresentative)) return false;
        CompanyRepresentative rep = (CompanyRepresentative) user;
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

    public void approveInternship(String opportunityId) {
        InternshipOpportunity opp = internshipRepository.getInternshipById(opportunityId);
        if (opp != null) {
            opp.setStatus("Approved");
        }
    }

    public void rejectInternship(String opportunityId) {
        InternshipOpportunity opp = internshipRepository.getInternshipById(opportunityId);
        if (opp != null) {
            opp.setStatus("Rejected");
            internshipRepository.removeInternship(opportunityId);
        }
    }

    public List<InternshipOpportunity> getAllInternships() {
        return internshipRepository.getAllInternships();
    }

    public InternshipOpportunity getInternship(String id) {
        return internshipRepository.getInternshipById(id);
    }

    public void toggleVisibility(String opportunityId, boolean visible) {
        InternshipOpportunity opp = internshipRepository.getInternshipById(opportunityId);
        if (opp != null) {
            opp.setVisibility(visible);
        }
    }
}