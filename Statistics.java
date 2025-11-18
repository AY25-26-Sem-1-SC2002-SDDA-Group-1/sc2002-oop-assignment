import java.util.HashMap;
import java.util.Map;

public class Statistics {
    private Map<String, Integer> applicationCounts;
    private Map<String, Integer> acceptanceCounts;
    private Map<String, Integer> rejectionCounts;
    private Map<String, Double> averageGPAByLevel;
    private int totalApplications;
    private int totalAcceptances;
    private int totalRejections;

    public Statistics() {
        this.applicationCounts = new HashMap<>();
        this.acceptanceCounts = new HashMap<>();
        this.rejectionCounts = new HashMap<>();
        this.averageGPAByLevel = new HashMap<>();
        this.totalApplications = 0;
        this.totalAcceptances = 0;
        this.totalRejections = 0;
    }

    public void incrementApplicationCount(String category) {
        applicationCounts.put(category, applicationCounts.getOrDefault(category, 0) + 1);
        totalApplications++;
    }

    public void incrementAcceptanceCount(String category) {
        acceptanceCounts.put(category, acceptanceCounts.getOrDefault(category, 0) + 1);
        totalAcceptances++;
    }

    public void incrementRejectionCount(String category) {
        rejectionCounts.put(category, rejectionCounts.getOrDefault(category, 0) + 1);
        totalRejections++;
    }

    public void updateAverageGPA(String level, double gpa) {
        if (!averageGPAByLevel.containsKey(level)) {
            averageGPAByLevel.put(level, gpa);
        } else {
            double current = averageGPAByLevel.get(level);
            averageGPAByLevel.put(level, (current + gpa) / 2);
        }
    }

    public void displayStudentStatistics(Student student) {
        System.out.println("\n=== YOUR STATISTICS ===");
        System.out.println("Student ID: " + student.getUserID());
        System.out.println("Name: " + student.getName());
        System.out.println("Major: " + student.getMajor());
        System.out.println("Year of Study: " + student.getYearOfStudy());
        System.out.println("GPA: " + student.getGpa());

        int appliedCount = 0;
        int acceptedCount = 0;
        int rejectedCount = 0;
        int confirmedCount = 0;
        int pendingCount = 0;
        int withdrawnCount = 0;
        int queuedCount = 0;

        // Track applications by level
        int basicApplied = 0, intermediateApplied = 0, advancedApplied = 0;
        int basicAccepted = 0, intermediateAccepted = 0, advancedAccepted = 0;

        for (Application app : Database.getApplications()) {
            if (app.getApplicant().getUserID().equals(student.getUserID())) {
                appliedCount++;
                String level = app.getOpportunity().getLevel();

                switch (level) {
                    case "Basic":
                        basicApplied++;
                        break;
                    case "Intermediate":
                        intermediateApplied++;
                        break;
                    case "Advanced":
                        advancedApplied++;
                        break;
                }

                switch (app.getStatus()) {
                    case "Successful":
                        acceptedCount++;
                        switch (level) {
                            case "Basic": basicAccepted++; break;
                            case "Intermediate": intermediateAccepted++; break;
                            case "Advanced": advancedAccepted++; break;
                        }
                        break;
                    case "Confirmed":
                        acceptedCount++;
                        confirmedCount++;
                        switch (level) {
                            case "Basic": basicAccepted++; break;
                            case "Intermediate": intermediateAccepted++; break;
                            case "Advanced": advancedAccepted++; break;
                        }
                        break;
                    case "Unsuccessful":
                        rejectedCount++;
                        break;
                    case "Pending":
                        pendingCount++;
                        break;
                    case "Withdrawn":
                        withdrawnCount++;
                        break;
                    case "Queued":
                        queuedCount++;
                        break;
                }
            }
        }

        System.out.println("\nApplication Summary:");
        System.out.println("Total Applications: " + appliedCount);
        System.out.println("  - Pending: " + pendingCount);
        System.out.println("  - Successful: " + (acceptedCount - confirmedCount));
        System.out.println("  - Confirmed: " + confirmedCount);
        System.out.println("  - Rejected: " + rejectedCount);
        System.out.println("  - Withdrawn: " + withdrawnCount);
        System.out.println("  - Queued: " + queuedCount);

        System.out.println("\nApplications by Level:");
        System.out.println("Basic Level: " + basicApplied + " applied, " + basicAccepted + " accepted");
        System.out.println("Intermediate Level: " + intermediateApplied + " applied, " + intermediateAccepted + " accepted");
        System.out.println("Advanced Level: " + advancedApplied + " applied, " + advancedAccepted + " accepted");

        if (appliedCount > 0) {
            double acceptanceRate = (double) acceptedCount / appliedCount * 100;
            System.out.println("\nOverall Acceptance Rate: " + String.format("%.1f%%", acceptanceRate));
        }

        // GPA Analysis
        System.out.println("\nGPA Analysis:");
        System.out.println("Your GPA: " + student.getGpa());
        System.out.println("Eligible for Basic Level: " + (student.getGpa() >= 2.5 ? "Yes" : "No"));
        System.out.println("Eligible for Intermediate Level: " + (student.getGpa() >= 3.0 ? "Yes" : "No"));
        System.out.println("Eligible for Advanced Level: " + (student.getGpa() >= 3.5 ? "Yes" : "No"));
    }

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

