## UML Class Diagram

```mermaid
classDiagram
    class PasswordUtil {
        +generateSalt(): String
        +hashPassword(password: String, salt: String): String
        +verifyPassword(password: String, hash: String, salt: String): bool
        <<static utility class>>
    }

    class UIHelper {
        -UIHelper()
        +printWelcomeBanner(): void
        +printMainMenu(): void
        +printRegistrationMenu(): void
        +printGoodbyeMessage(): void
        +printLoginHeader(): void
        +printSuccessMessage(message: String): void
        +printErrorMessage(message: String): void
        +printWarningMessage(message: String): void
        +printSectionHeader(title: String): void
        +printDivider(): void
        +printStudentMenu(): void
        +printCompanyRepMenu(): void
        +printCareerStaffMenu(): void
        <<static utility class>>
    }

    class MajorCatalog {
        -List~String~ MAJORS
        -MajorCatalog()
        +getMajors(): List~String~
        +displayMajors(): void
        +resolveMajor(userInput: String): String
        <<static utility class>>
    }

    class User {
        <<abstract>>
        -String userID
        -String name
        -String passwordHash
        -String salt
        -boolean isLoggedIn
        +User(userID: String, name: String, password: String)
        +User(userID: String, name: String, passwordHash: String, salt: String)
        +login(password: String): bool
        +logout(): void
        +changePassword(newPassword: String): void
        +setPasswordHash(passwordHash: String): void
        +setSalt(salt: String): void
        +verifyPassword(password: String): bool
        +getUserID(): String
        +getName(): String
        +getPasswordHash(): String
        +getSalt(): String
        +isLoggedIn(): bool
        +createMenuHandler(internshipService, applicationService, userService, scanner): IMenuHandler*
        +isStudent(): bool
        +isCompanyRepresentative(): bool
        +isCareerCenterStaff(): bool
        +asStudent(): Student
        +asCompanyRepresentative(): CompanyRepresentative
        +asCareerCenterStaff(): CareerCenterStaff
    }

     class Student {
         -int yearOfStudy
         -String major
         -double gpa
         -IInternshipRepository internshipRepository
         -IApplicationRepository applicationRepository
         +viewEligibleInternships(): List~InternshipOpportunity~
         +applyForInternship(opportunityID: String): bool
         +viewApplications(): List~Application~
         +acceptInternship(applicationID: String): void
         +requestWithdrawal(applicationID: String): void
         +isEligibleForInternship(opportunity: InternshipOpportunity): bool
         +getIneligibilityReason(opportunity: InternshipOpportunity): String
         +getYearOfStudy(): int
         +getMajor(): String
         +getGpa(): double
         +createMenuHandler(internshipService, applicationService, userService, scanner): IMenuHandler
         +isStudent(): bool
         +asStudent(): Student
         -datesOverlap(start1: Date, end1: Date, start2: Date, end2: Date): boolean
     }

     class CompanyRepresentative {
         -String companyName
         -String department
         -String position
         -boolean isApproved
         -String email
         -IInternshipRepository internshipRepository
         -IApplicationRepository applicationRepository
         -boolean isRejected
         +createInternship(title: String, description: String, level: String, preferredMajor: String, openingDate: Date, closingDate: Date, maxSlots: int, minGPA: double): bool
         +viewApplications(): List~Application~
         +viewApplications(opportunityID: String): List~Application~
         +processApplication(applicationID: String, approve: boolean): bool
         +getPendingApplications(): List~Application~
         +toggleVisibility(opportunityID: String, visible: bool): void
         +getCompanyName(): String
         +getDepartment(): String
         +getPosition(): String
         +isApproved(): bool
         +isRejected(): bool
         +setRejected(rejected: bool): void
         +setApproved(approved: bool): void
         +getEmail(): String
         +createMenuHandler(internshipService, applicationService, userService, scanner): IMenuHandler
         +isCompanyRepresentative(): bool
         +asCompanyRepresentative(): CompanyRepresentative
     }

     class CareerCenterStaff {
         -String staffDepartment
         -IUserRepository userRepository
         -IInternshipRepository internshipRepository
         -IApplicationRepository applicationRepository
         +processCompanyRep(repID: String, approve: boolean): bool
         +processInternship(opportunityID: String, approve: boolean): bool
         +processWithdrawal(applicationID: String, approve: boolean): bool
         +getPendingCompanyReps(): List~CompanyRepresentative~
         +getPendingInternships(): List~InternshipOpportunity~
         +getWithdrawalRequests(): List~Application~
         +generateReports(filters: Map~String,String~): Report
         +getStaffDepartment(): String
         +createMenuHandler(internshipService, applicationService, userService, scanner): IMenuHandler
         +isCareerCenterStaff(): bool
         +asCareerCenterStaff(): CareerCenterStaff
     }

    class InternshipOpportunity {
        -String opportunityID
        -String title
        -String description
        -String level
        -String preferredMajor
        -Date openingDate
        -Date closingDate
        -String status
        -int maxSlots
        -boolean visibility
        -double minGPA
        -CompanyRepresentative createdBy
        +isOpen(): bool
        +isVisible(): bool
        +getOpportunityID(): String
        +getTitle(): String
        +setTitle(title: String): void
        +getDescription(): String
        +setDescription(description: String): void
        +getLevel(): String
        +setLevel(level: String): void
        +getPreferredMajor(): String
        +setPreferredMajor(major: String): void
        +getOpeningDate(): Date
        +setOpeningDate(date: Date): void
        +getClosingDate(): Date
        +setClosingDate(date: Date): void
        +getStatus(): String
        +setStatus(status: String): void
        +getMaxSlots(): int
        +setMaxSlots(slots: int): void
        +isVisibility(): bool
        +setVisibility(visibility: bool): void
        +getMinGPA(): double
        +setMinGPA(minGPA: double): void
        +getCreatedBy(): CompanyRepresentative
    }

    class Application {
        -String applicationID
        -Student applicant
        -InternshipOpportunity opportunity
        -String status
        -Date appliedDate
        -boolean manuallyWithdrawn
        -String previousStatus
        +Application(applicationID: String, applicant: Student, opportunity: InternshipOpportunity, status: String)
        +Application(applicationID: String, applicant: Student, opportunity: InternshipOpportunity, status: String, appliedDate: Date)
        +updateStatus(newStatus: String): void
        +getApplicationID(): String
        +getApplicant(): Student
        +getOpportunity(): InternshipOpportunity
        +getStatus(): String
        +getAppliedDate(): Date
        +isManuallyWithdrawn(): boolean
        +setManuallyWithdrawn(withdrawn: boolean): void
        +getPreviousStatus(): String
    }

    %% Database class removed - all functionality migrated to repository pattern

    class Report {
        -List~InternshipOpportunity~ opportunities
        -Map~String,String~ filters
        +displayReport(): void
        +getOpportunities(): List~InternshipOpportunity~
        +getFilters(): Map~String,String~
    }

    class ReportManager {
        -static ReportManager instance
        -IInternshipRepository internshipRepository
        -IApplicationRepository applicationRepository
        -ReportManager()
        +getInstance(): ReportManager
        +initialize(internshipRepository: IInternshipRepository, applicationRepository: IApplicationRepository): void
        +generateReport(filters: Map~String,String~): Report
        +displayDetailedReport(report: Report): void
        +getApplicationStatistics(): Map~String,Integer~
        +getInternshipStatistics(): Map~String,Integer~
        <<singleton>>
    }

    class FilterSettings {
        -String statusFilter
        -String levelFilter
        -String majorFilter
        -double minGPAFilter
        -String sortBy
        +setStatusFilter(status: String): void
        +setLevelFilter(level: String): void
        +setMajorFilter(major: String): void
        +setMinGPAFilter(minGPA: double): void
        +setSortBy(sortBy: String): void
        +hasActiveFilters(): bool
        +clearFilters(): void
        +applyFilters(opportunities: List~InternshipOpportunity~): List~InternshipOpportunity~
        +toString(): String
    }

    class FilterManager {
        -Scanner scanner
        -FilterSettings filterSettings
        -String userType
        +FilterManager(scanner: Scanner)
        +FilterManager(scanner: Scanner, userType: String)
        +manageFilters(): void
        +hasActiveFilters(): bool
        +getFilterSettings(): FilterSettings
        <<instance-based, customizable per user type>>
    }

    class Statistics {
        -IApplicationRepository applicationRepository
        -IStudentApplicationService applicationService
        -IInternshipRepository internshipRepository
        -IUserRepository userRepository
        +Statistics(appRepo: IApplicationRepository, appService: IStudentApplicationService, internshipRepo: IInternshipRepository, userRepo: IUserRepository)
        +displayStudentStatistics(student: Student): void
        +displayCompanyRepresentativeStatistics(rep: CompanyRepresentative): void
        +displaySystemStatistics(): void
        +calculateInternshipStats(rep: CompanyRepresentative): InternshipStats
        +calculateApplicationStats(rep: CompanyRepresentative, internshipStats: InternshipStats): ApplicationStats
        +displayInternshipStats(stats: InternshipStats): void
        +displayApplicationStats(appStats: ApplicationStats, internshipStats: InternshipStats): void
        <<SRP-compliant with segregated methods and inner data classes>>
    }

    class Statistics.InternshipStats {
        +int totalInternships
        +int pendingInternships
        +int approvedInternships
        +int rejectedInternships
        +int filledInternships
        +int basicInternships, intermediateInternships, advancedInternships
        +int basicFilled, intermediateFilled, advancedFilled
    }

    class Statistics.ApplicationStats {
        +int totalApplications
        +int pendingApplications
        +int totalAccepted
        +int totalRejected
        +int confirmedPlacements
        +int withdrawnApplications
    }

    class IUserRepository {
        <<interface>>
        +getAllUsers(): List~User~
        +getUserById(userId: String): User
        +addUser(user: User): void
        +removeUser(userId: String): void
        +saveUsers(): void throws IOException
        +generateCompanyRepId(): String
    }

    class CsvUserRepository {
        +getAllUsers(): List~User~
        +getUserById(userId: String): User
        +addUser(user: User): void
        +removeUser(userId: String): void
        +saveUsers(): void throws IOException
        +generateCompanyRepId(): String
    }

    class CsvUserRepository {
        +getAllUsers(): List~User~
        +getUserById(userId: String): User
        +addUser(user: User): void
        +removeUser(userId: String): void
        +saveUsers(): void
        +generateCompanyRepId(): String
    }

    class IInternshipRepository {
        +getAllInternships(): List~InternshipOpportunity~
        +getInternshipById(opportunityId: String): InternshipOpportunity
        +addInternship(internship: InternshipOpportunity): void
        +removeInternship(opportunityId: String): void
        +saveInternships(): void
        +generateInternshipId(): String
    }

    class CsvInternshipRepository {
        -IUserRepository userRepository
        +CsvInternshipRepository(userRepository: IUserRepository)
        +getAllInternships(): List~InternshipOpportunity~
        +getInternshipById(opportunityId: String): InternshipOpportunity
        +addInternship(internship: InternshipOpportunity): void
        +removeInternship(opportunityId: String): void
        +saveInternships(): void
        +generateInternshipId(): String
    }

    class IApplicationRepository {
        +getAllApplications(): List~Application~
        +getApplicationById(applicationId: String): Application
        +addApplication(application: Application): void
        +saveApplications(): void
        +generateApplicationId(): String
    }

    class CsvApplicationRepository {
        -IUserRepository userRepository
        -IInternshipRepository internshipRepository
        +CsvApplicationRepository(userRepository: IUserRepository, internshipRepository: IInternshipRepository)
        +getAllApplications(): List~Application~
        +getApplicationById(applicationId: String): Application
        +addApplication(application: Application): void
        +saveApplications(): void
        +generateApplicationId(): String
    }

    class UserService {
        -IUserRepository userRepository
        +login(userId: String, password: String): User
        +registerStudent(userId: String, name: String, password: String, yearOfStudy: int, major: String, gpa: double): bool
        +registerStaff(userId: String, name: String, password: String, department: String): bool
        +registerCompanyRep(userId: String, name: String, password: String, company: String, department: String, position: String, email: String): bool
        +approveCompanyRep(repId: String): void
        +saveUsers(): void throws IOException
        +isUserIdAvailable(userId: String, allowRejectedCompanyRep: bool): bool
        +getUserRepository(): IUserRepository
        -isValidUserId(userId: String): bool
        -isValidName(name: String): bool
        -isValidPassword(password: String): bool
        -isValidYearOfStudy(year: int): bool
        -isValidMajor(major: String): bool
        -isValidGpa(gpa: double): bool
        -isValidDepartment(department: String): bool
        -isValidCompanyName(company: String): bool
        -isValidPosition(position: String): bool
        -isValidEmail(email: String): bool
    }

    class InternshipService {
        -IInternshipRepository internshipRepository
        -IUserRepository userRepository
        +createInternship(userId: String, title: String, description: String, level: String, preferredMajor: String, openingDate: Date, closingDate: Date, maxSlots: int, minGPA: double): bool
        +approveInternship(opportunityId: String): void
        +rejectInternship(opportunityId: String): void
        +getAllInternships(): List~InternshipOpportunity~
        +getInternship(id: String): InternshipOpportunity
        +toggleVisibility(opportunityId: String, visible: bool): void
        +getInternshipRepository(): IInternshipRepository
    }



     class ApplicationService {
         -IApplicationRepository applicationRepository
         -IInternshipRepository internshipRepository
         -IUserRepository userRepository
         +applyForInternship(studentId: String, opportunityId: String): bool
         +approveApplication(applicationId: String): void
         +rejectApplication(applicationId: String): void
         +acceptInternship(applicationId: String): void
         +requestWithdrawal(applicationId: String): void
         +approveWithdrawal(applicationId: String): void
         +getAllApplicationsForStudent(studentId: String): List~Application~
         +getAllApplicationsForInternship(opportunityId: String): List~Application~
         +getApplicationsForStudent(studentId: String): List~Application~
         +getApplicationsForCompanyRep(repId: String): List~Application~
         +getApplicationsForInternship(opportunityId: String): List~Application~
         +getApplicationRepository(): IApplicationRepository
     }

    class IStudentApplicationService {
        <<interface>>
        +applyForInternship(studentId: String, opportunityId: String): bool
        +acceptInternship(applicationId: String): void
        +requestWithdrawal(applicationId: String): void
        +getAllApplicationsForStudent(studentId: String): List~Application~
        +getApplicationsForStudent(studentId: String): List~Application~
        +getApplicationRepository(): IApplicationRepository
    }

    class IStaffApplicationService {
        <<interface>>
        +approveApplication(applicationId: String): void
        +rejectApplication(applicationId: String): void
        +approveWithdrawal(applicationId: String): void
        +getApplicationsForCompanyRep(repId: String): List~Application~
        +getApplicationsForInternship(opportunityId: String): List~Application~
        +getAllApplicationsForInternship(opportunityId: String): List~Application~
    }

    class ServiceFactory {
        -CsvUserRepository userRepository
        -CsvInternshipRepository internshipRepository
        -CsvApplicationRepository applicationRepository
        -UserService userService
        -InternshipService internshipService
        -ApplicationService applicationService
        +initialize(): void
        +getUserRepository(): IUserRepository
        +getInternshipRepository(): IInternshipRepository
        +getApplicationRepository(): IApplicationRepository
        +getUserService(): IUserService
        +getInternshipService(): IInternshipService
        +getApplicationService(): IApplicationService
        <<Dependency Injection Container>>
    }

    class IMenuHandler {
        +showMenu(): void
    }

    class StudentMenuHandler {
        -Student student
        -InternshipService internshipService
        -ApplicationService applicationService
        -UserService userService
        -Scanner scanner
        -FilterManager filterManager
        +showMenu(): void
    }

    class CompanyRepMenuHandler {
        -CompanyRepresentative rep
        -InternshipService internshipService
        -ApplicationService applicationService
        -UserService userService
        -Scanner scanner
        -FilterManager filterManager
        +showMenu(): void
    }

    class CareerStaffMenuHandler {
        -CareerCenterStaff staff
        -UserService userService
        -InternshipService internshipService
        -ApplicationService applicationService
        -Scanner scanner
        -FilterManager filterManager
        +showMenu(): void
    }

     class InternshipPlacementSystem {
         -Scanner scanner
         -User currentUser
         -ServiceFactory serviceFactory
         +main(args: String[]): void
         -run(): void
         -showMainMenu(): void
         -showRegistrationMenu(): void
         -login(): void
         -registerStudent(): void
         -registerStaff(): void
         -registerCompanyRep(): void
         -showUserMenu(): void
         -getMenuHandler(): IMenuHandler
     }

    User <|-- Student
    User <|-- CompanyRepresentative
    User <|-- CareerCenterStaff

    User ..> PasswordUtil : uses

     IUserRepository <|.. CsvUserRepository
     IInternshipRepository <|.. CsvInternshipRepository
     IApplicationRepository <|.. CsvApplicationRepository

     IStudentApplicationService <|.. ApplicationService
     IStaffApplicationService <|.. ApplicationService

     IMenuHandler <|.. StudentMenuHandler
     IMenuHandler <|.. CompanyRepMenuHandler
     IMenuHandler <|.. CareerStaffMenuHandler

    CompanyRepresentative "1" --> "0..5" InternshipOpportunity : creates >
    Student "1" --> "0..3" Application : applies >
    InternshipOpportunity "1" --> "*" Application : has >
    Application "*" --> "1" Student
    Application "*" --> "1" InternshipOpportunity

    CsvUserRepository ..> User : loads/saves
    CsvUserRepository ..> Student : creates
    CsvUserRepository ..> CompanyRepresentative : creates
    CsvUserRepository ..> CareerCenterStaff : creates

    CsvInternshipRepository ..> InternshipOpportunity : manages
    CsvInternshipRepository ..> IUserRepository : depends on

    CsvApplicationRepository ..> Application : manages
    CsvApplicationRepository ..> IUserRepository : depends on
    CsvApplicationRepository ..> IInternshipRepository : depends on

    UserService ..> IUserRepository : uses
    InternshipService ..> IInternshipRepository : uses
    InternshipService ..> IUserRepository : uses
    ApplicationService ..> IApplicationRepository : uses
    ApplicationService ..> IInternshipRepository : uses
    ApplicationService ..> IUserRepository : uses

    StudentMenuHandler ..> InternshipService : uses
    StudentMenuHandler ..> ApplicationService : uses
    StudentMenuHandler ..> UIHelper : uses
    CompanyRepMenuHandler ..> InternshipService : uses
    CompanyRepMenuHandler ..> ApplicationService : uses
    CompanyRepMenuHandler ..> UIHelper : uses
    CareerStaffMenuHandler ..> UserService : uses
    CareerStaffMenuHandler ..> InternshipService : uses
    CareerStaffMenuHandler ..> ApplicationService : uses
    CareerStaffMenuHandler ..> UIHelper : uses

    InternshipPlacementSystem ..> ServiceFactory : uses
    ServiceFactory ..> CsvUserRepository : creates
    ServiceFactory ..> CsvInternshipRepository : creates
    ServiceFactory ..> CsvApplicationRepository : creates
    ServiceFactory ..> UserService : creates
    ServiceFactory ..> InternshipService : creates
    ServiceFactory ..> ApplicationService : creates

    InternshipPlacementSystem ..> UIHelper : uses
    InternshipPlacementSystem ..> MajorCatalog : uses
    CompanyRepMenuHandler ..> MajorCatalog : uses

    Student ..> IInternshipRepository : uses
    Student ..> IApplicationRepository : uses
    CompanyRepresentative ..> IInternshipRepository : uses
    CompanyRepresentative ..> IApplicationRepository : uses
    CareerCenterStaff ..> IUserRepository : uses
    CareerCenterStaff ..> IInternshipRepository : uses
    CareerCenterStaff ..> IApplicationRepository : uses

     InternshipPlacementSystem ..> IUserRepository : injects
     InternshipPlacementSystem ..> IInternshipRepository : injects
     InternshipPlacementSystem ..> IApplicationRepository : injects
     InternshipPlacementSystem ..> UserService : injects
     InternshipPlacementSystem ..> InternshipService : injects
     InternshipPlacementSystem ..> ApplicationService : injects
     InternshipPlacementSystem ..> IMenuHandler : uses

    CareerCenterStaff ..> Report : creates
    Report ..> InternshipOpportunity : contains

    ReportManager ..> IInternshipRepository : uses
    ReportManager ..> IApplicationRepository : uses
    CareerStaffMenuHandler ..> ReportManager : uses

    Statistics ..> IApplicationRepository : uses
    Statistics ..> IInternshipRepository : uses
    Statistics ..> IUserRepository : uses
    Statistics ..> Student : analyzes
    Statistics ..> CompanyRepresentative : analyzes
    Statistics ..> InternshipOpportunity : analyzes
    Statistics ..> Application : analyzes

```

