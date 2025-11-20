# Changelog

## 2025-11-20

### Bug Fix: ReportManager Not Using Repository Pattern (SOLID Violation)

**Problem**: The `ReportManager` class was directly using `Database.getInternships()` and `Database.getApplications()` static methods to fetch data, violating the Dependency Inversion Principle. This caused:

- Report generation showing 0 results even when matching internships existed
- Data inconsistencies between repositories and the Database static lists
- String comparisons using exact match instead of case-insensitive comparison
- Inability to properly test or mock the ReportManager

**Root Cause**:

- ReportManager was tightly coupled to the Database class
- Used exact string matching (`equals`) instead of case-insensitive matching (`equalsIgnoreCase`)
- Singleton pattern without dependency injection made it impossible to inject repositories
- Violated Dependency Inversion Principle (high-level module depending on low-level module)

**Solution**:

- Added `initialize(IInternshipRepository, IApplicationRepository)` method to ReportManager to inject repositories
- Refactored `generateReport()` to use `internshipRepository.getAllInternships()` instead of `Database.getInternships()`
- Refactored `getApplicationStatistics()` to use `applicationRepository.getAllApplications()` instead of `Database.getApplications()`
- Refactored `getInternshipStatistics()` to use `internshipRepository.getAllInternships()` instead of `Database.getInternships()`
- Fixed string comparison to use `equalsIgnoreCase()` for status and level filters to handle case variations
- Updated `CareerStaffMenuHandler.generateReports()` to initialize ReportManager with repositories before use
- Added null checks with error messages when repositories are not initialized

**Files Modified**: `ReportManager.java`, `CareerStaffMenuHandler.java`

**Outcome**: Report generation now works correctly, showing accurate results when filtering internships. The ReportManager properly adheres to SOLID principles (specifically Dependency Inversion Principle) and uses the repository pattern consistently. Data is now sourced from repositories ensuring consistency across the system.

---

### Enhancement: Immediate Duplicate User ID Validation

**Problem**: When registering a new account, users had to complete the entire registration form (entering name, password, and all other details) before being told that their chosen User ID already exists. This wasted time and created a poor user experience.

**Solution**:

- Added `isUserIdAvailable(String userId, boolean allowRejectedCompanyRep)` method to `UserService` to check availability immediately
- Modified all three registration flows (`registerStudent()`, `registerStaff()`, `registerCompanyRep()`) in `InternshipPlacementSystem` to validate User ID immediately after entry
- Shows "User ID already exists. Registration cancelled." message and exits registration flow before collecting other details
- For company representative registration, still allows reuse of rejected usernames (as per previous enhancement)

**Files Modified**: `UserService.java`, `InternshipPlacementSystem.java`.

**Outcome**: Users receive immediate feedback when attempting to use a duplicate User ID, saving time and improving the registration experience. Registration is cancelled before users waste time entering remaining information.

---

### Bug Fix: CSV Persistence Layer Duplication and Loss

**Problem**: Approved company representatives were not persisting correctly across program restarts. The system had two separate static user lists (Database and CsvUserRepository) that could become out of sync, leading to duplicate entries or lost data. Users would be loaded correctly but not saved or vice versa.

**Root Cause**:

- `CsvUserRepository` and `Database` both maintained separate static user lists
- Neither cleared their lists before reloading, causing duplicates on reconstruction
- CsvUserRepository loaded on every constructor call without checking if already loaded
- Database static block loaded immediately, creating first copy of data
- When saving, both lists might be out of sync

**Solution**:

- Added `isLoaded` flag to `CsvUserRepository` to ensure single load per program execution
- Added `users.clear()` before loading in both `CsvUserRepository` and `Database` to prevent duplicates
- Ensured both persistence layers stay synchronized during save operations
- Maintained dual-save approach (repository and Database) for backward compatibility

**Files Modified**: `CsvUserRepository.java`, `Database.java`.

