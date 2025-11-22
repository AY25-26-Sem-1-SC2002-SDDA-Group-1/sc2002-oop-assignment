import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class for calculating and displaying various statistics related to internships and applications.
 * Provides detailed statistics for students (including eligible internships and active applications)
 * and company representatives, using repository pattern for data access.
 */
public class Statistics {
    private Map<String, Integer> applicationCounts;
    private Map<String, Integer> acceptanceCounts;
    private Map<String, Integer> rejectionCounts;
    private Map<String, Double> averageGPAByLevel;
    private int totalApplications;
    private int totalAcceptances;
    private int totalRejections;
    private final IApplicationRepository applicationRepository;
    private final IInternshipRepository internshipRepository;
    private final IUserRepository userRepository;

    /**
     * Constructs a Statistics instance.
     *
     * @param applicationRepository the application repository
     * @param internshipRepository the internship repository
     * @param userRepository the user repository
     */
    public Statistics(IApplicationRepository applicationRepository,
                     IInternshipRepository internshipRepository,
                     IUserRepository userRepository) {
        this.applicationCounts = new HashMap<>();
        this.acceptanceCounts = new HashMap<>();
        this.rejectionCounts = new HashMap<>();
        this.averageGPAByLevel = new HashMap<>();
        this.totalApplications = 0;
        this.totalAcceptances = 0;
        this.totalRejections = 0;
        this.applicationRepository = applicationRepository;
        this.internshipRepository = internshipRepository;
        this.userRepository = userRepository;
    }

    /**
     * Increments the application count for a category.
     *
     * @param category the category
     */
    public void incrementApplicationCount(String category) {
        applicationCounts.put(category, applicationCounts.getOrDefault(category, 0) + 1);
        totalApplications++;
    }

    /**
     * Increments the acceptance count for a category.
     *
     * @param category the category
     */
    public void incrementAcceptanceCount(String category) {
        acceptanceCounts.put(category, acceptanceCounts.getOrDefault(category, 0) + 1);
        totalAcceptances++;
    }

    /**
     * Increments the rejection count for a category.
     *
     * @param category the category
     */
    public void incrementRejectionCount(String category) {
        rejectionCounts.put(category, rejectionCounts.getOrDefault(category, 0) + 1);
        totalRejections++;
    }

    /**
     * Updates the average GPA for a level.
     *
     * @param level the level
     * @param gpa the GPA
     */
    public void updateAverageGPA(String level, double gpa) {
        if (!averageGPAByLevel.containsKey(level)) {
            averageGPAByLevel.put(level, gpa);
        } else {
            double current = averageGPAByLevel.get(level);
            averageGPAByLevel.put(level, (current + gpa) / 2);
        }
    }

