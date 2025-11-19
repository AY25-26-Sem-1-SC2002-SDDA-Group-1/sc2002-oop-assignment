# Changelog

Hey there! Here's what's been cooking in the Internship Placement System. We've been busy adding features, fixing bugs, and making things smoother for everyone.

## Version 3.1.0 - November 19, 2025

### User Experience Improvements

**Streamlined Registration Flow**: Consolidated the main menu registration options into a single "Register New Account" option that expands into a submenu. This provides a cleaner, more intuitive interface by reducing main menu clutter from 5 options to 3.

- Main menu now shows: Login, Register, Exit
- Registration submenu offers: Student, Career Center Staff, Company Representative, or Back to Main Menu
- Improved menu hierarchy for better navigation

### System Updates

**GPA Range Expansion**: Updated the GPA scale from 0.0-4.0 to 0.0-5.0 across the entire system to accommodate different grading systems.

- Student registration now accepts GPA values up to 5.0
- Internship minimum GPA requirements updated to 5.0 maximum
- All validation logic, error messages, and prompts updated consistently
- Documentation updated in TESTING_GUIDE.md

### Code Quality

- Added `showRegistrationMenu()` method to InternshipPlacementSystem
- Updated validation logic in CompanyRepresentative, CompanyRepMenuHandler, and InternshipService
- Enhanced UIHelper with new `printRegistrationMenu()` method
- UML diagrams updated to reflect new menu structure

## Version 3.0.0 - November 18, 2025

### Major Architectural Refactor

**SOLID Principles Implementation**: Completely refactored the codebase to follow SOLID principles for better maintainability, testability, and extensibility.

- **Single Responsibility**: Each class now has one clear purpose (repositories for data, services for logic, handlers for UI).
- **Open/Closed**: New features can be added without modifying existing code.
- **Liskov Substitution**: Subclasses properly replace base classes.
- **Interface Segregation**: Specific interfaces for different concerns.
- **Dependency Inversion**: High-level modules depend on abstractions, not concretions.

**Repository Pattern**: Introduced interfaces and implementations for data access (IUserRepository, IInternshipRepository, IApplicationRepository) with CSV-based storage.

**Service Layer**: Created business logic services (UserService, InternshipService, ApplicationService) to encapsulate domain rules.

**Handler Pattern**: Implemented menu handlers (StudentMenuHandler, CompanyRepMenuHandler, CareerStaffMenuHandler) for UI logic separation.

**Dependency Injection**: Main application now uses constructor injection to provide dependencies, reducing coupling.

**High Cohesion and Loose Coupling**: Achieved through layered architecture with clear separation of concerns.

### Documentation Updates

- **DESIGN_PRINCIPLES.md**: New document explaining SOLID principles and design patterns used.
- **UML.md**: Updated with class diagram and 20+ sequence diagrams for the new architecture.
- **README.md**: Updated class descriptions and documentation references.

### Code Quality Improvements

- Eliminated monolithic classes (e.g., old Database and InternshipPlacementSystem).
- Improved error handling and validation.
- Enhanced modularity for future enhancements.

## Version 2.0.0 - November 16, 2025

### Big New Stuff

**Flexible Withdrawals**: Students can now back out of applications at any point - pending, successful, or even confirmed. The system handles this smartly, only messing with queues when it's a confirmed placement.

**Batch Operations**: Tired of doing things one by one? Now you can apply to multiple internships, process applications, or toggle visibility for several internships all at once using space-separated IDs.

**Waitlist System**: When an internship fills up, students get queued automatically. First in, first out - and when a spot opens up, the next person gets confirmed right away.

**GPA Filtering**: Internships can now have minimum GPA requirements, and students are filtered based on their GPA eligibility.

**Statistics Dashboard**: Both students and company reps get detailed statistics on their activities - application success rates, internship fill rates, and more.

**Application Persistence**: Applications are now saved to CSV files, so they persist between sessions.

**User Registration System**: Added self-registration for students and career center staff, plus approval-based registration for company representatives. No more reliance on pre-loaded CSV files only.

### Better User Experience

**Pre-Show Lists**: Before you make decisions, you see what's available. Whether it's accepting offers or requesting withdrawals, you get the full picture first.

**Clean UI**: Ditched the emojis for professional text markers like [SUCCESS] and [FAILED]. Everything looks consistent now.

**Password Smarts**: Can't set your new password to be the same as your old one. Basic security, but hey, it works.

### Under the Hood Improvements

**Smart Tracking**: We remember if you withdrew manually so you can't just reapply to the same internship.

**Auto Status Updates**: Internships that were filled become available again when spots open up.

**Better Code Organization**: Centralized UI helpers, persistent filters, and solid validation everywhere.

**Data Persistence**: Everything saves properly now, including application states and queue info.

### Bug Squashes

- Fixed visibility issues for students
- Application limits now work correctly
- Status changes propagate properly
- Queue edge cases handled

### Docs and Stuff

- README updated with all the new features
- Better workflow docs
- Updated diagrams
- Test verification docs

## Version 1.0.0 - Initial Release

The basics: login system for students, company reps, and staff. Create internships, apply, approve, track everything. CSV data storage, filtering by major/year/dates. Simple but solid foundation.
