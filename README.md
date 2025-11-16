# sc2002-oop-assignment

An Internship Placement System, designed with Object-Oriented Principles

## Features

- **User Management**: Three user types (Students, Company Representatives, Career Center Staff)
- **Internship Management**: Create, approve, and manage internship opportunities
- **Application Process**: Students can apply for internships, track applications, and accept offers
- **Approval Workflows**: Career center staff approve company reps and internships
- **Reporting**: Generate filtered reports on internship opportunities
- **Data Persistence**: CSV-based storage for user data
- **Batch Processing**: Space-separated IDs for mass operations (apply, process applications, toggle visibility)
- **Waitlist System**: Automatic queue management for filled internships with FIFO processing
- **Smart Status Management**: Automatic status updates when slots become available

## Recent Enhancements (v2.0.0)

### Flexible Withdrawal System

- **Multi-Stage Withdrawal**: Students can request withdrawal at any stage (Pending, Successful, or Confirmed)
- **Smart Processing**: Career staff approval intelligently handles different withdrawal scenarios
- **Automatic Queue Advancement**: When confirmed placements are withdrawn, queued students automatically advance
- **Status Transparency**: UI displays current application status for informed withdrawal decisions

### User Experience

- **Pre-Display Lists**: Students see eligible internships before applying, successful/confirmed applications before accepting/withdrawing
- **Batch Operations**: Space-separated IDs for applying to multiple internships and processing applications
- **Clean Professional UI**: Removed all emojis, replaced with text markers ([SUCCESS], [FAILED])
- **Password Security**: Prevents users from setting new password same as current

### Advanced Features

- **Waitlist Queue**: Students added to queue when internship is full, automatically confirmed when slots open
- **Manual Withdrawal Tracking**: Prevents reapplication to internships students manually withdrew from
- **Auto Status Updates**: Internships automatically change from "Filled" to "Approved" when slots become available
- **Queue Processing**: Automatic confirmation of queued applications in FIFO order

## System Architecture

### Classes

- `User`: Base class for all users
- `Student`: Extends User, handles student operations with enhanced validation
- `CompanyRepresentative`: Extends User, manages company internships
- `CareerCenterStaff`: Extends User, admin functions with queue processing
- `InternshipOpportunity`: Represents internship postings
- `Application`: Manages student applications with manual withdrawal tracking
- `Database`: Handles data persistence and CSV operations
- `Report`: Generates filtered reports
- `InternshipPlacementSystem`: Main CLI application with batch processing
- `UIHelper`: Centralized UI formatting utilities

### Data Storage

- Users are loaded from CSV files:
  - `sample_student_list.csv`
  - `sample_staff_list.csv`
  - `sample_company_representative_list.csv`
- Internships and applications are stored in memory during runtime

## Usage

### Compile and Run

```bash
javac *.java
java InternshipPlacementSystem
```

### Default Login Credentials

All users have default password: `password`

#### Students

- U2310001A (Tan Wei Ling, Computer Science, Year 2)
- U2310002B (Ng Jia Hao, Data Science & AI, Year 3)
- U2310003C (Lim Yi Xuan, Computer Engineering, Year 4)
- U2310004D (Chong Zhi Hao, Information Engineering & Media, Year 1)
- U2310005E (Wong Shu Hui, Computer Science, Year 3)

#### Career Center Staff

- sng001 (Dr. Sng Hui Lin)
- tan002 (Mr. Tan Boon Kiat)
- lee003 (Ms. Lee Mei Ling)

## Workflow

1. **Company Representatives** must be approved by Career Center Staff before creating internships
2. **Internships** start as "Pending" and must be approved by Career Center Staff
3. **Students** can only apply to approved, visible internships matching their major
4. **Applications** go through approval/rejection workflow
5. **Students** can accept successful offers and request withdrawals at any stage (Pending, Successful, or Confirmed)
6. **Waitlist Queue** automatically manages students when internships are full
7. **Withdrawal Processing** by Career Staff triggers queue advancement for confirmed placements

## Key Features Explained

### Withdrawal System

