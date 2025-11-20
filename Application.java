import java.util.Date;

public class Application {
    private final String applicationID;
    private final Student applicant;
    private final InternshipOpportunity opportunity;
    private String status;
    private final Date appliedDate;
    private boolean manuallyWithdrawn;
    private String previousStatus; // stores status before a withdrawal request
    private Date queuedDate; // stores when student tried to accept but was queued

    public Application(String applicationID, Student applicant, InternshipOpportunity opportunity, String status) {
        this.applicationID = applicationID;
        this.applicant = applicant;
        this.opportunity = opportunity;
        this.status = status;
        this.appliedDate = new Date();
        this.manuallyWithdrawn = false;
        this.previousStatus = null;
        this.queuedDate = null;
    }

    public Application(String applicationID, Student applicant, InternshipOpportunity opportunity, String status, Date appliedDate) {
        this.applicationID = applicationID;
        this.applicant = applicant;
        this.opportunity = opportunity;
        this.status = status;
        this.appliedDate = appliedDate;
        this.manuallyWithdrawn = false;
        this.previousStatus = null;
        this.queuedDate = null;
    }

    public void updateStatus(String newStatus) {
        // Capture previous status when entering withdrawal requested state
        if ("Withdrawal Requested".equals(newStatus) && !"Withdrawal Requested".equals(this.status)) {
            this.previousStatus = this.status;
        }
        // If withdrawal rejected, revert to previous (default Successful if missing)
        if ("Withdrawal Rejected".equals(newStatus)) {
            if (this.previousStatus != null) {
                this.status = this.previousStatus;
            } else {
                this.status = "Successful";
            }
            this.manuallyWithdrawn = false; // rejection cancels manual withdrawal intent
            this.previousStatus = null;
            return;
        }
        // Track when application was queued (student tried to accept but internship was full)
        if ("Queued".equals(newStatus) && !"Queued".equals(this.status)) {
            this.queuedDate = new Date();
        }
        this.status = newStatus;
    }

    public String getApplicationID() {
        return applicationID;
    }

    public Student getApplicant() {
        return applicant;
    }

    public InternshipOpportunity getOpportunity() {
        return opportunity;
    }

    public String getStatus() {
        return status;
    }

    public Date getAppliedDate() {
        return appliedDate;
    }
    
    public boolean isManuallyWithdrawn() {
        return manuallyWithdrawn;
    }
    
    public void setManuallyWithdrawn(boolean manuallyWithdrawn) {
        this.manuallyWithdrawn = manuallyWithdrawn;
    }
    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public Date getQueuedDate() {
        return queuedDate;
    }

    public void setQueuedDate(Date queuedDate) {
        this.queuedDate = queuedDate;
    }
}