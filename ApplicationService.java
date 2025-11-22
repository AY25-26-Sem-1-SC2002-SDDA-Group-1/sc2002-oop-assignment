import java.util.Date;
import java.util.List;

/**
 * Service class for managing internship applications.
 * Handles application submission, approval, rejection, and withdrawal processes.
 */
public class ApplicationService implements IApplicationService, IStudentApplicationService, IStaffApplicationService, ICompanyRepApplicationService {
    private static final int MAX_ACTIVE_APPLICATIONS = 3;
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
     * Checks if the internship is already full (filled slots >= max slots).
     * Filled slots include Confirmed, Successful, and Withdrawal Requested applications.
     *
     * @param studentId the ID of the student
     * @param opportunityId the ID of the internship opportunity
     * @return structured result describing success or failure
     */
    @Override
    public OperationResult applyForInternship(String studentId, String opportunityId) {
        User user = userRepository.getUserById(studentId);
        if (user == null || !user.isStudent()) {
            return OperationResult.failure("Only authenticated students can apply for internships.");
        }
        Student student = user.asStudent();

        InternshipOpportunity opp = internshipRepository.getInternshipById(opportunityId);
        if (opp == null) {
            return OperationResult.failure("Internship not found.");
        }
        if (!opp.isOpen()) {
            return OperationResult.failure("Internship is not open for applications.");
        }
        if (!opp.isVisible()) {
            return OperationResult.failure("Internship is currently hidden from students.");
        }
        if (!student.isEligibleForInternship(opp)) {
            return OperationResult.failure(student.getIneligibilityReason(opp));
        }

        int activeCount = getActiveApplicationCount(studentId);
        if (activeCount >= MAX_ACTIVE_APPLICATIONS) {
            return OperationResult.failure("You already have " + activeCount + " active applications (max " + MAX_ACTIVE_APPLICATIONS + ").");
        }

        boolean hasConfirmed = applicationRepository.getAllApplications().stream()
            .anyMatch(a -> a.getApplicant().getUserID().equals(studentId) && a.getStatusEnum() == ApplicationStatus.CONFIRMED);
        if (hasConfirmed) {
            return OperationResult.failure("Student already has a confirmed internship.");
        }

        boolean alreadyApplied = applicationRepository.getAllApplications().stream()
            .anyMatch(a -> a.getApplicant().getUserID().equals(studentId) && a.getOpportunity().getOpportunityID().equals(opportunityId));
        if (alreadyApplied) {
            return OperationResult.failure("Student has already applied to this internship.");
        }

        long filled = applicationRepository.getAllApplications().stream()
            .filter(a -> a.getOpportunity().getOpportunityID().equals(opportunityId))
            .filter(a -> a.getStatusEnum() == ApplicationStatus.CONFIRMED ||
                         a.getStatusEnum() == ApplicationStatus.SUCCESSFUL ||
                         a.getStatusEnum() == ApplicationStatus.WITHDRAWAL_REQUESTED)
            .count();
        if (filled >= opp.getMaxSlots()) {
            return OperationResult.failure("Internship is already full.");
        }

        Application app = new Application(
            applicationRepository.generateApplicationId(),
            student,
            opp,
            ApplicationStatus.PENDING
        );
        applicationRepository.addApplication(app);
        applicationRepository.saveApplications();
        return OperationResult.success("Application submitted successfully.");
    }

    /**
     * Approves an application, changing its status to Accepted.
     *
     * @param applicationId the ID of the application
     */
    @Override
    public void approveApplication(String applicationId) {
        Application app = applicationRepository.getApplicationById(applicationId);
        if (app != null) {
            app.updateStatus(ApplicationStatus.SUCCESSFUL);
            applicationRepository.saveApplications();
        }
    }