## UML Sequence Diagrams

### Secure Login with Password Hashing

```mermaid
sequenceDiagram
    participant user
    participant InternshipPlacementSystem
    participant UserService
    participant IUserRepository
    participant User
    participant PasswordUtil

    user->>InternshipPlacementSystem: login(userID, password)
    InternshipPlacementSystem->>UserService: login(userID, password)
    UserService->>IUserRepository: getUserById(userID)
    IUserRepository-->>UserService: User
    UserService->>User: login(password)
    User->>PasswordUtil: verifyPassword(password, hash, salt)
    PasswordUtil-->>User: valid/invalid
    User-->>UserService: success/failure
    UserService-->>InternshipPlacementSystem: User or null
    InternshipPlacementSystem-->>user: login result

```

### Service-Based Application Submission

```mermaid
sequenceDiagram
    participant student
    participant StudentMenuHandler
    participant ApplicationService
    participant IApplicationRepository
    participant IInternshipRepository
    participant IUserRepository

    student->>StudentMenuHandler: applyForInternship(opportunityID)
    StudentMenuHandler->>ApplicationService: applyForInternship(studentID, opportunityID)
    ApplicationService->>IUserRepository: getUserById(studentID)
    IUserRepository-->>ApplicationService: Student
    ApplicationService->>IInternshipRepository: getInternshipById(opportunityID)
    IInternshipRepository-->>ApplicationService: InternshipOpportunity
    ApplicationService->>ApplicationService: validate eligibility
    ApplicationService->>IApplicationRepository: addApplication(Application)
    IApplicationRepository-->>ApplicationService: added
    ApplicationService->>IApplicationRepository: saveApplications()
    ApplicationService-->>StudentMenuHandler: success
    StudentMenuHandler-->>student: application submitted

```

