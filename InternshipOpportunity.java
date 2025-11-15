import java.util.Date;

public class InternshipOpportunity {
    private String opportunityID;
    private String title;
    private String description;
    private String level;
    private String preferredMajor;
    private Date openingDate;
    private Date closingDate;
    private String status;
    private int maxSlots;
    private boolean visibility;
    private CompanyRepresentative createdBy;

    public InternshipOpportunity(String opportunityID, String title, String description, 
                               String level, String preferredMajor, Date openingDate, 
                               Date closingDate, int maxSlots, CompanyRepresentative createdBy) {
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
        this.createdBy = createdBy;
    }

    public boolean isOpen() {
        Date now = new Date();
        return now.after(openingDate) && now.before(closingDate) && 
               !status.equals("Filled") && status.equals("Approved");
    }

    public boolean isVisible() {
        return visibility && status.equals("Approved");
    }

    public String getOpportunityID() {
        return opportunityID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLevel() {
        return level;
    }

    public String getPreferredMajor() {
        return preferredMajor;
    }

    public Date getOpeningDate() {
        return openingDate;
    }

    public Date getClosingDate() {
        return closingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxSlots() {
        return maxSlots;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public CompanyRepresentative getCreatedBy() {
        return createdBy;
    }
}