    /**
     * Rejects an application, changing its status to Unsuccessful.
     *
     * @param applicationId the ID of the application
     */
    @Override
    public void rejectApplication(String applicationId) {
        Application app = applicationRepository.getApplicationById(applicationId);
        if (app != null) {
            app.updateStatus(ApplicationStatus.UNSUCCESSFUL);
            applicationRepository.saveApplications();
        }
    }

    /**
     * Accepts an internship offer, confirming the application and withdrawing others.
     *
     * @param studentId the student requesting acceptance
     * @param applicationId the ID of the application
     * @return result describing success or failure
     */
    @Override
    public OperationResult acceptInternship(String studentId, String applicationId) {
        Application application = applicationRepository.getApplicationById(applicationId);
        String validationError = getAcceptValidationError(application, studentId);
        if (validationError != null) {
            return OperationResult.failure(validationError);
        }

        InternshipOpportunity opportunity = application.getOpportunity();
        List<Application> studentApps = getApplicationsForStudent(studentId);

        String overlapError = getOverlapError(studentApps, applicationId, opportunity);
        if (overlapError != null) {
            return OperationResult.failure(overlapError);
        }

        if (isInternshipFull(opportunity, applicationId)) {
            return OperationResult.failure("Cannot accept: internship is already full.");
        }

        application.updateStatus(ApplicationStatus.CONFIRMED);
        int withdrawnCount = withdrawOverlappingApplications(studentApps, applicationId, opportunity);
        updateOpportunityStatusIfFilled(opportunity);

        applicationRepository.saveApplications();
        internshipRepository.saveInternships();

        String message = withdrawnCount > 0
            ? "Internship accepted. " + withdrawnCount + " overlapping application(s) withdrawn."
            : "Internship accepted successfully.";
        return OperationResult.success(message);
    }

    /**
     * Requests withdrawal from an application.
     *
     * @param applicationId the ID of the application
     */
    @Override
    public OperationResult requestWithdrawal(String studentId, String applicationId) {
        Application app = applicationRepository.getApplicationById(applicationId);
        String validationError = getWithdrawalValidationError(app, studentId);
        if (validationError != null) {
            return OperationResult.failure(validationError);
        }

        app.updateStatus(ApplicationStatus.WITHDRAWAL_REQUESTED);
        app.setManuallyWithdrawn(true);
        applicationRepository.saveApplications();
        return OperationResult.success("Withdrawal request submitted successfully.");
    }