    /**
     * Displays statistics for a company representative.
     *
    /**
     * Displays comprehensive statistics for a company representative, including internship summary,
     * application breakdown, and performance metrics.
     *
     * @param rep the company representative to display statistics for
     */
    public void displayCompanyRepresentativeStatistics(CompanyRepresentative rep) {
        System.out.println("\n=== COMPANY STATISTICS ===");
        System.out.println("Representative ID: " + rep.getUserID());
        System.out.println("Name: " + rep.getName());
        System.out.println("Company: " + rep.getCompanyName());
        System.out.println("Department: " + rep.getDepartment());
        System.out.println("Position: " + rep.getPosition());
        System.out.println("Account Status: " + (rep.isApproved() ? "Approved" : "Pending"));

        int totalInternships = 0;
        int pendingInternships = 0;
        int approvedInternships = 0;
        int rejectedInternships = 0;
        int filledInternships = 0;
        int totalApplications = 0;
        int pendingApplications = 0;
        int totalAccepted = 0;
        int totalRejected = 0;
        int confirmedPlacements = 0;
        int withdrawnApplications = 0;

        // Track by level
        int basicInternships = 0, intermediateInternships = 0, advancedInternships = 0;
        int basicFilled = 0, intermediateFilled = 0, advancedFilled = 0;

        for (InternshipOpportunity opp : internshipRepository.getAllInternships()) {
            if (opp.getCreatedBy().getUserID().equals(rep.getUserID())) {
                totalInternships++;
                String level = opp.getLevel();

                switch (level) {
                    case "Basic": basicInternships++; break;
                    case "Intermediate": intermediateInternships++; break;
                    case "Advanced": advancedInternships++; break;
                }

                switch (opp.getStatus()) {
                    case "Pending": pendingInternships++; break;
                    case "Approved": approvedInternships++; break;
                    case "Rejected": rejectedInternships++; break;
                    case "Filled":
                        filledInternships++;
                        switch (level) {
                            case "Basic": basicFilled++; break;
                            case "Intermediate": intermediateFilled++; break;
                            case "Advanced": advancedFilled++; break;
                        }
                        break;
                }

                for (Application app : applicationRepository.getAllApplications()) {
                    if (app.getOpportunity().getOpportunityID().equals(opp.getOpportunityID())) {
                        totalApplications++;
                        switch (app.getStatus()) {
                            case "Pending":
                                pendingApplications++;
                                break;
                            case "Successful":
                            case "Confirmed":
                                totalAccepted++;
                                if (app.getStatus().equals("Confirmed")) {
                                    confirmedPlacements++;
                                }
                                break;
                            case "Unsuccessful":
                                totalRejected++;
                                break;
                            case "Withdrawn":
                                withdrawnApplications++;
                                totalAccepted++;
                                break;
                        }
                    }
                }
            }
        }

        System.out.println("\nInternship Summary:");
        System.out.println("Total Internships Posted: " + totalInternships);
        System.out.println("  - Pending: " + pendingInternships);
        System.out.println("  - Approved: " + approvedInternships);
        System.out.println("  - Rejected: " + rejectedInternships);
        System.out.println("  - Filled: " + filledInternships);

        System.out.println("\nInternships by Level:");
        System.out.println("Basic: " + basicInternships + " posted, " + basicFilled + " filled");
        System.out.println("Intermediate: " + intermediateInternships + " posted, " + intermediateFilled + " filled");
        System.out.println("Advanced: " + advancedInternships + " posted, " + advancedFilled + " filled");

        System.out.println("\nApplication Summary:");
        System.out.println("Total Applications Received: " + totalApplications);
        System.out.println("  - Pending: " + pendingApplications);
        System.out.println("  - Accepted: " + totalAccepted);
        System.out.println("  - Rejected: " + totalRejected);
        System.out.println("  - Withdrawn: " + withdrawnApplications);
        System.out.println("Confirmed Placements: " + confirmedPlacements);

        if (totalApplications > 0) {
            double acceptanceRate = (double) totalAccepted / totalApplications * 100;
            System.out.println("Acceptance Rate: " + String.format("%.1f%%", acceptanceRate));
            double withdrawalRate = (double) withdrawnApplications / totalApplications * 100;
            System.out.println("Withdrawal Rate: " + String.format("%.1f%%", withdrawalRate));
        }

        // Fill rate analysis
        if (approvedInternships > 0) {
            double fillRate = (double) filledInternships / approvedInternships * 100;
            System.out.println("Internship Fill Rate: " + String.format("%.1f%%", fillRate));
        }

        // Average applications per internship
        if (totalInternships > 0) {
            double avgApplications = (double) totalApplications / totalInternships;
            System.out.println("Average Applications per Internship: " + String.format("%.1f", avgApplications));
        }
    }

