# sc2002-oop-assignment
An Internship Placement System, designed with Object-Oriented Principles

# UML Class Diagram

```mermaid
classDiagram
    class User {
        -String userID
        -String name
        -String password
        +login(): bool
        +logout(): void
        +changePassword(newPassword: String): void
    }

    class Student {
        -int yearOfStudy
        -String major
        +viewEligibleInternships(): List<InternshipOpportunity>
        +applyForInternship(opportunityID: String): bool
        +viewApplications(): List<Application>
        +acceptInternship(applicationID: String): void
        +requestWithdrawal(applicationID: String): void
    }

    class CompanyRepresentative {
        -String companyName
        -String department
        -String position
        +createInternship(opportunity: InternshipOpportunity): bool
        +viewApplications(): List<Application>
        +approveApplication(applicationID: String): void
        +rejectApplication(applicationID: String): void
        +toggleVisibility(opportunityID: String, visible: bool): void
    }

    class CareerCenterStaff {
        -String staffDepartment
        +approveCompanyRep(repID: String): void
        +approveInternship(opportunityID: String): void
        +rejectInternship(opportunityID: String): void
        +approveWithdrawal(applicationID: String): void
        +rejectWithdrawal(applicationID: String): void
        +generateReports(filters: Map): Report
    }

    class InternshipOpportunity {
        -String opportunityID
        -String title
        -String description
        -String level  // Basic, Intermediate, Advanced
        -String preferredMajor
        -Date openingDate
        -Date closingDate
        -String status  // Pending, Approved, Rejected, Filled
        -int maxSlots
        -boolean visibility
        -CompanyRepresentative createdBy
        +isOpen(): bool
        +isVisible(): bool
    }

    class Application {
        -String applicationID
        -Student applicant
        -InternshipOpportunity opportunity
        -String status  // Pending, Successful, Unsuccessful
        -Date appliedDate
        +updateStatus(newStatus: String): void
    }

    User <|-- Student
    User <|-- CompanyRepresentative
    User <|-- CareerCenterStaff

    CompanyRepresentative "1" --> "0..5" InternshipOpportunity : creates >
    Student "1" --> "0..3" Application : applies >
    InternshipOpportunity "1" --> "*" Application : has >
    Application "*" --> "1" Student
    Application "*" --> "1" InternshipOpportunity

```

# UML Sequence Diagrams

## Apply for Intern

```mermaid
sequenceDiagram

    participant student

    participant system

    participant internshipDB

    participant applicationDB

  

    student->>system: login(userID, password)

    system-->>student: loginSuccess()

  

    student->>system: viewEligibleInternships()

    system->>internshipDB: getVisibleOpenInternships(major, year)

    internshipDB-->>system: List<InternshipOpportunity>

    system-->>student: showList()

  

    student->>system: applyForInternship(opportunityID)

    system->>internshipDB: getInternship(opportunityID)

    internshipDB-->>system: InternshipOpportunity

  

    system->>internshipDB: isOpen()

    system->>internshipDB: isVisible()

  

    alt Eligible and Visible

        system->>applicationDB: createApplication(studentID, opportunityID, "Pending")

        applicationDB-->>system: applicationCreated

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
    participant internshipDB
    participant careerStaff

    companyRep->>system: login()
    system-->>companyRep: loginSuccess()

    companyRep->>system: createInternship(opportunityDetails)
    system->>internshipDB: save(opportunity, "Pending")
    internshipDB-->>system: opportunitySaved
    system-->>companyRep: notify("Submitted for approval")

    careerStaff->>system: login()
    system-->>careerStaff: loginSuccess()

    careerStaff->>system: approveInternship(opportunityID)
    system->>internshipDB: updateStatus(opportunityID, "Approved")
    internshipDB-->>system: statusUpdated
    system-->>companyRep: notify("Internship Approved")

```

## Accept Internship
```mermaid
sequenceDiagram
    participant student
    participant system
    participant applicationDB
    participant internshipDB

    student->>system: acceptInternship(applicationID)
    system->>applicationDB: getApplication(applicationID)
    applicationDB-->>system: application

    system->>applicationDB: updateStatus(applicationID, "Confirmed")
    applicationDB-->>system: statusUpdated

    system->>applicationDB: withdrawOtherApplications(studentID)
    applicationDB-->>system: otherApplicationsWithdrawn

    system->>internshipDB: checkAndUpdateFilledStatus(opportunityID)
    internshipDB-->>system: filledStatusUpdated
    system-->>student: placementConfirmed

```

## Withdraw from Application
```mermaid
sequenceDiagram

    participant student

    participant system

    participant careerStaff

    participant applicationDB

  

    student->>system: requestWithdrawal(applicationID)

    system->>careerStaff: notifyWithdrawalRequest(applicationID)

  

    careerStaff->>system: reviewWithdrawalRequest(applicationID)

    system->>applicationDB: getApplication(applicationID)

    applicationDB-->>system: application

  

    careerStaff->>system: approveWithdrawal(applicationID)

    system->>applicationDB: updateStatus(applicationID, "Withdrawn")

    applicationDB-->>system: statusUpdated

  

    system-->>student: withdrawalApproved()
```

## Generate Report
```mermaid
sequenceDiagram
    participant careerStaff
    participant system
    participant internshipDB

    careerStaff->>system: login()
    system-->>careerStaff: loginSuccess()

    careerStaff->>system: generateReports(filters)
    system->>internshipDB: queryWithFilters(filters)
    internshipDB-->>system: filteredOpportunities
    system-->>careerStaff: displayReport

```

## Approve Application
```mermaid
sequenceDiagram
    participant companyRep
    participant system
    participant applicationDB
    participant student

    companyRep->>system: viewApplications(opportunityID)
    system->>applicationDB: getApplicationsByOpportunity(opportunityID)
    applicationDB-->>system: applications
    system-->>companyRep: displayApplications

    companyRep->>system: approveApplication(applicationID)
    system->>applicationDB: updateStatus(applicationID, "Successful")
    applicationDB-->>system: statusUpdated

    system-->>student: notify("Application Successful")

```

## Approve Rep Acct
```mermaid
sequenceDiagram
    participant companyRep
    participant system
    participant careerStaff

    companyRep->>system: registerCompanyRep(details)
    system-->>companyRep: notify("Pending Approval")

    careerStaff->>system: login()
    system-->>careerStaff: loginSuccess()

    careerStaff->>system: approveCompanyRep(repID)
    system-->>companyRep: notify("Account Approved")

```