### System Initialization with Proper Dependency Order

```mermaid
sequenceDiagram
    participant Main
    participant InternshipPlacementSystem
    participant CsvUserRepository
    participant CsvInternshipRepository
    participant CsvApplicationRepository
    participant UserService
    participant InternshipService
    participant ApplicationService

    Main->>InternshipPlacementSystem: new InternshipPlacementSystem()
    InternshipPlacementSystem->>InternshipPlacementSystem: initializeRepositories()
    InternshipPlacementSystem->>CsvUserRepository: new CsvUserRepository(null, null)
    InternshipPlacementSystem->>CsvInternshipRepository: new CsvInternshipRepository(userRepository)
    InternshipPlacementSystem->>CsvApplicationRepository: new CsvApplicationRepository(userRepository, internshipRepository)
    InternshipPlacementSystem->>CsvUserRepository: setInternshipRepository(internshipRepository)
    InternshipPlacementSystem->>CsvUserRepository: setApplicationRepository(applicationRepository)
    InternshipPlacementSystem->>InternshipPlacementSystem: initializeServices()
    InternshipPlacementSystem->>UserService: new UserService(userRepository, internshipRepository, applicationRepository)
    InternshipPlacementSystem->>InternshipService: new InternshipService(internshipRepository, userRepository)
    InternshipPlacementSystem->>ApplicationService: new ApplicationService(applicationRepository, internshipRepository, userRepository)
    InternshipPlacementSystem-->>Main: initialized system

```

