/**
 * Simple DI container for managing service and repository dependencies.
 * Provides factory methods to create and wire all components.
 */
public class ServiceFactory {
    private IUserRepository userRepository;
    private IInternshipRepository internshipRepository;
    private IApplicationRepository applicationRepository;
    private IUserService userService;
    private IInternshipService internshipService;
    private IApplicationService applicationService;

    /**
     * Initializes all repositories and services with proper dependency injection.
     */
    public void initialize() {
        // Initialize repositories in dependency order
        this.userRepository = new CsvUserRepository(null, null);
        this.internshipRepository = new CsvInternshipRepository(userRepository);
        this.applicationRepository = new CsvApplicationRepository(userRepository, internshipRepository);

        // Update user repository with dependencies
        ((CsvUserRepository) this.userRepository).setInternshipRepository(internshipRepository);
        ((CsvUserRepository) this.userRepository).setApplicationRepository(applicationRepository);
        ((CsvUserRepository) this.userRepository).setApplicationService((ICompanyRepApplicationService) this.applicationService);

        // Initialize services
        this.userService = new UserService(userRepository, internshipRepository, applicationRepository);
        this.internshipService = new InternshipService(internshipRepository, userRepository);
        this.applicationService = new ApplicationService(applicationRepository, internshipRepository, userRepository);

        // Set dependencies
        ((UserService) this.userService).setApplicationService((ICompanyRepApplicationService) this.applicationService);
    }

    // Getters for repositories
    public IUserRepository getUserRepository() { return userRepository; }
    public IInternshipRepository getInternshipRepository() { return internshipRepository; }
    public IApplicationRepository getApplicationRepository() { return applicationRepository; }

    // Getters for services
    public IUserService getUserService() { return userService; }
    public IInternshipService getInternshipService() { return internshipService; }
    public IApplicationService getApplicationService() { return applicationService; }
    public ICompanyRepApplicationService getCompanyRepApplicationService() { return (ICompanyRepApplicationService) applicationService; }
}