import java.util.Scanner;

/**
 * Factory for creating menu handlers.
 * Decouples the User model from the MenuHandler implementation.
 */
public class MenuHandlerFactory {
    private MenuHandlerFactory() {
        // Utility class
    }

    /**
     * Creates a menu handler for the given user.
     *
     * @param user the user
     * @param internshipService the internship service
     * @param applicationService the application service
     * @param userService the user service
     * @param scanner the scanner
     * @return the menu handler
     */
    public static IMenuHandler createMenuHandler(User user, IInternshipService internshipService, IApplicationService applicationService, IUserService userService, Scanner scanner) {
        if (user.isStudent()) {
            return new StudentMenuHandler(user.asStudent(), internshipService, (IStudentApplicationService) applicationService, userService, scanner);
        } else if (user.isCompanyRepresentative()) {
            return new CompanyRepMenuHandler(user.asCompanyRepresentative(), internshipService, applicationService, userService, scanner);
        } else if (user.isCareerCenterStaff()) {
            return new CareerStaffMenuHandler(user.asCareerCenterStaff(), userService, internshipService, applicationService, scanner);
        } else {
            throw new IllegalArgumentException("Unknown user type");
        }
    }
}