### User Registration with Comprehensive Validation

```mermaid
sequenceDiagram
    participant user
    participant InternshipPlacementSystem
    participant UserService
    participant IUserRepository
    participant PasswordUtil

    user->>InternshipPlacementSystem: registerStudent(details)
    InternshipPlacementSystem->>UserService: registerStudent(userId, name, password, year, major, gpa)
    UserService->>UserService: isValidUserId(userId)
    UserService->>UserService: isValidName(name)
    UserService->>UserService: isValidPassword(password)
    UserService->>UserService: isValidYearOfStudy(year)
    UserService->>UserService: isValidMajor(major)
    UserService->>UserService: isValidGpa(gpa)
    alt all validations pass
        UserService->>IUserRepository: getUserById(userId)
        IUserRepository-->>UserService: null (available)
        UserService->>Student: new Student(userId, name, password, ...)
        Student->>PasswordUtil: generateSalt()
        PasswordUtil-->>Student: salt
        Student->>PasswordUtil: hashPassword(password, salt)
        PasswordUtil-->>Student: hash
        Student->>Student: store hash and salt
        UserService->>IUserRepository: addUser(Student)
        UserService->>IUserRepository: saveUsers()
        UserService-->>InternshipPlacementSystem: success
        InternshipPlacementSystem-->>user: registration successful
    else validation fails
        UserService-->>InternshipPlacementSystem: validation error
        InternshipPlacementSystem-->>user: specific validation error message
    end

```

