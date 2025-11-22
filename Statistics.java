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
    private final IStudentApplicationService applicationService;
    private final IInternshipRepository internshipRepository;
    private final IUserRepository userRepository;

    /**
     * Constructs a Statistics instance.
     *
     * @param applicationRepository the application repository
     * @param internshipRepository the internship repository
     * @param userRepository the user repository
     */
    public Statistics(IApplicationRepository applicationRepository, IStudentApplicationService applicationService,
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
        this.applicationService = applicationService;
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
     * Inner class to hold internship statistics.
     */
    private static class InternshipStats {
        int totalInternships;
        int pendingInternships;
        int approvedInternships;
        int rejectedInternships;
        int filledInternships;
        int basicInternships, intermediateInternships, advancedInternships;
        int basicFilled, intermediateFilled, advancedFilled;
    }

    /**
     * Inner class to hold application statistics.
     */
    private static class ApplicationStats {
        int totalApplications;
        int pendingApplications;
        int totalAccepted;
        int totalRejected;
        int confirmedPlacements;
        int withdrawnApplications;
    }

    /**
     * Calculates internship statistics for a company representative.
     */
    private InternshipStats calculateInternshipStats(CompanyRepresentative rep) {
        InternshipStats stats = new InternshipStats();
        for (InternshipOpportunity opp : internshipRepository.getAllInternships()) {
            if (opp.getCreatedBy().getUserID().equals(rep.getUserID())) {
                stats.totalInternships++;
                String level = opp.getLevel();

                switch (level) {
                    case "Basic": stats.basicInternships++; break;
                    case "Intermediate": stats.intermediateInternships++; break;
                    case "Advanced": stats.advancedInternships++; break;
                }

                switch (opp.getStatus()) {
                    case "Pending": stats.pendingInternships++; break;
                    case "Approved": stats.approvedInternships++; break;
                    case "Rejected": stats.rejectedInternships++; break;
                    case "Filled":
                        stats.filledInternships++;
                        switch (level) {
                            case "Basic": stats.basicFilled++; break;
                            case "Intermediate": stats.intermediateFilled++; break;
                            case "Advanced": stats.advancedFilled++; break;
                        }
                        break;
                }
            }
        }
        return stats;
    }

    /**
     * Calculates application statistics for a company representative's internships.
     */
    private ApplicationStats calculateApplicationStats(CompanyRepresentative rep, InternshipStats internshipStats) {
        ApplicationStats stats = new ApplicationStats();
        for (InternshipOpportunity opp : internshipRepository.getAllInternships()) {
            if (opp.getCreatedBy().getUserID().equals(rep.getUserID())) {
                for (Application app : applicationService.getApplicationRepository().getAllApplications()) {
                    if (app.getOpportunity().getOpportunityID().equals(opp.getOpportunityID())) {
                        stats.totalApplications++;
                        switch (app.getStatusEnum()) {
                            case CONFIRMED:
                            case SUCCESSFUL:
                                stats.totalAccepted++;
                                if (app.getStatusEnum() == ApplicationStatus.CONFIRMED) {
                                    stats.confirmedPlacements++;
                                }
                                break;
                            case UNSUCCESSFUL:
                                stats.totalRejected++;
                                break;
                            case WITHDRAWN:
                                stats.withdrawnApplications++;
                                stats.totalAccepted++;
                                break;
                            case WITHDRAWAL_REQUESTED:
                                // Pending withdrawal, count as accepted for now
                                stats.totalAccepted++;
                                break;
                            case WITHDRAWAL_REJECTED:
                                // Rejected, count as rejected
                                stats.totalRejected++;
                                break;
                            case PENDING:
                                // Pending, not counted in accepted/rejected yet
                                break;
                        }
                    }
                }
            }
        }
        return stats;
    }

    /**
     * Displays internship statistics.
     */
    private void displayInternshipStats(InternshipStats stats) {
        System.out.println("\nInternship Summary:");
        System.out.println("Total Internships Posted: " + stats.totalInternships);
        System.out.println("  - Pending: " + stats.pendingInternships);
        System.out.println("  - Approved: " + stats.approvedInternships);
        System.out.println("  - Rejected: " + stats.rejectedInternships);
        System.out.println("  - Filled: " + stats.filledInternships);

        System.out.println("\nInternships by Level:");
        System.out.println("Basic: " + stats.basicInternships + " posted, " + stats.basicFilled + " filled");
        System.out.println("Intermediate: " + stats.intermediateInternships + " posted, " + stats.intermediateFilled + " filled");
        System.out.println("Advanced: " + stats.advancedInternships + " posted, " + stats.advancedFilled + " filled");
    }

    /**
     * Displays application statistics and performance metrics.
     */
    private void displayApplicationStats(ApplicationStats appStats, InternshipStats internshipStats) {
        System.out.println("\nApplication Summary:");
        System.out.println("Total Applications Received: " + appStats.totalApplications);
        System.out.println("  - Pending: " + appStats.pendingApplications);
        System.out.println("  - Accepted: " + appStats.totalAccepted);
        System.out.println("  - Rejected: " + appStats.totalRejected);
        System.out.println("  - Withdrawn: " + appStats.withdrawnApplications);
        System.out.println("Confirmed Placements: " + appStats.confirmedPlacements);

        if (appStats.totalApplications > 0) {
            double acceptanceRate = (double) appStats.totalAccepted / appStats.totalApplications * 100;
            System.out.println("Acceptance Rate: " + String.format("%.1f%%", acceptanceRate));
            double withdrawalRate = (double) appStats.withdrawnApplications / appStats.totalApplications * 100;
            System.out.println("Withdrawal Rate: " + String.format("%.1f%%", withdrawalRate));
        }

        // Fill rate analysis
        if (internshipStats.approvedInternships > 0) {
            double fillRate = (double) internshipStats.filledInternships / internshipStats.approvedInternships * 100;
            System.out.println("Internship Fill Rate: " + String.format("%.1f%%", fillRate));
        }

        // Average applications per internship
        if (internshipStats.totalInternships > 0) {
            double avgApplications = (double) appStats.totalApplications / internshipStats.totalInternships;
            System.out.println("Average Applications per Internship: " + String.format("%.1f", avgApplications));
        }
    }

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

        InternshipStats internshipStats = calculateInternshipStats(rep);
        ApplicationStats applicationStats = calculateApplicationStats(rep, internshipStats);

        displayInternshipStats(internshipStats);
        displayApplicationStats(applicationStats, internshipStats);
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
        for (Application app : applicationService.getApplicationRepository().getAllApplications()) {
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
        List<InternshipOpportunity> eligibleInternships = applicationService.getEligibleInternshipsForStudent(student.getUserID());
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