**Outcome**: Company representative approvals and rejections now persist correctly. Data loads once, saves correctly, and remains consistent across program restarts. Duplicate user prevention and proper CSV persistence ensured.

### Enhancement: Username Reuse for Rejected Company Representatives

**Problem**: Once a company representative account was rejected, the username became permanently blocked. New users could not register with the same username even though the rejected account was no longer valid or active.

**Solution**:

- Modified `UserService.registerCompanyRep()` to check if existing username belongs to a rejected company rep.
- If account is rejected, remove it from both repository and Database before allowing new registration.
- Added `removeUser(String userId)` method to `IUserRepository` interface and implemented in `CsvUserRepository`.
- Added `removeUser(String userID)` static method to `Database` for legacy compatibility.
- Syncs removal across both repository and Database to maintain consistency.

**Files Modified**: `UserService.java`, `IUserRepository.java`, `CsvUserRepository.java`, `Database.java`.

**Outcome**: Rejected usernames can now be reused for new registrations. Students and staff still maintain strict username uniqueness (no reuse). SOLID principles maintained through repository pattern.

### Enhancement: Explicit Account Rejection State for Company Representatives

**Problem**: Rejecting a company representative only displayed a warning while leaving the account indistinguishable from never-reviewed pending accounts. Users always saw 'pending approval' on login with no persistence of rejection state.

**Solution**:

- Added `isRejected` flag to `CompanyRepresentative` alongside existing `isApproved`.
- Updated staff processing to set rejection via `setRejected(true)` and exclude rejected reps from pending lists.
- Login now distinguishes states: shows `Your account is rejected.` if rejected; still blocks pending accounts.
- CSV persistence extended to save and load `Rejected` status (Database loader & saver updated).
- Standardized rejection messaging for company reps, internships, and withdrawal requests.
- UML updated to include `isRejected` field and methods.

**Files Modified**: `CompanyRepresentative.java`, `CareerCenterStaff.java`, `CareerStaffMenuHandler.java`, `InternshipPlacementSystem.java`, `Database.java`, `UML.md`.

**Outcome**: Rejection is a clear, persisted state with consistent user-facing feedback. SOLID preserved—changes localized without new static coupling.

### Enhancement: Confirmation Before Joining Queue for Filled Internships

**Problem**: When a student attempted to accept a successful application for an internship that was already full, the system silently placed them in the queue without explicit consent. This could confuse users who might have expected immediate confirmation or wished to cancel.

**Solution**:

Implemented an explicit confirmation step in `Student.acceptInternship()`:

- Detects full internship (`confirmedCount >= maxSlots`).
- Displays informative message with current fill state: `[INFO] The internship '<title>' is currently full (X/Y slots filled).`
- Prompts: `Proceed to join the queue? (Y/N):`
- If user declines (`N`), no status change (remains `Successful`).
- If user agrees (`Y`/`Yes`), status changes to `Queued`, timestamp (`queuedDate`) recorded, and message `[QUEUED]` displayed.
- Persistence triggered only after confirmation.

**Files Modified**:

- `Student.java`: Added confirmation prompt and guarded queue placement.

**Outcome**: Users now have control and clarity before being placed into a waitlist queue for a filled internship.

### Enhancement: Queue Prioritization by Acceptance Order

**Problem**: Queue processing was using `appliedDate` (when students submitted their application) to determine who gets promoted first. This is incorrect - the queue should prioritize based on **acceptance order** (when students tried to accept but got queued because the internship was full).

**Example**:

- Student A applied at 14:01:20
- Student B applied at 14:01:56
- Student B tried to accept first → got queued at 14:10:00
- Student A tried to accept later → got queued at 14:12:00
- When slot opens, Student B should get it (tried to accept first), not Student A

**Solution**:

Added `queuedDate` field to track when students tried to accept but were queued:

**Application.java**:

- Added `private Date queuedDate;` field
- Added `getQueuedDate()` and `setQueuedDate()` methods
- Modified `updateStatus()` to automatically set `queuedDate = new Date()` when status becomes "Queued"