### User Login via Services

```mermaid
sequenceDiagram
    participant user
    participant InternshipPlacementSystem
    participant UserService
    participant IUserRepository

    user->>InternshipPlacementSystem: login(userId, password)
    InternshipPlacementSystem->>UserService: login(userId, password)
    UserService->>IUserRepository: getUserById(userId)
    IUserRepository-->>UserService: User
    UserService->>User: login(password)
    User-->>UserService: success/failure
    UserService-->>InternshipPlacementSystem: User or null
    InternshipPlacementSystem-->>user: login result

```

### Creating Internship via Services

```mermaid
sequenceDiagram
    participant companyRep
    participant CompanyRepMenuHandler
    participant InternshipService
    participant IInternshipRepository
    participant IUserRepository

    companyRep->>CompanyRepMenuHandler: createInternship(details)
    CompanyRepMenuHandler->>InternshipService: createInternship(userId, title, description, level, major, dates, slots, gpa)
    InternshipService->>IUserRepository: getUserById(userId)
    IUserRepository-->>InternshipService: CompanyRepresentative
    InternshipService->>InternshipService: validate approval and limits
    InternshipService->>IInternshipRepository: addInternship(InternshipOpportunity)
    InternshipService-->>CompanyRepMenuHandler: success
    CompanyRepMenuHandler-->>companyRep: internship created

```

