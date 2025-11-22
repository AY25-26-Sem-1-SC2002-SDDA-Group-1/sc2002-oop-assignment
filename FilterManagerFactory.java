import java.util.Scanner;

/**
 * Factory for creating FilterManager instances.
 * Provides dependency injection for FilterManager.
 */
public class FilterManagerFactory {
    private FilterManagerFactory() {
        // Utility class
    }

    /**
     * Creates a new FilterManager with the given scanner.
     *
     * @param scanner the scanner for input
     * @return a new FilterManager instance
     */
    public static FilterManager createFilterManager(Scanner scanner) {
        return new FilterManager(scanner);
    }

    /**
     * Creates a new FilterManager with the given scanner and user type.
     *
     * @param scanner the scanner for input
     * @param userType the user type for filtering
     * @return a new FilterManager instance
     */
    public static FilterManager createFilterManager(Scanner scanner, String userType) {
        return new FilterManager(scanner, userType);
    }
}