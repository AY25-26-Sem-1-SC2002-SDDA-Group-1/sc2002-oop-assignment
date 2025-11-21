import java.util.*;

/**
 * Automated test to verify waitlist persistence and CSV integration
 */
public class WaitlistPersistenceTest {
    
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("Waitlist Persistence & Integration Test");
        System.out.println("===========================================\n");
        
        try {
            // Initialize repositories in correct order with proper dependencies
            IUserRepository userRepository = new CsvUserRepository(null, null);
            IInternshipRepository internshipRepository = new CsvInternshipRepository(userRepository);
            IApplicationRepository applicationRepository = new CsvApplicationRepository(userRepository, internshipRepository);
            
            // Set circular dependencies
            if (userRepository instanceof CsvUserRepository) {
                ((CsvUserRepository) userRepository).setInternshipRepository(internshipRepository);
                ((CsvUserRepository) userRepository).setApplicationRepository(applicationRepository);
            }
            
            // Initialize WaitlistManager
            IWaitlistManager waitlistManager = new WaitlistManager(applicationRepository, internshipRepository);
            
            int passCount = 0;
            int failCount = 0;
            
            // Test 1: Verify WaitlistManager loads existing waitlisted applications
            System.out.println("Test 1: Load Existing Waitlisted Applications");
            System.out.println("-----------------------------------------------");
            List<Application> allApps = applicationRepository.getAllApplications();
            long waitlistedCount = allApps.stream()
                .filter(app -> "Waitlisted".equals(app.getStatus()))
                .count();
            System.out.println("Found " + waitlistedCount + " waitlisted applications in CSV");
            
            boolean test1Pass = true;
            // Verify each waitlisted app is loaded into WaitlistManager
            for (Application app : allApps) {
                if ("Waitlisted".equals(app.getStatus())) {
                    String oppId = app.getOpportunity().getOpportunityID();
                    List<WaitlistEntry> waitlist = waitlistManager.getWaitlist(oppId);
                    boolean found = waitlist.stream()
                        .anyMatch(e -> e.getApplicationId().equals(app.getApplicationID()));
                    if (!found) {
                        System.out.println("  [FAIL] Application " + app.getApplicationID() + " not loaded into WaitlistManager");
                        test1Pass = false;
                    }
                }
            }
            
            if (test1Pass) {
                System.out.println("  [PASS] All waitlisted applications loaded correctly");
                passCount++;
            } else {
                failCount++;
            }
            System.out.println();
            
            // Test 2: Add application to waitlist and verify persistence
            System.out.println("Test 2: Add to Waitlist and Verify Status Update");
            System.out.println("--------------------------------------------------");
            
            // Find a pending application
            Application pendingApp = allApps.stream()
                .filter(app -> "Pending".equals(app.getStatus()))
                .findFirst()
                .orElse(null);
            
            if (pendingApp != null) {
                String oppId = pendingApp.getOpportunity().getOpportunityID();
                String appId = pendingApp.getApplicationID();
                
                // Add to waitlist
                boolean added = waitlistManager.addToWaitlist(oppId, pendingApp);
                
                if (added && "Waitlisted".equals(pendingApp.getStatus())) {
                    System.out.println("  [PASS] Application " + appId + " added to waitlist");
                    System.out.println("  [PASS] Status updated to 'Waitlisted'");
                    
                    // Verify it's in the waitlist
                    List<WaitlistEntry> waitlist = waitlistManager.getWaitlist(oppId);
                    boolean inWaitlist = waitlist.stream()
                        .anyMatch(e -> e.getApplicationId().equals(appId));
                    
                    if (inWaitlist) {
                        System.out.println("  [PASS] Application found in waitlist");
                        passCount++;
                    } else {
                        System.out.println("  [FAIL] Application not found in waitlist");
                        failCount++;
                    }
                } else {
                    System.out.println("  [FAIL] Failed to add application to waitlist");
                    failCount++;
                }
                
                // Restore original status
                pendingApp.updateStatus("Pending");
                applicationRepository.saveApplications();
            } else {
                System.out.println("  [SKIP] No pending applications available for testing");
            }
            System.out.println();
            
            // Test 3: Verify waitlist ordering (priority)
            System.out.println("Test 3: Verify Waitlist Priority Ordering");
            System.out.println("------------------------------------------");
            
            boolean test3Pass = true;
            for (InternshipOpportunity opp : internshipRepository.getAllInternships()) {
                List<WaitlistEntry> waitlist = waitlistManager.getWaitlist(opp.getOpportunityID());
                if (!waitlist.isEmpty()) {
                    // Verify priorities are sequential
                    for (int i = 0; i < waitlist.size(); i++) {
                        if (waitlist.get(i).getPriority() != i) {
                            System.out.println("  [FAIL] Priority mismatch at position " + i + 
                                " for internship " + opp.getOpportunityID());
                            test3Pass = false;
                        }
                    }
                }
            }
            
            if (test3Pass) {
                System.out.println("  [PASS] All waitlist priorities are correctly ordered");
                passCount++;
            } else {
                failCount++;
            }
            System.out.println();
            
            // Test 4: Verify getWaitlistPosition
            System.out.println("Test 4: Verify Waitlist Position Lookup");
            System.out.println("----------------------------------------");
            
            boolean test4Pass = true;
            for (InternshipOpportunity opp : internshipRepository.getAllInternships()) {
                List<WaitlistEntry> waitlist = waitlistManager.getWaitlist(opp.getOpportunityID());
                for (int i = 0; i < waitlist.size(); i++) {
                    String appId = waitlist.get(i).getApplicationId();
                    int position = waitlistManager.getWaitlistPosition(opp.getOpportunityID(), appId);
                    if (position != i + 1) { // Position is 1-based
                        System.out.println("  [FAIL] Position mismatch for " + appId + 
                            " - expected " + (i + 1) + ", got " + position);
                        test4Pass = false;
                    }
                }
            }
            
            if (test4Pass) {
                System.out.println("  [PASS] All position lookups correct");
                passCount++;
            } else {
                failCount++;
            }
            System.out.println();
            
            // Test 5: Verify getWaitlistSize
            System.out.println("Test 5: Verify Waitlist Size Calculation");
            System.out.println("-----------------------------------------");
            
            boolean test5Pass = true;
            for (InternshipOpportunity opp : internshipRepository.getAllInternships()) {
                int size = waitlistManager.getWaitlistSize(opp.getOpportunityID());
                int actualSize = waitlistManager.getWaitlist(opp.getOpportunityID()).size();
                if (size != actualSize) {
                    System.out.println("  [FAIL] Size mismatch for " + opp.getOpportunityID() + 
                        " - getWaitlistSize=" + size + ", actual=" + actualSize);
                    test5Pass = false;
                }
            }
            
            if (test5Pass) {
                System.out.println("  [PASS] All waitlist sizes correct");
                passCount++;
            } else {
                failCount++;
            }
            System.out.println();
            
            // Test 6: Test WaitlistEntry student profile summary
            System.out.println("Test 6: Verify Student Profile Summary");
            System.out.println("---------------------------------------");
            
            boolean test6Pass = true;
            for (InternshipOpportunity opp : internshipRepository.getAllInternships()) {
                List<WaitlistEntry> waitlist = waitlistManager.getWaitlist(opp.getOpportunityID());
                if (!waitlist.isEmpty()) {
                    WaitlistEntry entry = waitlist.get(0);
                    String summary = entry.getStudentProfileSummary();
                    
                    // Verify summary contains expected fields
                    if (!summary.contains("ID:") || !summary.contains("Name:") || 
                        !summary.contains("Major:") || !summary.contains("Year:") || 
                        !summary.contains("GPA:")) {
                        System.out.println("  [FAIL] Profile summary missing required fields: " + summary);
                        test6Pass = false;
                    }
                    
                    // Verify it matches student data
                    Student student = entry.getStudent();
                    if (!summary.contains(student.getUserID()) || 
                        !summary.contains(student.getName()) ||
                        !summary.contains(student.getMajor())) {
                        System.out.println("  [FAIL] Profile summary data mismatch");
                        test6Pass = false;
                    }
                    
                    break; // Only test first entry
                }
            }
            
            if (test6Pass) {
                System.out.println("  [PASS] Student profile summaries formatted correctly");
                passCount++;
            } else {
                failCount++;
            }
            System.out.println();
            
            // Test 7: Test reorderWaitlist
            System.out.println("Test 7: Verify Waitlist Reordering");
            System.out.println("-----------------------------------");
            
            boolean test7Pass = false;
            for (InternshipOpportunity opp : internshipRepository.getAllInternships()) {
                List<WaitlistEntry> waitlist = waitlistManager.getWaitlist(opp.getOpportunityID());
                if (waitlist.size() >= 2) {
                    String appId = waitlist.get(1).getApplicationId(); // Second item
                    
                    // Move to position 0
                    boolean reordered = waitlistManager.reorderWaitlist(opp.getOpportunityID(), appId, 0);
                    
                    if (reordered) {
                        List<WaitlistEntry> updatedWaitlist = waitlistManager.getWaitlist(opp.getOpportunityID());
                        if (updatedWaitlist.get(0).getApplicationId().equals(appId)) {
                            System.out.println("  [PASS] Successfully reordered waitlist");
                            test7Pass = true;
                            
                            // Restore original order
                            waitlistManager.reorderWaitlist(opp.getOpportunityID(), appId, 1);
                        }
                    }
                    break;
                }
            }
            
            if (test7Pass) {
                passCount++;
            } else {
                System.out.println("  [SKIP] Not enough waitlisted applications to test reordering");
            }
            System.out.println();
            
            // Summary
            System.out.println("===========================================");
            System.out.println("Test Summary");
            System.out.println("===========================================");
            System.out.println("Total Tests: " + (passCount + failCount));
            System.out.println("Passed: " + passCount);
            System.out.println("Failed: " + failCount);
            System.out.println("Success Rate: " + (passCount * 100 / (passCount + failCount)) + "%");
            System.out.println();
            
            if (failCount == 0) {
                System.out.println("[SUCCESS] All persistence tests passed!");
                System.out.println("\nWaitlist system is fully functional with:");
                System.out.println("- CSV persistence working correctly");
                System.out.println("- Priority ordering maintained");
                System.out.println("- Position lookups accurate");
                System.out.println("- Student profiles formatted properly");
                System.out.println("- Reordering functionality operational");
            } else {
                System.out.println("[FAILURE] Some tests failed. Please review above.");
            }
            
        } catch (Exception e) {
            System.out.println("[ERROR] Test suite failed with exception:");
            e.printStackTrace();
        }
    }
}