- **Pending Stage**: Student can withdraw before company processes application
- **Successful Stage**: Student can withdraw after approval but before accepting
- **Confirmed Stage**: Student can withdraw after accepting placement
- All withdrawals must be approved by Career Center Staff
- Confirmed withdrawals trigger automatic queue processing

### Batch Processing

- Apply to multiple internships: `INT0001 INT0002 INT0003`
- Process multiple applications: `APP0001 APP0002 APP0003`
- Toggle multiple visibility settings: `INT0001 INT0002`

### Waitlist Queue

- Automatically activated when internship reaches max slots
- Students added to queue in FIFO order
- Queue automatically processed when slots become available
- Queued students notified of automatic confirmation

### Manual Withdrawal Tracking

- Students who manually withdraw cannot reapply to same internship
- Prevents abuse of placement system
- Tracked via `manuallyWithdrawn` flag in Application class

## Documentation

- **CHANGELOG.md**: Complete version history and feature additions
- **IMPROVEMENTS.md**: Detailed enhancement descriptions beyond base requirements
- **TEST_VERIFICATION.md**: Validation of all 20 specification test cases
- **README.md**: This file - system overview and usage guide

# UML Class Diagram

```mermaid
classDiagram
    class User {
        -String userID
        -String name
        -String password
        -boolean isLoggedIn
        +login(password: String): bool
        +logout(): void
        +changePassword(newPassword: String): void
        +getUserID(): String
        +getName(): String
        +isLoggedIn(): bool
    }

    class Student {
        -int yearOfStudy
        -String major
        +viewEligibleInternships(): List~InternshipOpportunity~
        +applyForInternship(opportunityID: String): bool
        +viewApplications(): List~Application~
        +acceptInternship(applicationID: String): void
        +requestWithdrawal(applicationID: String): void
        +getYearOfStudy(): int
        +getMajor(): String
    }

    class CompanyRepresentative {
        -String companyName
        -String department
        -String position
        -boolean isApproved
        +createInternship(title: String, description: String, level: String, preferredMajor: String, openingDate: Date, closingDate: Date, maxSlots: int): bool
        +viewApplications(): List~Application~
        +viewApplications(opportunityID: String): List~Application~
        +approveApplication(applicationID: String): void
        +rejectApplication(applicationID: String): void
        +toggleVisibility(opportunityID: String, visible: bool): void
        +getCompanyName(): String
        +getDepartment(): String
        +getPosition(): String
        +isApproved(): bool
        +setApproved(approved: bool): void
    }

    class CareerCenterStaff {
        -String staffDepartment
        +approveCompanyRep(repID: String): void
        +approveInternship(opportunityID: String): void
        +rejectInternship(opportunityID: String): void
        +approveWithdrawal(applicationID: String): void
        +rejectWithdrawal(applicationID: String): void
        +generateReports(filters: Map~String,String~): Report
        +getStaffDepartment(): String
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
        -CompanyRepresentative createdBy
        +isOpen(): bool
        +isVisible(): bool
        +getOpportunityID(): String
        +getTitle(): String
        +getDescription(): String
        +getLevel(): String
        +getPreferredMajor(): String
        +getOpeningDate(): Date
        +getClosingDate(): Date
        +getStatus(): String
        +setStatus(status: String): void
        +getMaxSlots(): int
        +isVisibility(): bool
        +setVisibility(visibility: bool): void
        +getCreatedBy(): CompanyRepresentative
    }

    class Application {
        -String applicationID
        -Student applicant
        -InternshipOpportunity opportunity
        -String status
        -Date appliedDate
        -boolean manuallyWithdrawn
        +updateStatus(newStatus: String): void
        +getApplicationID(): String
        +getApplicant(): Student
        +getOpportunity(): InternshipOpportunity
        +getStatus(): String
        +getAppliedDate(): Date
        +isManuallyWithdrawn(): boolean
        +setManuallyWithdrawn(withdrawn: boolean): void
    }

    class Database {
        -static List~User~ users
        -static List~InternshipOpportunity~ internships
        -static List~Application~ applications
        -static int applicationCounter
        -static int internshipCounter
        +loadUsersFromCSV(): void
        +loadStudents(): void
        +loadStaff(): void
        +loadCompanyRepresentatives(): void
        +saveData(): void
        +saveCompanyRepresentatives(): void
        +getUser(userID: String): User
        +getUsers(): List~User~
        +addUser(user: User): void
        +getInternship(opportunityID: String): InternshipOpportunity
        +getInternships(): List~InternshipOpportunity~
        +addInternship(opportunity: InternshipOpportunity): void
        +getApplication(applicationID: String): Application
        +getApplications(): List~Application~
        +addApplication(application: Application): void
        +generateApplicationID(): String
        +generateInternshipID(): String
    }

    class Report {
        -List~InternshipOpportunity~ opportunities
        -Map~String,String~ filters
        +displayReport(): void
        +getOpportunities(): List~InternshipOpportunity~
        +getFilters(): Map~String,String~
    }

    class InternshipPlacementSystem {
        -static Scanner scanner
        -static User currentUser
        +main(args: String[]): void
        -showMainMenu(): void
        -login(): void
        -showUserMenu(): void
        -showStudentMenu(): void
        -showCompanyRepMenu(): void
        -showCareerStaffMenu(): void
        -logout(): void
        -viewEligibleInternships(student: Student): void
        -applyForInternship(student: Student): void
        -viewMyApplications(student: Student): void
        -acceptInternship(student: Student): void
        -requestWithdrawal(student: Student): void
        -createInternship(rep: CompanyRepresentative): void
        -viewApplications(rep: CompanyRepresentative): void
        -approveApplication(rep: CompanyRepresentative): void
        -rejectApplication(rep: CompanyRepresentative): void
        -toggleVisibility(rep: CompanyRepresentative): void
        -approveCompanyRep(staff: CareerCenterStaff): void
        -approveInternship(staff: CareerCenterStaff): void
        -rejectInternship(staff: CareerCenterStaff): void
        -approveWithdrawal(staff: CareerCenterStaff): void
        -rejectWithdrawal(staff: CareerCenterStaff): void
        -generateReports(staff: CareerCenterStaff): void
    }

    User <|-- Student
    User <|-- CompanyRepresentative
    User <|-- CareerCenterStaff

    CompanyRepresentative "1" --> "0..5" InternshipOpportunity : creates >
    Student "1" --> "0..3" Application : applies >
    InternshipOpportunity "1" --> "*" Application : has >
    Application "*" --> "1" Student
    Application "*" --> "1" InternshipOpportunity

    Database ..> User : loads/saves
    Database ..> Student : creates
    Database ..> CompanyRepresentative : creates
    Database ..> CareerCenterStaff : creates
    Database ..> InternshipOpportunity : manages
    Database ..> Application : manages

    InternshipPlacementSystem ..> Database : uses
    InternshipPlacementSystem ..> User : manages
    InternshipPlacementSystem ..> Student : interacts
    InternshipPlacementSystem ..> CompanyRepresentative : interacts
    InternshipPlacementSystem ..> CareerCenterStaff : interacts
    InternshipPlacementSystem ..> InternshipOpportunity : displays
    InternshipPlacementSystem ..> Application : displays
    InternshipPlacementSystem ..> Report : generates

    CareerCenterStaff ..> Report : creates
    Report ..> InternshipOpportunity : contains

```

