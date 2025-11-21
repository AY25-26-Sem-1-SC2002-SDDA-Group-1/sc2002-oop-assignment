import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service class for managing waitlist operations across all internship opportunities.
 * Follows Single Responsibility Principle - focused solely on waitlist management.
 * Follows Dependency Inversion Principle - depends on abstractions (repositories).
 */
public class WaitlistManager implements IWaitlistManager {
    // Map: opportunityId -> ordered list of waitlist entries
    private final Map<String, List<WaitlistEntry>> waitlists;
    private final IApplicationRepository applicationRepository;
    private final IInternshipRepository internshipRepository;
    
    public WaitlistManager(IApplicationRepository applicationRepository, 
                          IInternshipRepository internshipRepository) {
        this.waitlists = new ConcurrentHashMap<>();
        this.applicationRepository = applicationRepository;
        this.internshipRepository = internshipRepository;
        loadWaitlistsFromApplications();
    }
    
    /**
     * Load existing waitlisted applications into the waitlist manager
     */
    private void loadWaitlistsFromApplications() {
        for (Application app : applicationRepository.getAllApplications()) {
            if ("Waitlisted".equals(app.getStatus())) {
                String opportunityId = app.getOpportunity().getOpportunityID();
                List<WaitlistEntry> waitlist = waitlists.computeIfAbsent(
                    opportunityId, k -> new ArrayList<>()
                );
                
                // Use existing priority if available, otherwise use list size
                int priority = waitlist.size();
                Date addedDate = app.getQueuedDate() != null ? app.getQueuedDate() : new Date();
                waitlist.add(new WaitlistEntry(app, priority, addedDate));
            }
        }
        
        // Sort each waitlist by priority
        waitlists.values().forEach(list -> list.sort(Comparator.comparingInt(WaitlistEntry::getPriority)));
    }
    
    @Override
    public boolean addToWaitlist(String opportunityId, Application application) {
        if (application == null || opportunityId == null) {
            return false;
        }
        
        List<WaitlistEntry> waitlist = waitlists.computeIfAbsent(
            opportunityId, k -> new ArrayList<>()
        );
        
        // Check if already in waitlist
        if (waitlist.stream().anyMatch(e -> e.getApplicationId().equals(application.getApplicationID()))) {
            return false;
        }
        
        // Add to end of waitlist with lowest priority
        int priority = waitlist.size();
        WaitlistEntry entry = new WaitlistEntry(application, priority);
        waitlist.add(entry);
        
        // Update application status
        application.updateStatus("Waitlisted");
        applicationRepository.saveApplications();
        
        return true;
    }
    
    @Override
    public boolean removeFromWaitlist(String opportunityId, String applicationId) {
        List<WaitlistEntry> waitlist = waitlists.get(opportunityId);
        if (waitlist == null) {
            return false;
        }
        
        boolean removed = waitlist.removeIf(e -> e.getApplicationId().equals(applicationId));
        
        if (removed) {
            // Reindex priorities
            reindexPriorities(waitlist);
            
            // Update application status
            Application app = applicationRepository.getApplicationById(applicationId);
            if (app != null && "Waitlisted".equals(app.getStatus())) {
                app.updateStatus("Successful"); // Revert to successful
                applicationRepository.saveApplications();
            }
        }
        
        return removed;
    }
    
    @Override
    public List<WaitlistEntry> getWaitlist(String opportunityId) {
        List<WaitlistEntry> waitlist = waitlists.get(opportunityId);
        if (waitlist == null) {
            return new ArrayList<>();
        }
        // Return a sorted copy
        return waitlist.stream()
            .sorted(Comparator.comparingInt(WaitlistEntry::getPriority))
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean reorderWaitlist(String opportunityId, String applicationId, int newPosition) {
        List<WaitlistEntry> waitlist = waitlists.get(opportunityId);
        if (waitlist == null || newPosition < 0 || newPosition >= waitlist.size()) {
            return false;
        }
        
        // Find the entry to move
        WaitlistEntry entryToMove = null;
        int oldPosition = -1;
        
        for (int i = 0; i < waitlist.size(); i++) {
            if (waitlist.get(i).getApplicationId().equals(applicationId)) {
                entryToMove = waitlist.get(i);
                oldPosition = i;
                break;
            }
        }
        
        if (entryToMove == null || oldPosition == newPosition) {
            return false;
        }
        
        // Remove from old position
        waitlist.remove(oldPosition);
        
        // Insert at new position
        waitlist.add(newPosition, entryToMove);
        
        // Reindex all priorities
        reindexPriorities(waitlist);
        
        return true;
    }
    
    @Override
    public Application promoteNextFromWaitlist(String opportunityId) {
        List<WaitlistEntry> waitlist = waitlists.get(opportunityId);
        if (waitlist == null || waitlist.isEmpty()) {
            return null;
        }
        
        // Get the highest priority entry (lowest priority number)
        WaitlistEntry nextEntry = waitlist.stream()
            .min(Comparator.comparingInt(WaitlistEntry::getPriority))
            .orElse(null);
        
        if (nextEntry == null) {
            return null;
        }
        
        // Remove from waitlist
        waitlist.remove(nextEntry);
        reindexPriorities(waitlist);
        
        // Update application status to Successful (ready for company to approve)
        Application promotedApp = nextEntry.getApplication();
        promotedApp.updateStatus("Successful");
        applicationRepository.saveApplications();
        
        return promotedApp;
    }
    
    @Override
    public int getWaitlistPosition(String opportunityId, String applicationId) {
        List<WaitlistEntry> waitlist = getWaitlist(opportunityId);
        
        for (int i = 0; i < waitlist.size(); i++) {
            if (waitlist.get(i).getApplicationId().equals(applicationId)) {
                return i + 1; // 1-based position
            }
        }
        
        return -1;
    }
    
    @Override
    public int getWaitlistSize(String opportunityId) {
        List<WaitlistEntry> waitlist = waitlists.get(opportunityId);
        return waitlist != null ? waitlist.size() : 0;
    }
    
    @Override
    public void clearWaitlist(String opportunityId) {
        List<WaitlistEntry> waitlist = waitlists.remove(opportunityId);
        
        if (waitlist != null) {
            // Update all application statuses
            for (WaitlistEntry entry : waitlist) {
                Application app = entry.getApplication();
                if ("Waitlisted".equals(app.getStatus())) {
                    app.updateStatus("Successful");
                }
            }
            applicationRepository.saveApplications();
        }
    }
    
    /**
     * Reindex priorities after removal or reordering
     */
    private void reindexPriorities(List<WaitlistEntry> waitlist) {
        for (int i = 0; i < waitlist.size(); i++) {
            waitlist.get(i).setPriority(i);
        }
    }
}
