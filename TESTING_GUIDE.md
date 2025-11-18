# Testing Guide

## Setup
- Compile: `javac *.java`
- Run: `java InternshipPlacementSystem`
- Test credentials: Student (U2310005E/password), Company Rep (JH/password), Career Staff (sng001/password)

## Core Functionality Checklist
- [ ] **User Authentication**: Login/logout works for all roles
- [ ] **Student Restrictions**: Year 1-2 see only Basic level internships; max 3 active applications enforced; GPA eligibility for internships
- [ ] **Company Rep Limits**: Max 5 internships, max 10 slots per internship, date validation (dd/MM/yyyy, closing > opening), GPA requirements (0.0-4.0)
- [ ] **Registration**: New company reps get auto-generated IDs (CR001, etc.), CSV updates immediately
- [ ] **Password Change**: Cannot reuse current password, verification works
- [ ] **Workflow Integration**: Staff approves rep → rep creates internship → staff approves (auto-visibility) → rep toggles visibility → student applies → rep processes → student accepts → auto-withdrawals
- [ ] **Unified Process Workflows**: Career staff process company reps, internships, withdrawals in single workflows with pending item listings
- [ ] **View Applied Internships**: Students see applied internships even when visibility off ("Visible: No (You applied)")
- [ ] **Filter/Sort**: All users filter by status/level/major/closing date/min GPA, sort by title/company/level/closing (default alphabetical by title), filters persist across menus, reset on logout
- [ ] **Flexible Withdrawal**: Request at Pending/Successful/Confirmed stages, queue processing for confirmed withdrawals
- [ ] **Batch Operations**: Space-separated IDs for student applications, company rep application processing/visibility toggle
- [ ] **Waitlist Queue**: Auto-queue for filled internships, FIFO auto-confirmation when slots open
- [ ] **Manual Withdrawal Prevention**: Cannot reapply to manually withdrawn internships
- [ ] **Smart Status Updates**: Filled internships revert to Approved when slots available
- [ ] **GPA System**: Students have GPA, internships have min GPA, eligibility checks, GPA filtering
- [ ] **UI Improvements**: Pre-display lists, emoji-free messages, professional formatting
- [ ] **Statistics Dashboard**: Students and company reps can view comprehensive statistics
- [ ] **Application Persistence**: Applications are saved to and loaded from applications.csv
- [ ] **GPA Filtering**: Students cannot apply to internships requiring higher GPA than they have
- [ ] **Application Persistence**: Applications are saved to and loaded from applications.csv

## Process Applications Feature Checklist
- [ ] **No Applications**: Shows "⚠ No applications to process." when empty
- [ ] **Pending Display**: Lists all pending applications with ID, student name/ID/year/major, internship title, applied date
- [ ] **Approve/Reject**: Processes valid pending applications, updates status to Successful/Unsuccessful, auto-saves
- [ ] **Cancel Option**: 'cancel' input returns to menu without changes
- [ ] **Validation**: Empty ID rejected, invalid ID rejected, only approve/reject decisions accepted
- [ ] **Permissions**: Only shows/applications for rep's internships, only pending status
- [ ] **Integration**: Works with existing database, UIHelper formatting, no breaking changes

## Edge Cases and Error Handling
- [ ] **Invalid Inputs**: Empty fields, wrong formats, out-of-range values handled gracefully
- [ ] **Permission Checks**: Users cannot access unauthorized data/actions
- [ ] **Data Persistence**: All changes saved to CSV, survives restarts
- [ ] **Concurrency**: Multiple users can operate without conflicts
- [ ] **Boundary Conditions**: Max limits enforced (applications, internships, slots)
- [ ] **Date Validation**: Invalid formats, past dates, closing before opening rejected
- [ ] **Status Transitions**: Proper state changes, no invalid transitions allowed

## Performance and Integration
- [ ] **Large Data Sets**: Handles multiple internships/applications without slowdown
- [ ] **Report Generation**: Career staff reports reflect all processed data
- [ ] **Cross-User Consistency**: Changes by one user visible to others after refresh
- [ ] **Memory Management**: No memory leaks, proper cleanup
- [ ] **File I/O**: CSV reads/writes work reliably, no corruption

## Verification Against Requirements
- [ ] **Unified Process Pattern**: All approve/reject consolidated into single methods
- [ ] **Filters for All Users**: Status, level, major, closing date, min GPA available
- [ ] **Alphabetical Default Sort**: Internships sorted by title unless changed
- [ ] **Filter Persistence**: Settings saved across menu navigation, reset on logout
- [ ] **Auto-Visibility**: Internships visible immediately after staff approval
- [ ] **View Applied Internships**: Students see applications regardless of visibility
- [ ] **GPA Filtering**: GPA-based eligibility and filtering implemented
- [ ] **OOP Principles**: Encapsulation, SRP, DRY, reusability maintained
- [ ] **All Appendix A Test Cases**: 20 cases implemented and passing

