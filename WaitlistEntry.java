import java.util.Date;

/**
 * Represents an entry in the waitlist for an internship opportunity.
 * Contains application details and metadata for waitlist management.
 */
public class WaitlistEntry {
    private final Application application;
    private final Date addedToWaitlistDate;
    private int priority; // Lower number = higher priority (0 is highest)
    
    public WaitlistEntry(Application application, int priority) {
        this.application = application;
        this.priority = priority;
        this.addedToWaitlistDate = new Date();
    }
    
    public WaitlistEntry(Application application, int priority, Date addedDate) {
        this.application = application;
        this.priority = priority;
        this.addedToWaitlistDate = addedDate;
    }
    
    public Application getApplication() {
        return application;
    }
    
    public Date getAddedToWaitlistDate() {
        return addedToWaitlistDate;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public Student getStudent() {
        return application.getApplicant();
    }
    
    public String getApplicationId() {
        return application.getApplicationID();
    }
    
    /**
     * Get a summary of the student profile for display purposes
     */
    public String getStudentProfileSummary() {
        Student student = application.getApplicant();
        return String.format("ID: %s | Name: %s | Major: %s | Year: %d | GPA: %.2f",
            student.getUserID(),
            student.getName(),
            student.getMajor(),
            student.getYearOfStudy(),
            student.getGpa());
    }
}