        // Track by level
        int basicInternships = 0, intermediateInternships = 0, advancedInternships = 0;
        int basicFilled = 0, intermediateFilled = 0, advancedFilled = 0;

        for (InternshipOpportunity opp : Database.getInternships()) {
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

                for (Application app : Database.getApplications()) {
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
        System.out.println("Confirmed Placements: " + confirmedPlacements);

        if (totalApplications > 0) {
            double acceptanceRate = (double) totalAccepted / totalApplications * 100;
            System.out.println("Acceptance Rate: " + String.format("%.1f%%", acceptanceRate));
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

    public void displaySystemStatistics() {
        System.out.println("\n=== SYSTEM STATISTICS ===");
        
        int totalStudents = 0;
        int totalCompanyReps = 0;
        int totalStaff = 0;
        
        for (User user : Database.getUsers()) {
            if (user instanceof Student) {
                totalStudents++;
            } else if (user instanceof CompanyRepresentative) {
                totalCompanyReps++;
            } else if (user instanceof CareerCenterStaff) {
                totalStaff++;
            }
        }
        
        int totalInternships = Database.getInternships().size();
        int totalApplications = Database.getApplications().size();
        
        System.out.println("User Statistics:");
        System.out.println("Total Students: " + totalStudents);
        System.out.println("Total Company Representatives: " + totalCompanyReps);
        System.out.println("Total Career Center Staff: " + totalStaff);
        
        System.out.println("\nInternship Statistics:");
        System.out.println("Total Internships: " + totalInternships);
        System.out.println("Total Applications: " + totalApplications);
        
        Map<String, Integer> levelCounts = new HashMap<>();
        Map<String, Integer> majorCounts = new HashMap<>();
        
        for (InternshipOpportunity opp : Database.getInternships()) {
            levelCounts.put(opp.getLevel(), levelCounts.getOrDefault(opp.getLevel(), 0) + 1);
            majorCounts.put(opp.getPreferredMajor(), majorCounts.getOrDefault(opp.getPreferredMajor(), 0) + 1);
        }
        
        System.out.println("\nInternships by Level:");
        for (Map.Entry<String, Integer> entry : levelCounts.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        
        System.out.println("\nInternships by Major:");
        for (Map.Entry<String, Integer> entry : majorCounts.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    // Getters
    public Map<String, Integer> getApplicationCounts() {
        return applicationCounts;
    }

    public Map<String, Integer> getAcceptanceCounts() {
        return acceptanceCounts;
    }

    public Map<String, Integer> getRejectionCounts() {
        return rejectionCounts;
    }

    public int getTotalApplications() {
        return totalApplications;
    }

    public int getTotalAcceptances() {
        return totalAcceptances;
    }

    public int getTotalRejections() {
        return totalRejections;
    }
}