# UML Sequence Diagrams

## Apply for Intern

```mermaid
sequenceDiagram

    participant student

    participant system

    participant Database

    participant applicationDB



    student->>system: login(userID, password)

    system-->>student: loginSuccess()



    student->>system: viewEligibleInternships()

    system->>Database: getInternships()

    Database-->>system: List~InternshipOpportunity~

    system->>student: showList()



    student->>system: applyForInternship(opportunityID)

    system->>Database: getInternship(opportunityID)

    Database-->>system: InternshipOpportunity



    system->>InternshipOpportunity: isOpen()

    system->>InternshipOpportunity: isVisible()



    alt Eligible and Visible

        system->>Database: addApplication(Application)

        Database-->>system: applicationCreated

        system-->>student: applicationSuccess()

    else Not eligible

        system-->>student: showError("Not eligible")

    end
```

## Create Internship

```mermaid
sequenceDiagram
    participant companyRep
    participant system
    participant Database
    participant careerStaff

    companyRep->>system: login()
    system-->>companyRep: loginSuccess()

    companyRep->>system: createInternship(opportunityDetails)
    system->>Database: addInternship(InternshipOpportunity)
    Database-->>system: opportunitySaved
    system-->>companyRep: notify("Submitted for approval")

    careerStaff->>system: login()
    system-->>careerStaff: loginSuccess()

    careerStaff->>system: approveInternship(opportunityID)
    system->>Database: getInternship(opportunityID)
    Database-->>system: InternshipOpportunity
    system->>InternshipOpportunity: setStatus("Approved")
    system-->>companyRep: notify("Internship Approved")

```

