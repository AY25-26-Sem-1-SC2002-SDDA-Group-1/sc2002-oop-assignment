import java.util.Date;

/**
 * Represents an internship opportunity created by a company representative.
 */
public class InternshipOpportunity {
    private final String opportunityID;
    private String title;
    private String description;
    private String level;
    private String preferredMajor;
    private Date openingDate;
    private Date closingDate;
    private String status;
    private int maxSlots;
    private boolean visibility;
    private double minGPA;
    private final CompanyRepresentative createdBy;

    /**
     * Constructs an InternshipOpportunity.
     *
     * @param opportunityID the unique ID
     * @param title the title
     * @param description the description
     * @param level the level
     * @param preferredMajor the preferred major
     * @param openingDate the opening date
     * @param closingDate the closing date
     * @param maxSlots the max slots
     * @param minGPA the min GPA
     * @param createdBy the creator
     */
    public InternshipOpportunity(String opportunityID, String title, String description,
                                String level, String preferredMajor, Date openingDate,
                                Date closingDate, int maxSlots, double minGPA, CompanyRepresentative createdBy) {
        this.opportunityID = opportunityID;
        this.title = title;
        this.description = description;
        this.level = level;
        this.preferredMajor = preferredMajor;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.status = "Pending";
        this.maxSlots = maxSlots;
        this.visibility = false;
        this.minGPA = minGPA;
        this.createdBy = createdBy;
    }

    /**
     * Checks if the internship is currently open for applications.
     *
     * @return true if open
     */
    public boolean isOpen() {
        Date now = new Date();
        return now.after(openingDate) && now.before(closingDate) && 
                !status.equals("Filled") && status.equals("Approved");
    }

    /**
     * Checks if the internship is visible to students.
     *
     * @return true if visible
     */
    public boolean isVisible() {
        return visibility && status.equals("Approved");
    }

    /**
     * Gets the opportunity ID.
     *
     * @return the ID
     */
    public String getOpportunityID() {
        return opportunityID;
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the level.
     *
     * @return the level
     */
    public String getLevel() {
        return level;
    }

    /**
     * Gets the preferred major.
     *
     * @return the preferred major
     */
    public String getPreferredMajor() {
        return preferredMajor;
    }

    /**
     * Gets the opening date.
     *
     * @return the opening date
     */
    public Date getOpeningDate() {
        return openingDate;
    }

    /**
     * Gets the closing date.
     *
     * @return the closing date
     */
    public Date getClosingDate() {
        return closingDate;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Sets the title.
     *
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the level.
     *
     * @param level the level
     */
    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * Sets the preferred major.
     *
     * @param preferredMajor the preferred major
     */
    public void setPreferredMajor(String preferredMajor) {
        this.preferredMajor = preferredMajor;
    }

    /**
     * Sets the opening date.
     *
     * @param openingDate the opening date
     */
    public void setOpeningDate(Date openingDate) {
        this.openingDate = openingDate;
    }

    /**
     * Sets the closing date.
     *
     * @param closingDate the closing date
     */
    public void setClosingDate(Date closingDate) {
        this.closingDate = closingDate;
    }

    /**
     * Sets the max slots.
     *
     * @param maxSlots the max slots
     */
    public void setMaxSlots(int maxSlots) {
        this.maxSlots = maxSlots;
    }

    /**
     * Gets the max slots.
     *
     * @return the max slots
     */
    public int getMaxSlots() {
        return maxSlots;
    }

    /**
     * Checks if the opportunity is visible.
     *
     * @return true if visible
     */
    public boolean isVisibility() {
        return visibility;
    }

    /**
     * Sets the visibility.
     *
     * @param visibility true to make visible
     */
    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    /**
     * Gets the creator of the opportunity.
     *
     * @return the company representative who created it
     */
    public CompanyRepresentative getCreatedBy() {
        return createdBy;
    }

    /**
     * Gets the minimum GPA required.
     *
     * @return the min GPA
     */
    public double getMinGPA() {
        return minGPA;
    }

    /**
     * Sets the minimum GPA required.
     *
     * @param minGPA the min GPA
     */
    public void setMinGPA(double minGPA) {
        this.minGPA = minGPA;
    }
}