## Test Scenarios
1. **Company Rep Registration**: Register → approve as staff → login with new ID → verify CSV
2. **Student Application Limits**: Apply to 4th internship → rejected
3. **Internship Creation Limits**: Create 6th internship → rejected
4. **Date Input**: Enter invalid format → loop until valid
5. **Application Processing**: Approve/reject with full details display
6. **Filter Persistence**: Set filters → navigate menus → filters retained
7. **Withdrawal Queue**: Accept filled internship → auto-queue → withdrawal opens slot → auto-confirm
8. **Manual Withdrawal**: Withdraw → cannot reapply to same internship
9. **Batch Operations**: Apply to multiple with space-separated IDs
10. **Auto-Visibility**: Staff approves internship → immediately visible to students
11. **GPA Eligibility**: Student with GPA 3.0 cannot apply to internship requiring 3.5 GPA
12. **Statistics Dashboard**: View student stats (applications, success rate) and company rep stats (internships, applications, fill rate)

## Appendix A Test Case Verification

### Test Case Summary Matrix

| Test # | Test Case                      | Status | Implementation Notes |
| ------ | ------------------------------ | ------ | -------------------- |
| 1      | Valid User Login               | ✅     | Validates ID/password, role-specific menus |
| 2      | Invalid ID                     | ✅     | Shows "Invalid user ID." |
| 3      | Incorrect Password             | ✅     | Shows "Incorrect password." |
| 4      | Password Change                | ✅     | Verifies current, updates password |
| 5      | Company Rep Authorization      | ✅     | Blocks login until staff approval |
| 6      | Visibility Based on Profile    | ✅     | Filters by year, major, level, visibility |
| 7      | Application Eligibility        | ✅     | Max 3 apps, eligibility checks |
| 8      | View Apps After Visibility Off | ✅     | Students see all their applications |
| 10     | Single Placement Acceptance    | ✅     | Auto-withdraws others, updates slots |
| 13     | Opportunity Creation Limits    | ✅     | Max 5 internships, slots 1-10 |
| 14     | Approval Status Viewing        | ✅     | Reps see all their internships |
| 15     | Detail Access for Reps         | ✅     | Always accessible to creators |
| 16     | Edit Restriction (Approved)    | ✅     | Only pending internships editable |
| 18     | Application Management         | ✅     | View/process applications, slot management |
| 19     | Confirmation Status Update     | ✅     | Status changes to Confirmed |
| 20     | Create/Edit/Delete Listings    | ✅     | Full CRUD with validations |
| 21     | Staff Opportunity Approval     | ✅     | Approve/reject pending internships |
| 22     | Toggle Visibility              | ✅     | Changes reflected in student view |
| 23     | Withdrawal Management          | ✅     | Process withdrawals, update slots |
| 24     | Report Generation & Filtering  | ✅     | Filtered reports by status/level/major |

**All 20 Appendix A Test Cases: ✅ PASS**

### Quick Test Scenarios

#### Scenario 1: Company Rep Registration & Approval
1. Main Menu → Option 2 (Register)
2. Enter: test@company.com, John Doe, password, TechCorp, IT, Manager
3. Try login with test@company.com → BLOCKED (pending approval)
4. Login as staff: sng001/password
5. Menu Option 1 → Approve test@company.com
6. Logout and login as test@company.com → SUCCESS

#### Scenario 2: Year 1-2 Student Restrictions
1. Login as U2310004D (Year 1)/password
2. Staff creates and approves Basic & Intermediate internships
3. Company rep toggles visibility ON
4. Student views internships → Only sees Basic level
5. Try apply to Intermediate → BLOCKED with error message

#### Scenario 3: Max 3 Applications
1. Login as student
2. Apply to 3 different Basic internships
3. Try apply to 4th → BLOCKED "maximum of 3 active applications"

#### Scenario 4: Single Acceptance, Auto-Withdraw
1. Company rep approves 3 applications
2. Student sees 3 "Successful" applications
3. Accept first application → Status "Confirmed"
4. Check other applications → Auto "Withdrawn"

#### Scenario 5: Edit Restriction
1. Company rep creates internship (Status: Pending)
2. Menu Option 3 (Edit) → Can edit title, description
3. Staff approves internship (Status: Approved)
4. Rep tries Option 3 again → Only pending shown, approved blocked

#### Scenario 6: Max 5 Internships
1. Company rep creates 5 internships
2. Try create 6th → BLOCKED "maximum limit of 5 internships"