### Approving Application via Services

```mermaid
sequenceDiagram
    participant companyRep
    participant CompanyRepMenuHandler
    participant ApplicationService
    participant IApplicationRepository

    companyRep->>CompanyRepMenuHandler: approveApplication(applicationId)
    CompanyRepMenuHandler->>ApplicationService: approveApplication(applicationId)
    ApplicationService->>IApplicationRepository: getApplicationById(applicationId)
    IApplicationRepository-->>ApplicationService: Application
    ApplicationService->>Application: updateStatus("Successful")
    ApplicationService->>IApplicationRepository: saveApplications()
    ApplicationService-->>CompanyRepMenuHandler: success
    CompanyRepMenuHandler-->>companyRep: application approved

```

### Accepting Internship via Services

```mermaid
sequenceDiagram
    participant student
    participant StudentMenuHandler
    participant ApplicationService
    participant IApplicationRepository
    participant IInternshipRepository

    student->>StudentMenuHandler: acceptInternship(applicationId)
    StudentMenuHandler->>ApplicationService: acceptInternship(applicationId)
    ApplicationService->>IApplicationRepository: getApplicationById(applicationId)
    IApplicationRepository-->>ApplicationService: Application
    ApplicationService->>Application: updateStatus("Confirmed")
    ApplicationService->>IApplicationRepository: getAllApplications()
    ApplicationService->>ApplicationService: withdraw other applications
    ApplicationService->>IInternshipRepository: getInternshipById(opportunityId)
    IInternshipRepository-->>ApplicationService: InternshipOpportunity
    ApplicationService->>ApplicationService: check if slots filled
    ApplicationService->>InternshipOpportunity: setStatus("Filled")
    ApplicationService->>IApplicationRepository: saveApplications()
    ApplicationService-->>StudentMenuHandler: success
    StudentMenuHandler-->>student: internship accepted

```

### Requesting Withdrawal via Services

```mermaid
sequenceDiagram
    participant student
    participant StudentMenuHandler
    participant ApplicationService
    participant IApplicationRepository

    student->>StudentMenuHandler: requestWithdrawal(applicationId)
    StudentMenuHandler->>ApplicationService: requestWithdrawal(applicationId)
    ApplicationService->>IApplicationRepository: getApplicationById(applicationId)
    IApplicationRepository-->>ApplicationService: Application
    ApplicationService->>Application: updateStatus("Withdrawal Requested")
    ApplicationService->>IApplicationRepository: saveApplications()
    ApplicationService-->>StudentMenuHandler: success
    StudentMenuHandler-->>student: withdrawal requested

```

### Approving Withdrawal via Services

```mermaid
sequenceDiagram
    participant careerStaff
    participant CareerStaffMenuHandler
    participant ApplicationService
    participant IApplicationRepository

    careerStaff->>CareerStaffMenuHandler: approveWithdrawal(applicationId)
    CareerStaffMenuHandler->>ApplicationService: approveWithdrawal(applicationId)
    ApplicationService->>IApplicationRepository: getApplicationById(applicationId)
    IApplicationRepository-->>ApplicationService: Application
    ApplicationService->>Application: updateStatus("Withdrawn")
    ApplicationService->>IApplicationRepository: saveApplications()
    ApplicationService-->>CareerStaffMenuHandler: success
    CareerStaffMenuHandler-->>careerStaff: withdrawal approved

```

### Viewing Statistics via Services

```mermaid
sequenceDiagram
    participant user
    participant MenuHandler
    participant Statistics

    user->>MenuHandler: viewStatistics()
    MenuHandler->>Statistics: displayUserStatistics(user)
    Statistics->>Statistics: calculate statistics
    Statistics-->>MenuHandler: statistics data
    MenuHandler-->>user: display statistics

```

### Generating Reports via Services

```mermaid
sequenceDiagram
    participant careerStaff
    participant CareerStaffMenuHandler
    participant Report
    participant IInternshipRepository

    careerStaff->>CareerStaffMenuHandler: generateReports(filters)
    CareerStaffMenuHandler->>IInternshipRepository: getAllInternships()
    IInternshipRepository-->>CareerStaffMenuHandler: internships
    CareerStaffMenuHandler->>Report: new Report(filteredInternships, filters)
    CareerStaffMenuHandler->>Report: displayReport()
    Report-->>CareerStaffMenuHandler: report displayed
    CareerStaffMenuHandler-->>careerStaff: report generated

```

