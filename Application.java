import java.util.Date;

public class Application {
    private final String applicationID;
    private final Student applicant;
    private final InternshipOpportunity opportunity;
    private String status;
    private final Date appliedDate;

    public Application(String applicationID, Student applicant, InternshipOpportunity opportunity, String status) {
        this.applicationID = applicationID;
        this.applicant = applicant;
        this.opportunity = opportunity;
        this.status = status;
        this.appliedDate = new Date();
    }

    public void updateStatus(String newStatus) {
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
}