**CareerCenterStaff.java**:

- Updated `processQueue()` to sort by `queuedDate` instead of `appliedDate`
- Uses fallback to `appliedDate` if `queuedDate` is null (backward compatibility)

**CsvApplicationRepository.java**:

- Added `QueuedDate` as 8th column in applications.csv
- Updated `loadApplications()` to parse `queuedDate` from CSV (handles null/empty)
- Updated `saveApplications()` to write `queuedDate` to CSV

**Outcome**: Queue now correctly prioritizes students by the order they **tried to accept** the internship, not by when they originally applied.

### Bug Fix: Queue Processing Not Working - Stale Data Issue

**Problem**: When a confirmed internship was withdrawn, the queue processing failed to promote waitlisted students correctly. The system was using a stale application list, causing wrong students to be promoted.

**Root Cause**:

1. Initial fix addressed object comparison (`.equals()` vs ID comparison)
2. Secondary issue: `processQueue()` was working with a cached list of applications. After promoting someone and withdrawing their other applications, the list wasn't refreshed, so the next iteration used outdated statuses.

**Solution**:

- Moved application list refresh **inside the while loop** so each iteration gets current statuses
- Added immediate persistence after each promotion: `applicationRepository.saveApplications()`
- Changed loop logic to check slot availability at the start of each iteration
- Fixed internship ID comparison throughout: `app.getOpportunity().getOpportunityID().equals(internship.getOpportunityID())`

**Outcome**: Queue processing now works correctly. When a confirmed student withdraws, the **oldest queued applicant** (by appliedDate) is automatically promoted to confirmed status, with all their other applications withdrawn.

### Enhancement: Clear Error Messages for Application Rejections

**Problem**: When students couldn't apply to internships, the system returned false without explaining why, making it difficult to understand the rejection reason.

**Solution**:

Added detailed error messages with context for all rejection scenarios in `Student.applyForInternship()`:

- **[BLOCKED]** prefix for user-facing restrictions
- **[ERROR]** prefix for system errors
- Detailed context for each failure:
  - Already has confirmed internship → Shows which internship is confirmed
  - Max 3 active applications → Shows current count
  - Internship not open → Shows current status
  - Major mismatch → Shows required vs actual major
  - Level ineligible → Shows year requirement (Year 1-2 only Basic)
  - GPA too low → Shows required vs actual GPA
  - Already applied → Shows current application status
  - Previously withdrawn → Shows previous application ID

**Outcome**: Students now receive clear, actionable feedback explaining exactly why they cannot apply to specific internships.

### Bug Fix: Students Can No Longer Apply to Multiple Internships When Confirmed

**Problem**: Students with a confirmed internship were able to apply to additional internships, which violates the rule that students can only hold one confirmed internship at a time.

**Solution**:

- Added check in `Student.applyForInternship()` to prevent applications if the student already has a `Confirmed` internship.
- Added check in `ApplicationService.applyForInternship()` to prevent applications if the student already has a `Confirmed` internship.
- Displays clear error message: "You cannot apply to new internships while you have a confirmed internship."

**Outcome**: Students with confirmed internships are now properly blocked from applying to additional opportunities, enforcing the one-confirmed-internship rule.

### Architecture Refactor: Statistics Uses Repository Pattern (SOLID Compliance)

**Problem**: `Statistics` class was directly using `Database` static methods to fetch data, violating the Dependency Inversion Principle and causing it to not work properly with students and company representatives.

**Solution**:

- Refactored `Statistics` constructor to accept `IApplicationRepository`, `IInternshipRepository`, and `IUserRepository` via dependency injection.
- Updated all Statistics methods (`displayStudentStatistics()`, `displayCompanyRepresentativeStatistics()`, `displaySystemStatistics()`) to use injected repositories instead of `Database`.
- Added `getUserRepository()` getter to `UserService` for service layer consistency.
- Added `getApplicationRepository()` and `getInternshipRepository()` getters to `ApplicationService` and `InternshipService`.
- Updated menu handlers to pass repositories when creating `Statistics` instances.