### Menu Navigation with Handlers

```mermaid
sequenceDiagram
    participant user
    participant InternshipPlacementSystem
    participant IMenuHandler

    user->>InternshipPlacementSystem: login successful
    InternshipPlacementSystem->>InternshipPlacementSystem: getMenuHandler()
    InternshipPlacementSystem->>IMenuHandler: new Handler(user, services, scanner)
    InternshipPlacementSystem->>IMenuHandler: showMenu()
    IMenuHandler->>user: display menu options
    user->>IMenuHandler: select option
    IMenuHandler->>IMenuHandler: execute action
    IMenuHandler->>InternshipPlacementSystem: logout or continue
    InternshipPlacementSystem-->>user: menu interaction complete

```

### Approving Company Representative Account

```mermaid
sequenceDiagram
    participant companyRep
    participant InternshipPlacementSystem
    participant CareerStaffMenuHandler
    participant UserService
    participant IUserRepository

    companyRep->>InternshipPlacementSystem: registerCompanyRep(details)
    InternshipPlacementSystem->>UserService: registerCompanyRep(userId, name, password, company, department, position, email)
    UserService->>IUserRepository: addUser(CompanyRepresentative)
    UserService->>IUserRepository: saveUsers()
    UserService-->>InternshipPlacementSystem: success
    InternshipPlacementSystem-->>companyRep: pending approval

    careerStaff->>CareerStaffMenuHandler: approveCompanyRep(repId)
    CareerStaffMenuHandler->>UserService: approveCompanyRep(repId)
    UserService->>IUserRepository: getUserById(repId)
    IUserRepository-->>UserService: CompanyRepresentative
    UserService->>CompanyRepresentative: setApproved(true)
    UserService->>IUserRepository: saveUsers()
    UserService-->>CareerStaffMenuHandler: success
    CareerStaffMenuHandler-->>careerStaff: account approved

```

### Approving Internship by Staff

```mermaid
sequenceDiagram
    participant careerStaff
    participant CareerStaffMenuHandler
    participant InternshipService
    participant IInternshipRepository

    careerStaff->>CareerStaffMenuHandler: approveInternship(opportunityId)
    CareerStaffMenuHandler->>InternshipService: approveInternship(opportunityId)
    InternshipService->>IInternshipRepository: getInternshipById(opportunityId)
    IInternshipRepository-->>InternshipService: InternshipOpportunity
    InternshipService->>InternshipOpportunity: setStatus("Approved")
    InternshipService-->>CareerStaffMenuHandler: success
    CareerStaffMenuHandler-->>careerStaff: internship approved

```

### Batch Application Submission

```mermaid
sequenceDiagram
    participant student
    participant StudentMenuHandler
    participant ApplicationService
    participant IApplicationRepository
    participant IInternshipRepository
    participant IUserRepository

    student->>StudentMenuHandler: applyForInternships([opportunityIDs])
    loop for each opportunityID
        StudentMenuHandler->>ApplicationService: applyForInternship(studentID, opportunityID)
        ApplicationService->>IUserRepository: getUserById(studentID)
        IUserRepository-->>ApplicationService: Student
        ApplicationService->>IInternshipRepository: getInternshipById(opportunityID)
        IInternshipRepository-->>ApplicationService: InternshipOpportunity
        ApplicationService->>ApplicationService: validate eligibility
        alt valid
            ApplicationService->>IApplicationRepository: addApplication(Application)
        end
    end
    ApplicationService->>IApplicationRepository: saveApplications()
    ApplicationService-->>StudentMenuHandler: results
    StudentMenuHandler-->>student: batch application results

```

### Secure Password Change with Hashing and Persistence

```mermaid
sequenceDiagram
    participant user
    participant MenuHandler
    participant User
    participant PasswordUtil
    participant UserService
    participant IUserRepository

    user->>MenuHandler: changePassword()
    MenuHandler->>user: prompt current password
    user->>MenuHandler: enter current password
    MenuHandler->>User: verifyPassword(currentPassword)
    User->>PasswordUtil: verifyPassword(currentPassword, storedHash, storedSalt)
    PasswordUtil-->>User: valid/invalid
    User-->>MenuHandler: valid
    MenuHandler->>user: prompt new password
    user->>MenuHandler: enter new password
    MenuHandler->>user: confirm new password
    user->>MenuHandler: confirm password
    MenuHandler->>MenuHandler: validate passwords match and not same as current
    MenuHandler->>MenuHandler: save old passwordHash and salt
    MenuHandler->>User: changePassword(newPassword)
    User->>PasswordUtil: generateSalt()
    PasswordUtil-->>User: newSalt
    User->>PasswordUtil: hashPassword(newPassword, newSalt)
    PasswordUtil-->>User: newHash
    User->>User: store newHash and newSalt
    User-->>MenuHandler: password changed
    MenuHandler->>UserService: saveUsers()
    UserService->>IUserRepository: saveUsers()
    IUserRepository-->>UserService: success or IOException
    UserService-->>MenuHandler: success or exception
    alt success
        MenuHandler-->>user: password change successful
    else exception
        MenuHandler->>User: setPasswordHash(oldHash)
        MenuHandler->>User: setSalt(oldSalt)
        MenuHandler-->>user: failed to save password change
    end

```

