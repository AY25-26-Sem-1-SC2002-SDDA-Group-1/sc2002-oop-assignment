# Testing Guide

This guide provides explicit step-by-step instructions to test all features of the Internship Placement System.

## Setup Instructions

1. **Compile the application:**
   ```bash
   javac *.java
   ```

2. **Start the application:**
   ```bash
   java InternshipPlacementSystem
   ```

3. **Test credentials to use:**
   - Student: U2310001A / password
   - Company Rep: JH / password
   - Staff: sng001 / password

## Test Scenarios

### 1. Staff Registration & Authentication

**Steps:**
1. From main menu, select option "2" (Register New Account)
2. Select option "2" (Staff)
3. Enter the following details:
   - User ID: teststaff001
   - Name: Test Staff
   - Password: password123
   - Department: Testing Department
4. Select option "1" (Log In to Your Account) from main menu
5. Enter credentials: teststaff001 / password123
6. Verify you see the staff menu with options for processing reps, internships, etc.
7. Select option "8" (Logout)

**Expected:** Staff registration succeeds, login works, staff menu appears, logout works.

### 2. Company Rep Registration & Approval Workflow

**Steps:**
1. From main menu, select option "2" (Register New Account)
2. Select option "3" (Company Representative)
3. Enter the following details:
   - User ID: testcompany001
   - Name: Test Company Rep
   - Password: password123
   - Company Name: Test Corp
   - Department: IT
   - Position: Manager
   - Email: test@company.com
4. Try to login with testcompany001 / password123
5. Verify login fails with "pending approval" message
6. Login as staff: teststaff001 / password123 (from test 1)
7. Select option "1" (Process Company Representatives)
8. Find and approve "testcompany001"
9. Logout as staff
10. Login as testcompany001 / password123
11. Verify company rep menu appears

**Expected:** Company rep registration succeeds, login blocked until approval, staff approval enables login.

### 3. Student Registration & Authentication

**Steps:**
1. From main menu, select option "2" (Register New Account)
2. Select option "1" (Student)
3. Enter the following details:
   - User ID: teststudent001
   - Name: Test Student
   - Password: password123
   - Year of Study: 2
   - Major: Computer Science
   - GPA: 4.0
4. Select option "1" (Log In to Your Account) from main menu
5. Enter credentials: teststudent001 / password123
6. Verify you see the student menu with options for viewing internships, applications, etc.
7. Select option "9" (Logout)

**Expected:** Student registration succeeds, login works, student menu appears, logout works.

### 3. Company Rep Registration & Approval Workflow

**Steps:**
1. From main menu, select option "2" (Register New Account)
2. Select option "3" (Company Representative)
3. Enter the following details:
   - User ID: testcompany001
   - Name: Test Company Rep
   - Password: password123
   - Company Name: Test Corp
   - Department: IT
   - Position: Manager
   - Email: test@company.com
4. Try to login with testcompany001 / password123
5. Verify login fails with "pending approval" message
6. Login as staff: sng001 / password
7. Select option "1" (Process Company Representatives)
8. Find and approve "testcompany001"
9. Logout as staff
10. Login as testcompany001 / password123
11. Verify company rep menu appears

**Expected:** Company rep registration succeeds, login blocked until approval, staff approval enables login.

### 5. Internship Approval Process

**Steps:**
1. Login as staff: teststaff001 / password123 (from test 1)
2. Select option "2" (Process Internships)
3. View pending internships from all company reps
4. Find the "Test Internship Position" and approve it
5. Logout
6. Login as student: teststudent001 / password123 (from test 3)
7. Select option "1" (View Eligible Internships)
8. Verify "Test Internship Position" appears in the list

**Expected:** Staff can approve internships, approved internships become visible to students.

### 4. Internship Creation & Management

