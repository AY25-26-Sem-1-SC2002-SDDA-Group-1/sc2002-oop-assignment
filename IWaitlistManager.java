import java.util.List;

/**
 * Interface for managing waitlist operations for internship opportunities.
 * Follows Interface Segregation Principle - focused interface for waitlist operations only.
 */
public interface IWaitlistManager {
    /**
     * Add an application to the waitlist for an internship
     * @param opportunityId The internship opportunity ID
     * @param application The application to add to waitlist
     * @return true if successfully added, false otherwise
     */
    boolean addToWaitlist(String opportunityId, Application application);
    
    /**
     * Remove an application from the waitlist
     * @param opportunityId The internship opportunity ID
     * @param applicationId The application ID to remove
     * @return true if successfully removed, false otherwise
     */
    boolean removeFromWaitlist(String opportunityId, String applicationId);
    
    /**
     * Get all waitlist entries for a specific internship, ordered by priority
     * @param opportunityId The internship opportunity ID
     * @return Ordered list of waitlist entries
     */
    List<WaitlistEntry> getWaitlist(String opportunityId);
    
    /**
     * Reorder the waitlist by moving an application to a new position
     * @param opportunityId The internship opportunity ID
     * @param applicationId The application to move
     * @param newPosition The new position (0-based index)
     * @return true if successfully reordered, false otherwise
     */
    boolean reorderWaitlist(String opportunityId, String applicationId, int newPosition);
    
    /**
     * Automatically promote the next waitlisted application when a slot becomes available
     * @param opportunityId The internship opportunity ID
     * @return The promoted application, or null if waitlist is empty
     */
    Application promoteNextFromWaitlist(String opportunityId);
    
    /**
     * Get the position of an application in the waitlist (1-based)
     * @param opportunityId The internship opportunity ID
     * @param applicationId The application ID
     * @return Position in waitlist, or -1 if not in waitlist
     */
    int getWaitlistPosition(String opportunityId, String applicationId);
    
    /**
     * Get the size of the waitlist for an internship
     * @param opportunityId The internship opportunity ID
     * @return Number of applications in the waitlist
     */
    int getWaitlistSize(String opportunityId);
    
    /**
     * Clear all waitlist entries for an internship
     * @param opportunityId The internship opportunity ID
     */
    void clearWaitlist(String opportunityId);
}