### Logout Process

```mermaid
sequenceDiagram
    participant user
    participant MenuHandler
    participant InternshipPlacementSystem
    participant User

    user->>MenuHandler: select logout
    MenuHandler->>User: logout()
    User-->>MenuHandler: logged out
    MenuHandler->>InternshipPlacementSystem: set currentUser to null
    InternshipPlacementSystem-->>MenuHandler: session ended
    MenuHandler-->>user: logged out successfully

```

### Filter Management

```mermaid
sequenceDiagram
    participant user
    participant MenuHandler
    participant FilterSettings

    user->>MenuHandler: manageFilters()
    MenuHandler->>user: display current filters
    user->>MenuHandler: set status filter
    MenuHandler->>FilterSettings: setStatusFilter(value)
    user->>MenuHandler: set level filter
    MenuHandler->>FilterSettings: setLevelFilter(value)
    user->>MenuHandler: set major filter
    MenuHandler->>FilterSettings: setMajorFilter(value)
    user->>MenuHandler: set GPA filter
    MenuHandler->>FilterSettings: setMinGPAFilter(value)
    user->>MenuHandler: set sort by
    MenuHandler->>FilterSettings: setSortBy(value)
    MenuHandler->>FilterSettings: toString()
    FilterSettings-->>MenuHandler: filter summary
    MenuHandler-->>user: filters updated

```

### Optimized Queue Processing

```mermaid
sequenceDiagram
    participant CareerStaff
    participant CareerCenterStaff
    participant ApplicationService
    participant IApplicationRepository
    participant IInternshipRepository

    CareerStaff->>CareerCenterStaff: processWithdrawal(applicationId, approve=true)
    CareerCenterStaff->>ApplicationService: approveWithdrawal(applicationId)
    ApplicationService->>IApplicationRepository: getApplicationById(applicationId)
    IApplicationRepository-->>ApplicationService: Application
    ApplicationService->>Application: updateStatus("Withdrawn")
    ApplicationService->>ApplicationService: check if confirmed slot freed
    ApplicationService->>CareerCenterStaff: processQueue(internship)
    CareerCenterStaff->>IApplicationRepository: getAllApplications()
    IApplicationRepository-->>CareerCenterStaff: all applications
    CareerCenterStaff->>CareerCenterStaff: filter by internshipId
    CareerCenterStaff->>CareerCenterStaff: count confirmed applications
    loop while slots available and queue not empty
        CareerCenterStaff->>CareerCenterStaff: find oldest queued application (by appliedDate/queuedDate)
        CareerCenterStaff->>Application: updateStatus("Confirmed")
        CareerCenterStaff->>CareerCenterStaff: withdraw other student applications
        CareerCenterStaff->>IApplicationRepository: saveApplications()
    end
    CareerCenterStaff->>ApplicationService: queue processing complete
    ApplicationService->>IApplicationRepository: saveApplications()
    ApplicationService-->>CareerCenterStaff: success
    CareerCenterStaff-->>CareerStaff: withdrawal processed, queue updated

```

### Viewing Applications for Company Reps

```mermaid
sequenceDiagram
    participant companyRep
    participant CompanyRepMenuHandler
    participant ApplicationService
    participant IApplicationRepository

    companyRep->>CompanyRepMenuHandler: viewApplications()
    CompanyRepMenuHandler->>ApplicationService: getApplicationsForCompanyRep(repId)
    ApplicationService->>IApplicationRepository: getAllApplications()
    IApplicationRepository-->>ApplicationService: all applications
    ApplicationService->>ApplicationService: filter by repId
    ApplicationService-->>CompanyRepMenuHandler: rep applications
    CompanyRepMenuHandler-->>companyRep: display applications

```

### Toggling Internship Visibility

```mermaid
sequenceDiagram
    participant companyRep
    participant CompanyRepMenuHandler
    participant InternshipService
    participant IInternshipRepository

    companyRep->>CompanyRepMenuHandler: toggleVisibility(opportunityId, visible)
    CompanyRepMenuHandler->>InternshipService: toggleVisibility(opportunityId, visible)
    InternshipService->>IInternshipRepository: getInternshipById(opportunityId)
    IInternshipRepository-->>InternshipService: InternshipOpportunity
    InternshipService->>InternshipOpportunity: setVisibility(visible)
    InternshipService-->>CompanyRepMenuHandler: success
    CompanyRepMenuHandler-->>companyRep: visibility toggled

```

### Batch Internship Approval

```mermaid
sequenceDiagram
    participant careerStaff
    participant CareerStaffMenuHandler
    participant InternshipService
    participant IInternshipRepository

    careerStaff->>CareerStaffMenuHandler: processInternships([opportunityIDs])
    loop for each opportunityID
        CareerStaffMenuHandler->>InternshipService: approveInternship(opportunityID)
        InternshipService->>IInternshipRepository: getInternshipById(opportunityID)
        IInternshipRepository-->>InternshipService: InternshipOpportunity
        InternshipService->>InternshipOpportunity: setStatus("Approved")
        InternshipService-->>CareerStaffMenuHandler: success
    end
    CareerStaffMenuHandler->>CareerStaffMenuHandler: display batch summary
    CareerStaffMenuHandler-->>careerStaff: batch approval complete

```