**Steps:**
1. Login as approved company rep: JH / password
2. Select option "1" (Create New Internship)
3. Enter the following details:
   - Title: Test Internship Position
   - Description: A test internship for validation
   - Level: Basic
   - Number of Slots: 3
   - Minimum GPA: 3.0
   - Opening Date: 01/01/2024
   - Closing Date: 31/12/2024
4. Select option "2" (View My Internships)
5. Verify the internship appears with "Pending" status
6. Try to edit the internship (should work since it's pending)
7. Logout

**Expected:** Internship creation succeeds, appears in company rep's list, can be edited while pending.

### 4. Internship Creation & Management

**Steps:**
1. Login as approved company rep: testcompany001 / password123 (from test 2)
2. Select option "1" (Create New Internship)
3. Enter the following details:
   - Title: Test Internship Position
   - Description: A test internship for validation
   - Level: Basic
   - Number of Slots: 3
   - Minimum GPA: 3.0
   - Opening Date: 01/01/2024
   - Closing Date: 31/12/2024
4. Select option "2" (View My Internships)
5. Verify the internship appears with "Pending" status
6. Try to edit the internship (should work since it's pending)
7. Logout

**Expected:** Internship creation succeeds, appears in company rep's list, can be edited while pending.

### 6. Student Internship Discovery

**Steps:**
1. Login as student: teststudent001 / password123 (Year 2, Computer Science major, GPA 4.0, from test 3)
2. Select option "1" (View Eligible Internships)
3. Verify you see internships that match:
   - Major: Computer Science
   - GPA requirement ≤ 4.0
4. For internships where level is Intermediate/Advanced, verify "Ineligible: Level restriction: year 1-2 can only apply for Basic level" appears
5. Note which internships are visible
6. Select option "7" (Manage Filters) to set filters, then return to view filtered results

**Expected:** All matching major/GPA internships appear, with ineligibility reasons displayed for non-eligible ones. Filtering available via separate menu option.

### 7. Application Submission

**Steps:**
1. Login as student: teststudent001 / password123 (from test 3)
2. Select option "1" (View Eligible Internships)
3. Choose the "Test Internship Position" (eligible) and select option to apply
4. Provide any required application details
5. Select option "3" (View My Applications)
6. Verify the application appears with "Pending" status
7. Try to apply to the same internship again

**Expected:** Application submission succeeds, appears in student's applications, duplicate applications blocked.

### 8. Application Processing

**Steps:**
1. Login as company rep: testcompany001 / password123 (from test 2)
2. Select option "3" (Process Applications)
3. View pending applications for your internships
4. For the application from teststudent001, choose to approve
5. Logout
6. Login as teststudent001 / password123 (from test 3)
7. Select option "3" (View My Applications)
8. Check application status has changed to "Successful"

**Expected:** Company reps can process applications, status changes are reflected for students.

### 9. Offer Acceptance & Auto-Withdrawal

**Steps:**
1. Login as student: teststudent001 / password123 (from test 3, with successful application from test 8)
2. Select option "3" (View My Applications)
3. Find the "Successful" application and choose to accept it
4. Verify the accepted application shows "Confirmed" status
5. Check that all other applications now show "Withdrawn" status (if any)
6. Try to accept another offer

**Expected:** Single offer acceptance works, other applications auto-withdrawn, multiple acceptances blocked.

### 10. Withdrawal & Queue Management

**Steps:**
1. Login as student with confirmed internship
2. Select option "3" (View My Applications)
3. Choose the confirmed application and select withdrawal option
4. Logout
5. Login as company rep who owns the internship
6. Process the withdrawal request
7. Verify application status changes to "Withdrawn"
8. Check if any queued applications were automatically confirmed

**Expected:** Withdrawal process works, queue management promotes waiting applicants.

### 10. Password Security

**Steps:**
1. Login as teststudent001 / password123 (from test 3)
2. Select option "8" (Change Password)
3. Enter current password "password123", new password "newpass123", and confirm it
4. Logout
5. Try to login with old password "password123" (should fail)
6. Login with new password "newpass123" (should succeed)
7. Exit application completely
8. Restart application and login with new password again

**Expected:** Password changes work, persist across sessions, old passwords rejected.

### 11. Filtering & Search

**Steps:**
1. Login as student: teststudent001 / newpass123 (from test 10)
2. Select option "7" (Manage Filters)
3. Try different filter combinations:
   - Filter by Status: select "Approved"
   - Filter by Level: select "Basic"
   - Filter by Major: select "Computer Science"
   - Set GPA requirement filter
4. Test sorting options (by title, company, level, date)
5. Select option "1" (View Eligible Internships) to see filtered results
6. Navigate between different menus and verify filters persist
7. Logout and login again to verify filters reset

**Expected:** Filtering available via separate menu option, all options work correctly, filters maintain state during session.

### 12. Statistics Dashboard

**Steps:**
1. Login as student: teststudent001 / newpass123 (Year 2, from test 3)
2. Select option "6" (View My Statistics)
3. Verify GPA Analysis shows "Eligible for Advanced Level: No" (due to year restriction)
4. Note the displayed statistics (application counts, success rates)
5. Login as company rep: testcompany001 / password123 (from test 2)
6. Select option "4" (View Statistics)
7. Note the company rep statistics (internship counts, application metrics)

**Expected:** Both user types see relevant, accurate statistics that reflect their activity, with eligibility checks including year restrictions.

### 13. Data Persistence

**Steps:**
1. The changes from tests 1-12 should persist
2. Exit the application completely (not just logout)
3. Restart the application
4. Login as teststudent001 / newpass123 and verify:
   - User can login with new password
   - "Test Internship Position" still exists and is approved
   - Application from test 7 still shows with "Confirmed" status (from test 9)
5. Login as testcompany001 / password123 and verify internship and statistics

**Expected:** All data changes survive complete application restarts.

### 14. Error Handling & Validation

**Steps:**
1. Try various invalid operations:
   - Login as teststudent001 with wrong password "wrongpass"
   - Register a new student with duplicate user ID "teststudent001"
   - As teststudent001, try to apply to an ineligible internship (e.g., Advanced level)
   - As testcompany001, create internship with invalid dates (closing before opening)
   - Enter non-numeric values where numbers expected (e.g., GPA as "abc")
2. Verify appropriate error messages appear
3. Ensure system remains stable after errors

**Expected:** All invalid inputs handled gracefully with clear error messages, no crashes.

### 15. Batch Operations

**Steps:**
1. Login as teststudent001 / newpass123
2. Select option "2" (Apply for Internship)
3. When prompted for Internship ID(s), enter multiple IDs separated by spaces (use existing approved internships)
4. Login as testcompany001 / password123
5. Select option "3" (Process Applications)
6. When prompted for Application ID(s), enter multiple IDs separated by spaces

**Expected:** Batch operations work for both application submission and processing.

### 16. System Limits Enforcement

**Steps:**
1. Login as teststudent001 / newpass123 and try to submit 4 applications (should fail at 4th)
2. Login as testcompany001 / password123 and try to create 6 internships (should fail at 6th)
3. When creating internships, try 11 slots (should be rejected)
4. Try GPA values outside 0.0-5.0 range during registration
5. Try year values outside 1-4 range during registration

**Expected:** All system limits are properly enforced with appropriate error messages.

### 17. Complete Student Journey

**Steps:**
1. Register new student account (done in test 3)
2. Login as student (teststudent001)
3. Browse and apply to internships (tests 6-7)
4. Get application approved by company rep (test 8)
5. Accept the offer (test 9)
6. Verify placement is confirmed

**Expected:** Complete end-to-end workflow from registration to placement works seamlessly.

### 18. Complete Company Rep Journey

**Steps:**
1. Register company rep account (test 2)
2. Get approved by staff (test 2)
3. Login and create internship (test 4)
4. Get internship approved by staff (test 5)
5. Receive and process student applications (test 8)
6. Fill internship positions

**Expected:** Complete company workflow from registration to hiring works correctly.

### 19. Administrative Oversight

**Steps:**
1. Login as teststaff001 / password123
2. Approve pending company reps (if any beyond test 2)
3. Approve pending internships (if any beyond test 5)
4. Generate reports with various filters
5. View system statistics

**Expected:** All administrative functions work properly for system oversight.

### 20. Company Rep Internship Visibility Toggle

**Steps:**
1. Login as approved company rep: testcompany001 / password123
2. Select option "2" (View My Internships)
3. Note the visibility status of the "Test Internship Position"
4. Select option "5" (Toggle Visibility) for that internship
5. Select option "2" again to verify visibility has changed
6. Logout
7. Login as teststudent001 / newpass123 and check if the internship visibility affects what they see

**Expected:** Company reps can toggle internship visibility, affecting student access.

### 21. Staff Filtered Internship Viewing

**Steps:**
1. Login as teststaff001 / password123
2. Select option "4" (View All Internships (Filtered))
3. Set various filters (status, level, major, company)
4. Verify only matching internships appear
5. Clear filters and verify all internships show

**Expected:** Staff can view and filter all internships in the system.

### 22. Company Rep Application Processing for Specific Internship

**Steps:**
1. Login as testcompany001 / password123
2. Select option "4" (View Applications)
3. Choose the "Test Internship Position" to view applications for
4. Process applications (approve/reject) for that internship
5. Verify status changes are reflected

**Expected:** Company reps can view and process applications per internship.

### 23. Complete Staff Workflow

**Steps:**
1. Register new staff account (done in test 1)
2. Login and approve pending company reps (test 2)
3. Approve pending internships (test 5)
4. Process withdrawal requests
5. Generate detailed reports
6. View system-wide statistics

**Expected:** Staff can perform all administrative tasks end-to-end.

### 24. Complete Company Rep Workflow

**Steps:**
1. Register company rep account (test 2)
2. Get approved by staff (test 2)
3. Login and create multiple internships
4. Toggle visibility on internships
5. Receive and process student applications (test 8)
6. View detailed statistics
7. Edit internship details

**Expected:** Company reps can manage their internships and applications fully.

## Verification Checklist

After completing all 24 tests in order, verify:

- [ ] Staff registration and authentication (test 1)
- [ ] Company rep registration and approval workflow (test 2)
- [ ] Student registration and authentication (test 3)
- [ ] Internship creation and management (test 4)
- [ ] Internship approval process (test 5)
- [ ] Student internship discovery with ineligibility reasons (test 6)
- [ ] Application submission (test 7)
- [ ] Application processing (test 8)
- [ ] Offer acceptance and auto-withdrawal (test 9)
- [ ] Password security and persistence (test 10)
- [ ] Filtering and search functionality (test 11)
- [ ] Statistics dashboard accuracy (test 12)
- [ ] Data persistence across restarts (test 13)
- [ ] Error handling and validation (test 14)
- [ ] Batch operations (test 15)
- [ ] System limits enforcement (test 16)
- [ ] Complete student journey (test 17)
- [ ] Complete company rep journey (test 18)
- [ ] Administrative oversight (test 19)
- [ ] Visibility toggling (test 20)
- [ ] Staff filtered viewing (test 21)
- [ ] Per-internship application processing (test 22)
- [ ] Complete staff workflow (test 23)
- [ ] Complete company rep workflow (test 24)

## Test Data Reference

**Existing Test Users:**
- Students: U2310001A, U2310002B, U2310003C, U2310004D, U2310005E
- Company Reps: JH (Nvidia, approved)
- Staff: sng001 (CCDS department)

**Sample Data:**
- 3 existing internships with different requirements
- Various application states available for testing

Follow these explicit steps to thoroughly test every feature of the system.
