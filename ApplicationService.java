import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationService {
    private final IApplicationRepository applicationRepository;
    private final IInternshipRepository internshipRepository;
    private final IUserRepository userRepository;

    public ApplicationService(IApplicationRepository applicationRepository, IInternshipRepository internshipRepository, IUserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.internshipRepository = internshipRepository;
        this.userRepository = userRepository;
    }

    public boolean applyForInternship(String studentId, String opportunityId) {
        User user = userRepository.getUserById(studentId);
        if (!(user instanceof Student)) return false;
        Student student = (Student) user;

        InternshipOpportunity opp = internshipRepository.getInternshipById(opportunityId);
        if (opp == null || !opp.isOpen() || !opp.isVisible() || !student.isEligibleForInternship(opp)) return false;

        // Check if already applied
        boolean alreadyApplied = applicationRepository.getAllApplications().stream()
            .anyMatch(a -> a.getApplicant().getUserID().equals(studentId) && a.getOpportunity().getOpportunityID().equals(opportunityId));
        if (alreadyApplied) return false;

        Application app = new Application(
            applicationRepository.generateApplicationId(),
            student,
            opp,
            "Pending"
        );
        applicationRepository.addApplication(app);
        applicationRepository.saveApplications();
        return true;
    }

    public void approveApplication(String applicationId) {
        Application app = applicationRepository.getApplicationById(applicationId);
        if (app != null) {
            app.updateStatus("Successful");
            applicationRepository.saveApplications();
        }
    }

    public void rejectApplication(String applicationId) {
        Application app = applicationRepository.getApplicationById(applicationId);
        if (app != null) {
            app.updateStatus("Unsuccessful");
            applicationRepository.saveApplications();
        }
    }

    public void acceptInternship(String applicationId) {
        Application app = applicationRepository.getApplicationById(applicationId);
        if (app != null && app.getStatus().equals("Successful")) {
            app.updateStatus("Confirmed");
            // Withdraw other applications
            String studentId = app.getApplicant().getUserID();
            List<Application> others = applicationRepository.getAllApplications().stream()
                .filter(a -> a.getApplicant().getUserID().equals(studentId) && !a.getApplicationID().equals(applicationId))
                .collect(Collectors.toList());
            for (Application other : others) {
                other.updateStatus("Withdrawn");
            }
            // Check if filled
            InternshipOpportunity opp = app.getOpportunity();
            long confirmed = applicationRepository.getAllApplications().stream()
                .filter(a -> a.getOpportunity().getOpportunityID().equals(opp.getOpportunityID()) && a.getStatus().equals("Confirmed"))
                .count();
            if (confirmed >= opp.getMaxSlots()) {
                opp.setStatus("Filled");
            }
            applicationRepository.saveApplications();
        }
    }

    public void requestWithdrawal(String applicationId) {
        Application app = applicationRepository.getApplicationById(applicationId);
        if (app != null) {
            app.updateStatus("Withdrawal Requested");
            applicationRepository.saveApplications();
        }
    }

    public void approveWithdrawal(String applicationId) {
        Application app = applicationRepository.getApplicationById(applicationId);
        if (app != null) {
            app.updateStatus("Withdrawn");
            // Handle queue if confirmed
            if (app.getStatus().equals("Confirmed")) {
                // Process queue - complex, skip for now
            }
            applicationRepository.saveApplications();
        }
    }

    public List<Application> getApplicationsForStudent(String studentId) {
        return applicationRepository.getAllApplications().stream()
            .filter(a -> a.getApplicant().getUserID().equals(studentId))
            .collect(Collectors.toList());
    }

    public List<Application> getApplicationsForCompanyRep(String repId) {
        return applicationRepository.getAllApplications().stream()
            .filter(a -> a.getOpportunity().getCreatedBy().getUserID().equals(repId))
            .collect(Collectors.toList());
    }

    public List<Application> getApplicationsForInternship(String opportunityId) {
        return applicationRepository.getAllApplications().stream()
            .filter(a -> a.getOpportunity().getOpportunityID().equals(opportunityId))
            .collect(Collectors.toList());
    }
}