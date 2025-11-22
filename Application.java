import java.util.Date;

/**
 * Represents an application submitted by a student for an internship opportunity.
 * Contains details such as application ID, applicant, opportunity, status, and dates.
 */
public class Application {

    private final String applicationID;
    private final Student applicant;
    private final InternshipOpportunity opportunity;
    private ApplicationStatus status;
    private final Date appliedDate;
    private boolean manuallyWithdrawn;
    private ApplicationStatus previousStatus; // stores status before a withdrawal request

    /**
     * Constructs an Application with the current date as applied date.
     *
     * @param applicationID the unique ID of the application
     * @param applicant the student applying
     * @param opportunity the internship opportunity
     * @param status the current status of the application
     */
    public Application(String applicationID, Student applicant, InternshipOpportunity opportunity, ApplicationStatus status) {
        this.applicationID = applicationID;
        this.applicant = applicant;
        this.opportunity = opportunity;
        this.status = status;
        this.appliedDate = new Date();
        this.manuallyWithdrawn = false;
        this.previousStatus = null;
    }

    /**
     * Constructs an Application with a specified applied date.
     *
     * @param applicationID the unique ID of the application
     * @param applicant the student applying
     * @param opportunity the internship opportunity
     * @param status the current status of the application
     * @param appliedDate the date the application was submitted
     */
    public Application(String applicationID, Student applicant, InternshipOpportunity opportunity, ApplicationStatus status, Date appliedDate) {
        this.applicationID = applicationID;
        this.applicant = applicant;
        this.opportunity = opportunity;
        this.status = status;
        this.appliedDate = appliedDate;
        this.manuallyWithdrawn = false;
        this.previousStatus = null;
    }

    /**
     * Updates the status of the application, handling withdrawal logic.
     *
     * @param newStatus the new status to set
     */
    public void updateStatus(ApplicationStatus newStatus) {
        if (newStatus == ApplicationStatus.WITHDRAWAL_REQUESTED && status != ApplicationStatus.WITHDRAWAL_REQUESTED) {
            this.previousStatus = this.status;
        }
        if (newStatus == ApplicationStatus.WITHDRAWAL_REJECTED) {
            if (this.previousStatus != null) {
                this.status = this.previousStatus;
            } else {
                this.status = ApplicationStatus.SUCCESSFUL;
            }
            this.manuallyWithdrawn = false;
            this.previousStatus = null;
            return;
        }
        this.status = newStatus;
    }

    /**
     * Gets the application ID.
     *
     * @return the application ID
     */
    public String getApplicationID() {
        return applicationID;
    }

    /**
     * Gets the applicant student.
     *
     * @return the student who applied
     */
    public Student getApplicant() {
        return applicant;
    }

    /**
     * Gets the internship opportunity.
     *
     * @return the internship opportunity
     */
    public InternshipOpportunity getOpportunity() {
        return opportunity;
    }

    /**
     * Gets the current status of the application.
     *
     * @return the status
     */
    public ApplicationStatus getStatusEnum() {
        return status;
    }

    /**
     * Gets the status label for UI/persistence needs.
     *
     * @return the status label
     */
    public String getStatus() {
        return status.getLabel();
    }

    /**
     * Gets the date the application was applied.
     *
     * @return the applied date
     */
    public Date getAppliedDate() {
        return appliedDate;
    }

    /**
     * Checks if the application was manually withdrawn.
     *
     * @return true if manually withdrawn, false otherwise
     */
    public boolean isManuallyWithdrawn() {
        return manuallyWithdrawn;
    }

    /**
     * Sets the manually withdrawn flag.
     *
     * @param manuallyWithdrawn true if manually withdrawn
     */
    public void setManuallyWithdrawn(boolean manuallyWithdrawn) {
        this.manuallyWithdrawn = manuallyWithdrawn;
    }

    /**
     * Gets the previous status before withdrawal request.
     *
     * @return the previous status
     */
    public ApplicationStatus getPreviousStatusEnum() {
        return previousStatus;
    }

    /**
     * Sets the previous status.
     *
     * @param previousStatus the previous status to set
     */
    public void setPreviousStatus(ApplicationStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    /**
     * Gets the previous status label.
     *
     * @return previous status label or null
     */
    public String getPreviousStatus() {
        return previousStatus != null ? previousStatus.getLabel() : null;
    }
}