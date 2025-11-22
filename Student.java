import java.util.ArrayList;
import java.util.List;

/**
 * Represents a student user who can apply for internships.
 */
public class Student extends User {

    private final int yearOfStudy;
    private final String major;
    private final double gpa;

    private IInternshipRepository internshipRepository;
    private IApplicationRepository applicationRepository;

    /**
     * Constructs a Student.
     *
     * @param userID the user ID
     * @param name the name
     * @param password the password
     * @param yearOfStudy the year of study
     * @param major the major
     * @param gpa the GPA
     * @param internshipRepository the internship repository
     * @param applicationRepository the application repository
     */
    public Student(String userID, String name, String password, int yearOfStudy, String major, double gpa,
                   IInternshipRepository internshipRepository, IApplicationRepository applicationRepository) {
        super(userID, name, password);
        this.yearOfStudy = yearOfStudy;
        this.major = major;
        this.gpa = gpa;
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
    }

    /**
     * Constructs a Student with hashed password.
     *
     * @param userID the user ID
     * @param name the name
     * @param passwordHash the hashed password
     * @param salt the salt
     * @param yearOfStudy the year of study
     * @param major the major
     * @param gpa the GPA
     * @param internshipRepository the internship repository
     * @param applicationRepository the application repository
     */
    public Student(String userID, String name, String passwordHash, String salt, int yearOfStudy, String major, double gpa,
                   IInternshipRepository internshipRepository, IApplicationRepository applicationRepository) {
        super(userID, name, passwordHash, salt);
        this.yearOfStudy = yearOfStudy;
        this.major = major;
        this.gpa = gpa;
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
    }

    /**
     * Sets the internship repository.
     *
     * @param internshipRepository the internship repository
     */
    public void setInternshipRepository(IInternshipRepository internshipRepository) {
        this.internshipRepository = internshipRepository;
    }

    /**
     * Sets the application repository.
     *
     * @param applicationRepository the application repository
     */
    public void setApplicationRepository(IApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    /**
     * Checks if the student is eligible for a given level.
     *
     * @param level the level
     * @return true if eligible
     */
    private boolean isEligibleForLevel(String level) {
        if (level == null) {
            return false;
        }
        if (yearOfStudy <= 2) {
            return "Basic".equalsIgnoreCase(level);
        }
        // Year 3 and above can apply for any level
        return true;
    }

    /**
     * Gets all internships visible to the student.
     *
     * @return list of internships
     */
    public List<InternshipOpportunity> viewAllInternships() {
        List<InternshipOpportunity> allInternships = new ArrayList<>();
        if (internshipRepository == null || applicationRepository == null) {
            return allInternships;
        }
        for (InternshipOpportunity opportunity : internshipRepository.getAllInternships()) {
            if (InternshipOpportunity.STATUS_APPROVED.equals(opportunity.getStatus())) {
                boolean hasApplied = false;
                for (Application app : applicationRepository.getAllApplications()) {
                    if (app.getApplicant().getUserID().equals(this.userID) &&
                        app.getOpportunity().getOpportunityID().equals(opportunity.getOpportunityID())) {
                        hasApplied = true;
                        break;
                    }
                }
                if (opportunity.isVisible() || hasApplied) {
                    allInternships.add(opportunity);
                }
            }
        }
        return allInternships;
    }

    /**
     * Gets the year of study.
     *
     * @return the year of study
     */
    public int getYearOfStudy() {
        return yearOfStudy;
    }

    /**
     * Gets the major.
     *
     * @return the major
     */
    public String getMajor() {
        return major;
    }

    /**
     * Gets the GPA.
     *
     * @return the GPA
     */
    public double getGpa() {
        return gpa;
    }

    /**
     * Checks if the student is eligible for the given internship.
     *
     * @param opportunity the internship opportunity
     * @return true if eligible
     */
    public boolean isEligibleForInternship(InternshipOpportunity opportunity) {
        return opportunity.isVisible() &&
               opportunity.getPreferredMajor().equalsIgnoreCase(this.major) &&
               isEligibleForLevel(opportunity.getLevel()) &&
               this.gpa >= opportunity.getMinGPA();
    }

    /**
     * Gets the reason why the student is ineligible for the internship.
     *
     * @param opportunity the internship opportunity
     * @return the ineligibility reason or "Eligible" if eligible
     */
    public String getIneligibilityReason(InternshipOpportunity opportunity) {
        if (!opportunity.isVisible()) {
            return "Internship is not visible";
        }
        if (!opportunity.getPreferredMajor().equalsIgnoreCase(this.major)) {
            return "Major mismatch: required " + opportunity.getPreferredMajor() + ", your major " + this.major;
        }
        if (!isEligibleForLevel(opportunity.getLevel())) {
            return "Level restriction: year 1-2 can only apply for Basic level";
        }
        if (this.gpa < opportunity.getMinGPA()) {
            return "GPA requirement not met: required " + opportunity.getMinGPA() + ", your GPA " + this.gpa;
        }
        return "Eligible";
    }

    @Override
    public boolean isStudent() {
        return true;
    }

    @Override
    public Student asStudent() {
        return this;
    }
}