**Outcome**: Statistics now properly adheres to SOLID principles and works correctly across all user types. Repositories are the single source of data truth.

### Architecture Refactor: FilterManager Converted to Instance-Based

**Problem**: `FilterManager` used static shared state (`FilterSettings`), causing filters set by one user to leak into other users' sessions.

**Solution**:

- Converted `FilterManager` from static class to instance-based with constructor accepting `Scanner`.
- Changed `filterSettings` from static to instance field.
- Updated all menu handlers (`StudentMenuHandler`, `CompanyRepMenuHandler`, `CareerStaffMenuHandler`) to:
  - Add `FilterManager` instance field
  - Create own `FilterManager` instance in constructor
  - Change all `FilterManager.method()` calls to `filterManager.method()`
- Each user session now maintains its own independent `FilterManager` instance.

**Outcome**: Filters are now properly isolated per user. Setting filters in one user session no longer affects other users' filter settings.

### Bug Fix: Career Staff View All Internships Uses Service Layer

**Problem**: `CareerStaffMenuHandler.viewAllInternshipsFiltered()` was directly calling `Database.getInternships()`, bypassing the service layer and violating SOLID principles.

**Solution**:

- Updated `viewAllInternshipsFiltered()` to use `internshipService.getAllInternships()` instead of `Database.getInternships()`.
- Ensures all data access goes through the proper repository/service layer hierarchy.

**Outcome**: Career staff can now properly view all internships through the correct architectural layers.

### Feature: Withdrawal Rejection Restores Previous Status

**Problem**: Rejecting a withdrawal request defaulted the application back to `Pending`, losing its original state (e.g., `Successful` or `Confirmed`).

**Solution**:

- Added `previousStatus` tracking to `Application` when status changes to `Withdrawal Requested`.
- Updated `CareerCenterStaff.processWithdrawal(...)` so rejecting a withdrawal uses `application.updateStatus("Withdrawal Rejected")`, which internally restores `previousStatus` (fallback to `Successful`).
- Accepting (approving) withdrawal sets status to `Withdrawn` and processes queue if a confirmed slot was freed.

**Outcome**: Application now cleanly reverts to its former status after a rejected withdrawal request; queue integrity preserved.

## 2025-11-20

### Feature: Waitlist Messaging at Apply Time + Queued Status

**Problem**: When students applied to internships that were already full, they received a generic success/failure message with no indication they were placed on a waitlist.

**Solution**:

- `ApplicationService.applyForInternship(...)` now assigns initial status `Queued` when the number of `Confirmed` applications for an internship is greater than or equal to its `maxSlots`; otherwise status remains `Pending`.
- `Student.applyForInternship(...)` mirrors this logic and prints a clear message if queued, including current filled slots and waitlist size.
- `StudentMenuHandler.applyForInternship()` tailors feedback per ID: prints `[WAITLISTED]` with slot counts and waitlist size when queued, or `[SUCCESS]` when pending.

**Outcome**: Students immediately see that they’re on a waitlist upon applying to a full internship and understand current capacity and queue size.

### Bug Fix: Student Views Show Company Name (Not Recruiter Name)

**Problem**: In the student "Successful Applications" and withdrawal lists, the field labeled "Company" displayed the recruiter’s name instead of the company’s name.

**Solution**:

- Updated `StudentMenuHandler` to consistently use `opp.getCreatedBy().getCompanyName()` when rendering company information across student views: eligible internships, my applications, successful applications (accept flow), and withdrawable applications.

**Outcome**: All student-facing views now correctly display the company’s name.

### Feature: Withdrawal Rejection Restores Previous Status

**Problem**: Rejecting a withdrawal request defaulted the application back to `Pending`, losing its original state (e.g., `Successful` or `Confirmed`).

**Solution**:

- Added `previousStatus` tracking to `Application` when status changes to `Withdrawal Requested`.
- Updated `CareerCenterStaff.processWithdrawal(...)` so rejecting a withdrawal uses `application.updateStatus("Withdrawal Rejected")`, which internally restores `previousStatus` (fallback to `Successful`).
- Accepting (approving) withdrawal sets status to `Withdrawn` and processes queue if a confirmed slot was freed.

**Outcome**: Application now cleanly reverts to its former status after a rejected withdrawal request; queue integrity preserved.

## 2025-11-20

### Feature: Waitlist Messaging at Apply Time + Queued Status

**Problem**: When students applied to internships that were already full, they received a generic message with no indication they were placed on a waitlist.

**Solution**:

- `ApplicationService.applyForInternship(...)` assigns initial status `Queued` when the number of `Confirmed` applications for an internship is greater than or equal to its `maxSlots`; otherwise status remains `Pending`.
- `Student.applyForInternship(...)` mirrors this logic and prints a clear message if queued, including filled slots and current waitlist size.
- `StudentMenuHandler.applyForInternship()` tailors feedback per ID: prints `[WAITLISTED]` with slot counts and waitlist size when queued, or `[SUCCESS]` when pending.

**Outcome**: Students immediately see that they’re on a waitlist upon applying to a full internship and understand current capacity and queue size.

### Bug Fix: Student Views Show Company Name (Not Recruiter Name)

**Problem**: In the student views, the field labeled "Company" sometimes displayed the recruiter’s name instead of the company’s name.

**Solution**:

- Updated `StudentMenuHandler` to consistently use `opp.getCreatedBy().getCompanyName()` when rendering company information across student views: eligible internships, my applications, successful applications (accept flow), and withdrawable applications.

**Outcome**: All student-facing views now correctly display the company’s name.

## 2025-11-20

### Bug Fix: Applied Date Showing Current Time Instead of Original Application Date

**Problem**: Application's applied date was showing the current date/time instead of when the student originally applied.
**Root Cause**: `Application` constructor always created `new Date()` for `appliedDate`, even when loading existing applications from CSV. The date parsed from CSV was discarded.
**Solution**:

- Added overloaded constructor `Application(String applicationID, Student applicant, InternshipOpportunity opportunity, String status, Date appliedDate)` that accepts the applied date as a parameter.
- Updated `CsvApplicationRepository.loadApplications()` to use the new constructor with the parsed date from CSV.
- Original constructor still creates `new Date()` when creating new applications via student application flow.

**Verification**:

- Applications now display original application dates from CSV (e.g., "Thu Nov 20 11:46:22 GMT+08:00 2025") instead of current timestamp.

---

## 2025-11-20

### Feature: Prevent Overlapping Internship Confirmations

**Problem**: Students could confirm multiple internships with overlapping time periods, which is not realistic.
**Requirement**: Only one internship can be confirmed per person for non-overlapping periods.
**Solution**:

- Added `datesOverlap(Date, Date, Date, Date)` helper method to `Student` class that checks if two date ranges overlap using standard interval overlap logic: `start1 <= end2 AND start2 <= end1`.
- Modified `Student.acceptInternship()` to check for existing confirmed internships with overlapping dates before allowing acceptance.
- If overlap detected, displays error message showing both internship periods and rejects the acceptance.
- Updated withdrawal logic to only withdraw applications with overlapping periods, not all applications, allowing students to maintain non-overlapping confirmed internships.
- Added import `java.util.Date` to Student class.

**Impact**:

- Students can now have multiple confirmed internships as long as their periods don't overlap.
- System provides clear feedback when attempting to accept overlapping internships.
- Automatic withdrawal only affects conflicting applications, preserving non-overlapping ones.

---

## 2025-11-20

### Bug Fix: Career Center Staff Not Receiving Withdrawal Requests

**Problem**: Career center staff could not see withdrawal requests submitted by students.
**Root Cause**: `CareerCenterStaff.getWithdrawalRequests()` used legacy `Database.getApplications()`, but students saved withdrawal requests via `applicationRepository.saveApplications()`. The Database class was never synced with repository changes.
**Solution**:

- Refactored `CareerCenterStaff` to use repository pattern with constructor-injected dependencies.
- Added overloaded constructor accepting `IUserRepository`, `IInternshipRepository`, and `IApplicationRepository`.
- Updated `getWithdrawalRequests()`, `processWithdrawal()`, and `processQueue()` to use `applicationRepository.getAllApplications()` with fallback to Database for backward compatibility.
- Modified `UserService.registerStaff()` and `CsvUserRepository.loadStaff()` to inject repositories when creating CareerCenterStaff instances.

**Verification**:

- Test `TestWithdrawalVisibility` confirms staff member can now see withdrawal requests (APP004 with status "Withdrawal Requested").
- Staff retrieves applications from same repository where students save them.

---

## 2025-11-20

### Bug Fix: Internship Filled Status Not Persisting

**Problem**: Internships remained stuck at "Approved" status even after reaching max confirmed slots.
**Root Cause**: `Student.acceptInternship()` called legacy `Database.saveData()` which didn't persist internship status changes to CSV.
**Solution**:

- Replaced `Database.saveData()` with `applicationRepository.saveApplications()` and `internshipRepository.saveInternships()`.
- Status now correctly updates to "Filled" and persists to `internships.csv`.
- Confirmed through manual testing: student accepts internship → status changes → CSV reflects "Filled".

### Refactor: Student Class Database Removal

Eliminated remaining `Database` static method calls in `Student`:

- `Database.generateApplicationID()` → `applicationRepository.generateApplicationId()`
- Multiple `Database.saveData()` → `applicationRepository.saveApplications()` (3 instances in acceptInternship, requestWithdrawal, and queued logic)
- Application creation now fully repository-driven with proper ID generation.

### Verification

- Compilation: Success (javac \*.java clean)
- Interactive test confirmed:
  - Students can apply and accept internships
  - Company reps can view applications correctly
  - Statistics menu available and functional
  - Status updates (Confirmed, Withdrawn) persist properly
  - Application visibility working end-to-end

---

## 2025-11-20 (Earlier)

### Refactor: CompanyRepresentative

Removed legacy `Database` dependencies from `CompanyRepresentative`:

- `createInternship` now uses `internshipRepository.getAllInternships()` for limit checks and `internshipRepository.generateInternshipId()` for ID creation.
- `processApplication` iterates `applicationRepository.getAllApplications()` to locate target and persists changes via repositories.
- `toggleVisibility` uses `internshipRepository.getInternshipById()` and immediately saves internships.
- Stripped all temporary debug logging from `viewApplications(String)`.

### Architectural Progress

- Strengthens Dependency Inversion: domain object relies solely on interfaces.
- Moves toward deprecating `Database` (still referenced by `Student`, managers, statistics/reporting).
- Persistence and ID generation centralized in repositories.

### Next Steps (Planned)

- Migrate remaining `Database` usages in: `Student`, `InternshipManager`, `ApplicationManager`, `ReportManager`, `Statistics`.
- Introduce application ID generation in `IApplicationRepository` to replace `Database.generateApplicationID()`.
- Remove `Database` class entirely after final migration and update tests/UML.

### Verification

- Compilation successful post-refactor (run `javac *.java`).
- Manual test via `TestApplicationView` confirms application visibility intact.

---

## Historical Versions

Previous detailed version notes retained below for context.

### Version 3.2.0 - November 20, 2025

(See previous entries for full details on phased initialization, CSV fixes, and SOLID enforcement.)

### Version 3.1.0 - November 19, 2025

(Registration flow, GPA expansion, approval persistence.)

### Version 3.0.0 - November 18, 2025

(Major architectural SOLID refactor: repositories, services, handlers.)

### Version 2.0.0 - November 16, 2025

(Withdrawals, batching, waitlists, statistics, persistence.)

### Version 1.0.0 - Initial Release

(Foundational login, internship/application features.)