    /**
     * Displays comprehensive statistics for a student, including application history,
     * success rate (including withdrawn applications that were previously successful),
     * eligible internships, and active application count.
     *
     * @param student the student to display statistics for
     */
    public void displayStudentStatistics(Student student) {
        System.out.println("\n=== STUDENT STATISTICS ===");
        System.out.println("Student ID: " + student.getUserID());
        System.out.println("Name: " + student.getName());
        System.out.println("Major: " + student.getMajor());
        System.out.println("Year of Study: " + student.getYearOfStudy());
        System.out.println("GPA: " + student.getGpa());

        List<Application> studentApplications = new ArrayList<>();
        for (Application app : applicationRepository.getAllApplications()) {
            if (app.getApplicant().getUserID().equals(student.getUserID())) {
                studentApplications.add(app);
            }
        }

        int totalApplications = studentApplications.size();
        int pendingApplications = 0;
        int successfulApplications = 0;
        int unsuccessfulApplications = 0;
        int confirmedApplications = 0;
        int withdrawnApplications = 0;

        for (Application app : studentApplications) {
            String statusToCount = app.getStatus();
            // For withdrawal requests, count under previous status until approved
            if ("Withdrawal Requested".equals(statusToCount) && app.getPreviousStatus() != null) {
                statusToCount = app.getPreviousStatus();
            }
            
            switch (statusToCount) {
                case "Pending": pendingApplications++; break;
                case "Successful": successfulApplications++; break;
                case "Unsuccessful": unsuccessfulApplications++; break;
                case "Confirmed": confirmedApplications++; break;
                case "Withdrawn": withdrawnApplications++; break;
            }
        }

        System.out.println("\nApplication Summary:");
        System.out.println("Total Applications Submitted: " + totalApplications);
        int successfulTotal = successfulApplications + confirmedApplications + withdrawnApplications;
        System.out.println("  - Accepted: " + successfulTotal);
        System.out.println("  - Rejected: " + unsuccessfulApplications);
        System.out.println("  - Pending: " + pendingApplications);
        System.out.println("  - Withdrawn: " + withdrawnApplications);

        if (totalApplications > 0) {
            // Acceptance rate includes applications that were accepted (successful/confirmed) or later withdrawn
            double acceptanceRate = (double) successfulTotal / totalApplications * 100;
            System.out.println("Acceptance Rate: " + String.format("%.1f%%", acceptanceRate));
        }

        // Eligible internships
        List<InternshipOpportunity> eligibleInternships = student.viewEligibleInternships();
        System.out.println("\nEligible Internships: " + eligibleInternships.size());

        // Active applications (not withdrawn or unsuccessful)
        int activeApplications = pendingApplications + successfulApplications + confirmedApplications;
        System.out.println("Active Applications: " + activeApplications + " (max 3 allowed)");

        // Additional insights
        System.out.println("\nAdditional Insights:");
        System.out.println("Student GPA: " + String.format("%.2f", student.getGpa()));
        System.out.println("Student Major: " + student.getMajor());
        
        // Unique companies applied to
        Set<String> companies = new HashSet<>();
        for (Application app : studentApplications) {
            companies.add(app.getOpportunity().getCreatedBy().getCompanyName());
        }
        System.out.println("Unique Companies Applied To: " + companies.size());
        
        // Total internships available
        List<InternshipOpportunity> allInternships = internshipRepository.getAllInternships();
        System.out.println("Total Internships Available: " + allInternships.size());
    }

    // Getters

    /**
     * Gets the application counts.
     *
     * @return map of application counts
     */
    public Map<String, Integer> getApplicationCounts() {
        return applicationCounts;
    }

    /**
     * Gets the acceptance counts.
     *
     * @return map of acceptance counts
     */
    public Map<String, Integer> getAcceptanceCounts() {
        return acceptanceCounts;
    }

    /**
     * Gets the rejection counts.
     *
     * @return map of rejection counts
     */
    public Map<String, Integer> getRejectionCounts() {
        return rejectionCounts;
    }

    /**
     * Gets the total applications.
     *
     * @return total applications
     */
    public int getTotalApplications() {
        return totalApplications;
    }

    /**
     * Gets the total acceptances.
     *
     * @return total acceptances
     */
    public int getTotalAcceptances() {
        return totalAcceptances;
    }

    /**
     * Gets the total rejections.
     *
     * @return total rejections
     */
    public int getTotalRejections() {
        return totalRejections;
    }
}