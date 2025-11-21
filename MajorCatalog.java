import java.util.Collections;
import java.util.List;

/**
 * Shared catalog of supported majors for consistent major lists across the system.
 * Ensures all UI flows use the same major options.
 */
public final class MajorCatalog {
    private static final List<String> MAJORS = List.of(
        "Computer Science",
        "Computer Engineering",
        "Data Science & AI",
        "Information Engineering & Media",
        "Biomedical Engineering"
    );

    private MajorCatalog() {
        // Prevent instantiation
    }

    /**
     * Gets the unmodifiable list of all majors.
     *
     * @return list of majors
     */
    public static List<String> getMajors() {
        return Collections.unmodifiableList(MAJORS);
    }

    /**
     * Displays the majors as a numbered menu.
     */
    public static void displayMajors() {
        System.out.println("Select Major:");
        for (int i = 0; i < MAJORS.size(); i++) {
            System.out.println((i + 1) + ". " + MAJORS.get(i));
        }
    }

    /**
     * Resolves user input (number or text) to a valid major.
     *
     * @param userInput the user's input
     * @return the resolved major name, or null if invalid
     */
    public static String resolveMajor(String userInput) {
        if (userInput == null || userInput.isBlank()) {
            return null;
        }
        String trimmed = userInput.trim();
        
        // Try parsing as number
        try {
            int choice = Integer.parseInt(trimmed);
            if (choice >= 1 && choice <= MAJORS.size()) {
                return MAJORS.get(choice - 1);
            }
        } catch (NumberFormatException ignored) {
            // Fall through to text match
        }
        
        // Try exact text match (case-insensitive)
        for (String major : MAJORS) {
            if (major.equalsIgnoreCase(trimmed)) {
                return major;
            }
        }
        
        return null;
    }
}
