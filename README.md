# sc2002-oop-assignment

An Internship Placement System, designed with Object-Oriented Principles

## Features

- **User Management**: Three user types (Students, Company Representatives, Career Center Staff) with self-registration capabilities
- **Registration System**: Students and Career Center Staff can register new accounts; Company Representatives require staff approval
- **Internship Management**: Create, approve, and manage internship opportunities
- **Application Process**: Students can apply for internships, track applications, and accept offers
- **Approval Workflows**: Career center staff approve company reps and internships
- **Reporting**: Generate filtered reports on internship opportunities
- **Data Persistence**: CSV-based storage for user data and applications (organized in `/data` folder)
- **Batch Processing**: Space-separated IDs for mass operations (apply, process applications, toggle visibility)
- **Waitlist System**: Automatic queue management for filled internships with FIFO processing
- **Smart Status Management**: Automatic status updates when slots become available
- **GPA Filtering**: Minimum GPA requirements for internships with eligibility checking
- **Statistics Dashboard**: Comprehensive stats for students and company representatives
- **Automated Testing**: Comprehensive test suite with 13 automated tests covering authentication, data persistence, and business logic

## Security & Architecture Improvements

### Enhanced Security
- **Password Hashing**: All passwords are securely hashed using SHA-256 with salt
- **Input Validation**: Comprehensive validation for all user inputs (GPA ranges, major lists, email formats)
- **Data Protection**: No plain text password storage in memory or files

### Architecture Refinements
- **SOLID Compliance**: Full adherence to SOLID principles with proper dependency injection
- **Repository Pattern**: Clean data access layer with legacy code completely removed
- **Performance Optimization**: Streamlined algorithms using Java streams for better performance
- **Code Quality**: Clean compilation without warnings, proper error handling
- **Code Cleanup**: Removed all unused legacy classes (`ApplicationManager`, `InternshipManager`, `Database`)

## Testing

### Manual Testing Guide
The project includes comprehensive manual testing procedures covering all system functionality:

- **15 Detailed Test Scenarios**: Step-by-step procedures for testing all features
- **Expected Behavior Documentation**: Clear descriptions of correct system responses
- **Edge Case Coverage**: Error handling and boundary condition testing
- **Data Persistence Verification**: Tests that ensure changes survive application restarts
- **Workflow Validation**: End-to-end testing of business processes

### Testing Guide Location
- **Complete Manual Testing Guide**: `docs/TESTING_GUIDE.md`
- **Step-by-step Instructions**: Detailed procedures with expected outcomes
- **Test Data Reference**: Sample credentials and data for testing
- **Troubleshooting**: Common issues and resolution steps

### Test Coverage
- **User Authentication**: All user types and role-based access
- **Input Validation**: Registration, internship creation, application processing
- **Business Logic**: GPA eligibility, internship restrictions, workflow rules
- **Data Persistence**: CSV operations and restart verification
- **Error Handling**: Invalid inputs, boundary conditions, edge cases
- **UI/UX**: Menu navigation, formatting, user experience

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
- **Password Security**: Secure SHA-256 password hashing with salt; prevents reuse of current password

### Advanced Features

- **Waitlist Queue**: Students added to queue when internship is full, automatically confirmed when slots open
- **Manual Withdrawal Tracking**: Prevents reapplication to internships students manually withdrew from
- **Auto Status Updates**: Internships automatically change from "Filled" to "Approved" when slots become available
- **Queue Processing**: Automatic confirmation of queued applications in FIFO order

### Registration Workflows

- **Student Self-Registration**: Students can create accounts with custom user IDs, including year of study, major, and GPA
- **Staff Self-Registration**: Career Center Staff can register with custom user IDs and department information
- **Company Representative Approval**: Company reps register but require staff approval before account activation
- **Dynamic User Management**: System supports both pre-loaded users and dynamically registered accounts

## System Architecture

### Classes

- `User`: Base class for all users
- `Student`: Extends User, handles student operations with GPA and enhanced validation
- `CompanyRepresentative`: Extends User, manages company internships
- `CareerCenterStaff`: Extends User, admin functions with queue processing
- `InternshipOpportunity`: Represents internship postings with GPA requirements
- `Application`: Manages student applications with manual withdrawal tracking
- `IUserRepository`: Interface for user data access
- `CsvUserRepository`: Implementation of IUserRepository for CSV storage
- `IInternshipRepository`: Interface for internship data access
- `CsvInternshipRepository`: Implementation of IInternshipRepository
- `IApplicationRepository`: Interface for application data access
- `CsvApplicationRepository`: Implementation of IApplicationRepository
- `UserService`: Business logic for user operations
- `InternshipService`: Business logic for internship operations
- `ApplicationService`: Business logic for application operations
- `IMenuHandler`: Interface for menu handling
- `StudentMenuHandler`: Handles student UI menus
- `CompanyRepMenuHandler`: Handles company representative UI menus
- `CareerStaffMenuHandler`: Handles career center staff UI menus
- `InternshipPlacementSystem`: Main CLI application with dependency injection
- `UIHelper`: Centralized UI formatting utilities
- `FilterSettings`: Manages persistent filter preferences including GPA
- `Statistics`: Provides comprehensive statistics for users

### Data Storage

- Users are loaded from CSV files:
  - `sample_student_list.csv` (includes GPA)
  - `sample_staff_list.csv`
  - `sample_company_representative_list.csv`
- Applications are persisted to `applications.csv`
- Internships are stored in memory during runtime

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

0. **Registration**: Students and Career Center Staff can self-register; Company Representatives register but require staff approval
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

### GPA Filtering System

- **Student GPA**: Each student has a GPA field loaded from CSV
- **Internship Requirements**: Company reps can set minimum GPA requirements when creating internships
- **Eligibility Checking**: Students can only apply to internships where their GPA meets or exceeds the minimum requirement
- **Filter Options**: GPA-based filtering available in the filter settings

### Statistics Dashboard

- **Student Statistics**: View application counts, success rates, acceptance rates by level
- **Company Rep Statistics**: View internship creation stats, application received stats, fill rates
- **Personalized Insights**: Detailed breakdowns of user activity and performance

### Application Persistence

- **CSV Storage**: Applications are saved to `applications.csv` with all necessary fields
- **Session Persistence**: Application data survives system restarts
- **Data Integrity**: Proper loading and saving of application states

## Documentation

- **docs/CHANGELOG.md**: Complete version history and feature additions
- **docs/DESIGN_PRINCIPLES.md**: Detailed explanation of SOLID principles and design patterns used
- **docs/UML.md**: All UML class and sequence diagrams
- **docs/TESTING_GUIDE.md**: Comprehensive testing procedures and validation checklists
- **README.md**: This file - system overview and usage guide
