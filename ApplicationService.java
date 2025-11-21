import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing internship applications.
 * Handles application submission, approval, rejection, and withdrawal processes.
 */
public class ApplicationService {
    private final IApplicationRepository applicationRepository;
    private final IInternshipRepository internshipRepository;
    private final IUserRepository userRepository;

    /**
     * Constructs an ApplicationService with the necessary repositories.
     *
     * @param applicationRepository the application repository
     * @param internshipRepository the internship repository
     * @param userRepository the user repository
     */
    public ApplicationService(IApplicationRepository applicationRepository, IInternshipRepository internshipRepository, IUserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.internshipRepository = internshipRepository;
        this.userRepository = userRepository;
    }

    /**
     * Allows a student to apply for an internship opportunity.
     *
     * @param studentId the ID of the student
     * @param opportunityId the ID of the internship opportunity
     * @return true if application was successful, false otherwise
     */
    public boolean applyForInternship(String studentId, String opportunityId) {
        User user = userRepository.getUserById(studentId);
        if (!user.isStudent()) return false;
        Student student = user.asStudent();

        InternshipOpportunity opp = internshipRepository.getInternshipById(opportunityId);
        if (opp == null || !opp.isOpen() || !opp.isVisible() || !student.isEligibleForInternship(opp)) return false;

        // Check if student already has a confirmed internship
        boolean hasConfirmed = applicationRepository.getAllApplications().stream()
            .anyMatch(a -> a.getApplicant().getUserID().equals(studentId) && "Confirmed".equals(a.getStatus()));
        if (hasConfirmed) return false;

        // Check if already applied
        boolean alreadyApplied = applicationRepository.getAllApplications().stream()
            .anyMatch(a -> a.getApplicant().getUserID().equals(studentId) && a.getOpportunity().getOpportunityID().equals(opportunityId));
        if (alreadyApplied) return false;

        // All applications are accepted initially
        long confirmed = applicationRepository.getAllApplications().stream()
            .filter(a -> a.getOpportunity().getOpportunityID().equals(opportunityId) && "Confirmed".equals(a.getStatus()))
            .count();

        String initialStatus = "Pending";

        Application app = new Application(
            applicationRepository.generateApplicationId(),
            student,
            opp,
            initialStatus
        );
        applicationRepository.addApplication(app);
        return true;
    }

    /**
     * Approves an application, changing its status to Accepted.
     *
     * @param applicationId the ID of the application
     */
    public void approveApplication(String applicationId) {
        Application app = applicationRepository.getApplicationById(applicationId);
        if (app != null) {
            app.updateStatus("Successful");
            applicationRepository.saveApplications();
        }
    }

    /**
     * Rejects an application, changing its status to Unsuccessful.
     *
     * @param applicationId the ID of the application
     */
    public void rejectApplication(String applicationId) {
        Application app = applicationRepository.getApplicationById(applicationId);
        if (app != null) {
            app.updateStatus("Unsuccessful");
            applicationRepository.saveApplications();
        }
    }

    /**
     * Accepts an internship offer, confirming the application and withdrawing others.
     *
     * @param applicationId the ID of the application
     */
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
                internshipRepository.saveInternships();
            }
            applicationRepository.saveApplications();
        }
    }

    /**
     * Requests withdrawal from an application.
     *
     * @param applicationId the ID of the application
     */
    public void requestWithdrawal(String applicationId) {
        Application app = applicationRepository.getApplicationById(applicationId);
        if (app != null) {
            app.updateStatus("Withdrawal Requested");
            applicationRepository.saveApplications();
        }
    }

    /**
     * Approves a withdrawal request, changing status to Withdrawn.
     *
     * @param applicationId the ID of the application
     */
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

    /**
     * Gets all applications for a specific student.
     *
     * @param studentId the ID of the student
     * @return list of applications
     */
    public java.util.List<Application> getAllApplicationsForStudent(String studentId) {
        return applicationRepository.getAllApplications().stream()
            .filter(a -> a.getApplicant().getUserID().equals(studentId))
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Gets all applications for a specific internship opportunity.
     *
     * @param opportunityId the ID of the opportunity
     * @return list of applications
     */
    public java.util.List<Application> getAllApplicationsForInternship(String opportunityId) {
        return applicationRepository.getAllApplications().stream()
            .filter(a -> a.getOpportunity().getOpportunityID().equals(opportunityId))
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Gets applications for a specific student.
     *
     * @param studentId the ID of the student
     * @return list of applications
     */
    public List<Application> getApplicationsForStudent(String studentId) {
        return applicationRepository.getAllApplications().stream()
            .filter(a -> a.getApplicant().getUserID().equals(studentId))
            .collect(Collectors.toList());
    }

    /**
     * Gets applications for internships created by a company representative.
     *
     * @param repId the ID of the company representative
     * @return list of applications
     */
    public List<Application> getApplicationsForCompanyRep(String repId) {
        return applicationRepository.getAllApplications().stream()
            .filter(a -> a.getOpportunity().getCreatedBy().getUserID().equals(repId))
            .collect(Collectors.toList());
    }

    /**
     * Gets applications for a specific internship opportunity.
     *
     * @param opportunityId the ID of the opportunity
     * @return list of applications
     */
    public List<Application> getApplicationsForInternship(String opportunityId) {
        return applicationRepository.getAllApplications().stream()
            .filter(a -> a.getOpportunity().getOpportunityID().equals(opportunityId))
            .collect(Collectors.toList());
    }

    /**
     * Gets the application repository.
     *
     * @return the application repository
     */
    public IApplicationRepository getApplicationRepository() {
        return applicationRepository;
    }
}