## Accept Internship

```mermaid
sequenceDiagram
    participant student
    participant system
    participant Database

    student->>system: acceptInternship(applicationID)
    system->>Database: getApplication(applicationID)
    Database-->>system: application

    system->>Application: updateStatus("Confirmed")
    system->>Database: getApplications()
    Database-->>system: allApplications
    loop withdraw other applications
        system->>Application: updateStatus("Withdrawn")
    end

    system->>Database: getApplications()
    Database-->>system: opportunityApplications
    loop count confirmed applications
        system->>Application: getStatus()
    end
    alt slots filled
        system->>InternshipOpportunity: setStatus("Filled")
    end
    system-->>student: placementConfirmed

```

## Withdraw from Application

```mermaid
sequenceDiagram

    participant student

    participant system

    participant careerStaff

    participant Database



    student->>system: requestWithdrawal(applicationID)

    system->>Application: updateStatus("Withdrawal Requested")

    system->>careerStaff: notifyWithdrawalRequest(applicationID)



    careerStaff->>system: reviewWithdrawalRequest(applicationID)

    system->>Database: getApplication(applicationID)

    Database-->>system: application



    careerStaff->>system: approveWithdrawal(applicationID)

    system->>Application: updateStatus("Withdrawn")

    system-->>student: withdrawalApproved()
```

## Generate Report

```mermaid
sequenceDiagram
    participant careerStaff
    participant system
    participant Database

    careerStaff->>system: login()
    system-->>careerStaff: loginSuccess()

    careerStaff->>system: generateReports(filters)
    system->>Database: getInternships()
    Database-->>system: allOpportunities
    loop filter opportunities
        system->>InternshipOpportunity: getStatus()
        system->>InternshipOpportunity: getLevel()
        system->>InternshipOpportunity: getPreferredMajor()
    end
    system->>Report: Report(filteredOpportunities, filters)
    system->>Report: displayReport()
    system-->>careerStaff: displayReport

```

## Approve Application

```mermaid
sequenceDiagram
    participant companyRep
    participant system
    participant Database
    participant student

    companyRep->>system: viewApplications(opportunityID)
    system->>Database: getApplications()
    Database-->>system: allApplications
    loop filter by opportunity
        system->>Application: getOpportunity()
    end
    system-->>companyRep: displayApplications

    companyRep->>system: approveApplication(applicationID)
    system->>Database: getApplication(applicationID)
    Database-->>system: application
    system->>Application: updateStatus("Successful")
    system-->>student: notify("Application Successful")

```

## Approve Rep Acct

```mermaid
sequenceDiagram
    participant companyRep
    participant system
    participant careerStaff
    participant Database

    companyRep->>system: registerCompanyRep(details)
    system->>Database: addUser(CompanyRepresentative)
    Database-->>system: userAdded
    system-->>companyRep: notify("Pending Approval")

    careerStaff->>system: login()
    system-->>careerStaff: loginSuccess()

    careerStaff->>system: approveCompanyRep(repID)
    system->>Database: getUser(repID)
    Database-->>system: companyRep
    system->>CompanyRepresentative: setApproved(true)
    system->>Database: saveData()
    Database-->>system: dataSaved
    system-->>companyRep: notify("Account Approved")

```