#### Scenario 7: Password Change
1. Login as any user
2. Select "Change Password" option
3. Enter current: password, new: newpass123, confirm: newpass123
4. Logout
5. Login with old password → FAIL
6. Login with newpass123 → SUCCESS

#### Scenario 8: Report Filtering
1. Login as staff
2. Option 6 (Generate Reports)
3. Filter by Status: Approved, Level: Basic, Major: Computer Science
4. Report shows only matching opportunities

## Appendix A Test Case Verification

### Test Case Summary Matrix

| Test # | Test Case                      | Status | Implementation Notes |
| ------ | ------------------------------ | ------ | -------------------- |
| 1      | Valid User Login               | ✅     | Validates ID/password, role-specific menus |
| 2      | Invalid ID                     | ✅     | Shows "Invalid user ID." |
| 3      | Incorrect Password             | ✅     | Shows "Incorrect password." |
| 4      | Password Change                | ✅     | Verifies current, updates password |
| 5      | Company Rep Authorization      | ✅     | Blocks login until staff approval |
| 6      | Visibility Based on Profile    | ✅     | Filters by year, major, level, visibility |
| 7      | Application Eligibility        | ✅     | Max 3 apps, eligibility checks |
| 8      | View Apps After Visibility Off | ✅     | Students see all their applications |
| 10     | Single Placement Acceptance    | ✅     | Auto-withdraws others, updates slots |
| 13     | Opportunity Creation Limits    | ✅     | Max 5 internships, slots 1-10 |
| 14     | Approval Status Viewing        | ✅     | Reps see all their internships |
| 15     | Detail Access for Reps         | ✅     | Always accessible to creators |
| 16     | Edit Restriction (Approved)    | ✅     | Only pending internships editable |
| 18     | Application Management         | ✅     | View/process applications, slot management |
| 19     | Confirmation Status Update     | ✅     | Status changes to Confirmed |
| 20     | Create/Edit/Delete Listings    | ✅     | Full CRUD with validations |
| 21     | Staff Opportunity Approval     | ✅     | Approve/reject pending internships |
| 22     | Toggle Visibility              | ✅     | Changes reflected in student view |
| 23     | Withdrawal Management          | ✅     | Process withdrawals, update slots |
| 24     | Report Generation & Filtering  | ✅     | Filtered reports by status/level/major |

**All 20 Appendix A Test Cases: ✅ PASS**

### Quick Test Scenarios

#### Scenario 1: Company Rep Registration & Approval
1. Main Menu → Option 2 (Register)
2. Enter: test@company.com, John Doe, password, TechCorp, IT, Manager
3. Try login with test@company.com → BLOCKED (pending approval)
4. Login as staff: sng001/password
5. Menu Option 1 → Approve test@company.com
6. Logout and login as test@company.com → SUCCESS

#### Scenario 2: Year 1-2 Student Restrictions
1. Login as U2310004D (Year 1)/password
2. Staff creates and approves Basic & Intermediate internships
3. Company rep toggles visibility ON
4. Student views internships → Only sees Basic level
5. Try apply to Intermediate → BLOCKED with error message

#### Scenario 3: Max 3 Applications
1. Login as student
2. Apply to 3 different Basic internships
3. Try apply to 4th → BLOCKED "maximum of 3 active applications"

#### Scenario 4: Single Acceptance, Auto-Withdraw
1. Company rep approves 3 applications
2. Student sees 3 "Successful" applications
3. Accept first application → Status "Confirmed"
4. Check other applications → Auto "Withdrawn"

#### Scenario 5: Edit Restriction
1. Company rep creates internship (Status: Pending)
2. Menu Option 3 (Edit) → Can edit title, description
3. Staff approves internship (Status: Approved)
4. Rep tries Option 3 again → Only pending shown, approved blocked

#### Scenario 6: Max 5 Internships
1. Company rep creates 5 internships
2. Try create 6th → BLOCKED "maximum limit of 5 internships"

#### Scenario 7: Password Change
1. Login as any user
2. Select "Change Password" option
3. Enter current: password, new: newpass123, confirm: newpass123
4. Logout
5. Login with old password → FAIL
6. Login with newpass123 → SUCCESS

#### Scenario 8: Report Filtering
1. Login as staff
2. Option 6 (Generate Reports)
3. Filter by Status: Approved, Level: Basic, Major: Computer Science
4. Report shows only matching opportunities

## Post-Test Cleanup
- [ ] Reset CSV files to original state if needed
- [ ] Verify no exceptions in logs
- [ ] Confirm all success/error messages use consistent formatting
- [ ] Check database integrity after operations