    /**
     * Approves a withdrawal request, changing status to Withdrawn.
     *
     * @param applicationId the ID of the application
     */
    @Override
    public void approveWithdrawal(String applicationId) {
        Application app = applicationRepository.getApplicationById(applicationId);
        if (app != null) {
            boolean wasConfirmed = app.getStatusEnum() == ApplicationStatus.CONFIRMED;
            app.updateStatus(ApplicationStatus.WITHDRAWN);
            if (wasConfirmed) {
                // Queue processing for confirmed replacements is not supported yet
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
    @Override
    public java.util.List<Application> getAllApplicationsForStudent(String studentId) {
        return applicationRepository.getAllApplications().stream()
            .filter(a -> a.getApplicant().getUserID().equals(studentId))
            .toList();
    }

    /**
     * Gets all applications for a specific internship opportunity.
     *
     * @param opportunityId the ID of the opportunity
     * @return list of applications
     */
    @Override
    public java.util.List<Application> getAllApplicationsForInternship(String opportunityId) {
        return applicationRepository.getAllApplications().stream()
            .filter(a -> a.getOpportunity().getOpportunityID().equals(opportunityId))
            .toList();
    }

    /**
     * Gets applications for a specific student.
     *
     * @param studentId the ID of the student
     * @return list of applications
     */
    @Override
    public List<Application> getApplicationsForStudent(String studentId) {
        return applicationRepository.getAllApplications().stream()
            .filter(a -> a.getApplicant().getUserID().equals(studentId))
            .toList();
    }

    /**
     * Gets applications for internships created by a company representative.
     *
     * @param repId the ID of the company representative
     * @return list of applications
     */
    @Override
    public List<Application> getApplicationsForCompanyRep(String repId) {
        return applicationRepository.getAllApplications().stream()
            .filter(a -> a.getOpportunity().getCreatedBy().getUserID().equals(repId))
            .toList();
    }

    /**
     * Gets applications for a specific internship opportunity created by the company representative.
     *
     * @param repId the company representative ID
     * @param opportunityId the opportunity ID
     * @return list of applications
     */
    @Override
    public List<Application> getApplicationsForCompanyRepOpportunity(String repId, String opportunityId) {
        return applicationRepository.getAllApplications().stream()
            .filter(a -> a.getOpportunity().getCreatedBy().getUserID().equals(repId) &&
                       a.getOpportunity().getOpportunityID().equals(opportunityId))
            .toList();
    }

    /**
     * Processes an internship application (approve or reject).
     *
     * @param repId the company representative ID
     * @param applicationId the application ID to process
     * @param approve true to approve, false to reject
     * @return true if processed successfully
     */
    @Override
    public boolean processApplication(String repId, String applicationId, boolean approve) {
        Application target = applicationRepository.getApplicationById(applicationId);
        if (target != null &&
            target.getOpportunity().getCreatedBy().getUserID().equals(repId) &&
            target.getStatusEnum() == ApplicationStatus.PENDING) {

            if (approve) {
                // Check slot limit before approving
                InternshipOpportunity opp = target.getOpportunity();
                long filledSlots = applicationRepository.getAllApplications().stream()
                    .filter(a -> a.getOpportunity().getOpportunityID().equals(opp.getOpportunityID()) &&
                           (a.getStatusEnum() == ApplicationStatus.SUCCESSFUL || a.getStatusEnum() == ApplicationStatus.CONFIRMED))
                    .count();
                if (filledSlots >= opp.getMaxSlots()) {
                    return false; // Cannot approve - slots are full
                }
                target.updateStatus(ApplicationStatus.SUCCESSFUL);
            } else {
                target.updateStatus(ApplicationStatus.UNSUCCESSFUL);
            }

            applicationRepository.saveApplications();
            internshipRepository.saveInternships();
            return true;
        }
        return false;
    }

    /**
     * Gets applications for a specific internship opportunity.
     *
     * @param opportunityId the ID of the opportunity
     * @return list of applications
     */
    @Override
    public List<Application> getApplicationsForInternship(String opportunityId) {
        return applicationRepository.getAllApplications().stream()
            .filter(a -> a.getOpportunity().getOpportunityID().equals(opportunityId))
            .toList();
    }

    /**
     * Gets the application repository.
     *
     * @return the application repository
     */
    @Override
    public IApplicationRepository getApplicationRepository() {
        return applicationRepository;
    }

    private String getAcceptValidationError(Application application, String studentId) {
        if (application == null) {
            return "Application not found.";
        }
        if (!application.getApplicant().getUserID().equals(studentId)) {
            return "This application does not belong to the requesting student.";
        }
        if (application.getStatusEnum() != ApplicationStatus.SUCCESSFUL) {
            return "Only successful applications can be accepted. Current status: " + application.getStatus();
        }
        return null;
    }

    private String getWithdrawalValidationError(Application application, String studentId) {
        if (application == null) {
            return "Application not found.";
        }
        if (!application.getApplicant().getUserID().equals(studentId)) {
            return "This application does not belong to the requesting student.";
        }
        ApplicationStatus currentStatus = application.getStatusEnum();
        boolean withdrawable = currentStatus == ApplicationStatus.PENDING ||
                               currentStatus == ApplicationStatus.SUCCESSFUL ||
                               currentStatus == ApplicationStatus.CONFIRMED;
        if (!withdrawable) {
            return "Cannot request withdrawal for application with status: " + currentStatus;
        }
        return null;
    }

    private String getOverlapError(List<Application> studentApps, String targetApplicationId, InternshipOpportunity opportunity) {
        boolean hasOverlap = studentApps.stream()
            .filter(app -> !app.getApplicationID().equals(targetApplicationId))
            .filter(app -> app.getStatusEnum() == ApplicationStatus.CONFIRMED)
            .anyMatch(app -> datesOverlap(opportunity, app.getOpportunity()));
        return hasOverlap ? "Cannot accept: overlaps with another confirmed internship." : null;
    }

    private boolean isInternshipFull(InternshipOpportunity opportunity, String targetApplicationId) {
        long filledCount = applicationRepository.getAllApplications().stream()
            .filter(app -> app.getOpportunity().getOpportunityID().equals(opportunity.getOpportunityID()))
            .filter(app -> !app.getApplicationID().equals(targetApplicationId))
            .filter(app -> app.getStatusEnum() == ApplicationStatus.CONFIRMED ||
                           app.getStatusEnum() == ApplicationStatus.SUCCESSFUL ||
                           app.getStatusEnum() == ApplicationStatus.WITHDRAWAL_REQUESTED)
            .count();
        return filledCount >= opportunity.getMaxSlots();
    }

    private int withdrawOverlappingApplications(List<Application> studentApps, String acceptedApplicationId, InternshipOpportunity acceptedOpportunity) {
        int withdrawnCount = 0;
        for (Application app : studentApps) {
            if (shouldWithdraw(app, acceptedApplicationId, acceptedOpportunity)) {
                app.updateStatus(ApplicationStatus.WITHDRAWN);
                withdrawnCount++;
            }
        }
        return withdrawnCount;
    }

    private boolean shouldWithdraw(Application app, String acceptedApplicationId, InternshipOpportunity acceptedOpportunity) {
        if (app.getApplicationID().equals(acceptedApplicationId)) {
            return false;
        }
        ApplicationStatus status = app.getStatusEnum();
        if (status == ApplicationStatus.WITHDRAWN || status == ApplicationStatus.CONFIRMED) {
            return false;
        }
        return datesOverlap(acceptedOpportunity, app.getOpportunity());
    }

    private void updateOpportunityStatusIfFilled(InternshipOpportunity opportunity) {
        long confirmedCount = applicationRepository.getAllApplications().stream()
            .filter(app -> app.getOpportunity().getOpportunityID().equals(opportunity.getOpportunityID()))
            .filter(app -> app.getStatusEnum() == ApplicationStatus.CONFIRMED)
            .count();
        if (confirmedCount >= opportunity.getMaxSlots()) {
            opportunity.setStatus(InternshipOpportunity.STATUS_FILLED);
        }
    }

    private boolean datesOverlap(InternshipOpportunity first, InternshipOpportunity second) {
        Date start1 = first.getOpeningDate();
        Date end1 = first.getClosingDate();
        Date start2 = second.getOpeningDate();
        Date end2 = second.getClosingDate();
        return !start1.after(end2) && !start2.after(end1);
    }

    private int getActiveApplicationCount(String studentId) {
        return (int) applicationRepository.getAllApplications().stream()
            .filter(app -> app.getApplicant().getUserID().equals(studentId))
            .filter(app -> app.getStatusEnum() != ApplicationStatus.WITHDRAWN)
            .filter(app -> app.getStatusEnum() != ApplicationStatus.UNSUCCESSFUL)
            .count();
    }

    @Override
    public List<Application> getAllApplications() {
        return applicationRepository.getAllApplications();
    }

    @Override
    public List<InternshipOpportunity> getEligibleInternshipsForStudent(String studentId) {
        User user = userRepository.getUserById(studentId);
        if (user == null || !user.isStudent()) {
            return List.of();
        }
        Student student = user.asStudent();
        return internshipRepository.getAllInternships().stream()
            .filter(i -> i.isVisible() && i.getStatus().equals(InternshipOpportunity.STATUS_APPROVED) && student.isEligibleForInternship(i))
            .toList();
    }
}