# Quick Test Guide - Process Applications Feature

## Test Scenario 1: No Applications to Process

### Steps:

1. Run: `java InternshipPlacementSystem`
2. Login as company representative (e.g., ID: JH, Password: password)
3. Choose option 5: "Process Applications"

### Expected Output:

```
=== PROCESS APPLICATIONS ===

⚠ No applications to process.
```

## Test Scenario 2: Process Pending Applications

### Prerequisites:

- Company rep must have created internships
- Students must have applied to those internships
- Applications must be in "Pending" status

### Steps:

1. Run: `java InternshipPlacementSystem`
2. Login as company representative
3. Choose option 5: "Process Applications"

### Expected Output:

```
=== PROCESS APPLICATIONS ===

Pending Applications:

==================================================
Application ID: APP001
Student Name: John Doe
Student ID: S001
Student Year: Year 3
Student Major: Computer Science
Internship: Software Engineering Intern
Applied Date: Fri Nov 15 12:30:45 SGT 2025
==================================================

[More applications listed if available...]

Enter Application ID to process (or 'cancel' to go back): APP001
Decision (approve/reject): approve

✓ Application approved successfully.
```

## Test Scenario 3: Cancel Operation

### Steps:

1. Run: `java InternshipPlacementSystem`
2. Login as company representative
3. Choose option 5: "Process Applications"
4. Enter: `cancel`

### Expected Output:

```
Enter Application ID to process (or 'cancel' to go back): cancel

⚠ Operation cancelled.
```

## Test Scenario 4: Invalid Application ID

### Steps:

1. Run: `java InternshipPlacementSystem`
2. Login as company representative
3. Choose option 5: "Process Applications"
4. Enter invalid ID: `INVALID123`

### Expected Output:

```
Enter Application ID to process (or 'cancel' to go back): INVALID123

✖ Invalid Application ID or application is not pending.
```

## Test Scenario 5: Reject Application

### Steps:

1. Run: `java InternshipPlacementSystem`
2. Login as company representative
3. Choose option 5: "Process Applications"
4. Enter valid Application ID
5. Enter: `reject`

### Expected Output:

```
Decision (approve/reject): reject

✓ Application rejected successfully.
```

## Test Scenario 6: Invalid Decision

### Steps:

1. Run: `java InternshipPlacementSystem`
2. Login as company representative
3. Choose option 5: "Process Applications"
4. Enter valid Application ID
5. Enter: `maybe` (or any invalid option)

### Expected Output:

```
Decision (approve/reject): maybe

✖ Invalid decision. Please enter 'approve' or 'reject'.
```

## Test Scenario 7: Empty Input

### Steps:

1. Run: `java InternshipPlacementSystem`
2. Login as company representative
3. Choose option 5: "Process Applications"
4. Press Enter without typing anything

### Expected Output:

```
Enter Application ID to process (or 'cancel' to go back):

✖ Application ID cannot be empty.
```

## Verification Checklist

After running tests, verify:

- [ ] "No applications to process" message appears when appropriate
- [ ] All pending applications are displayed with complete information
- [ ] Approve functionality works correctly
- [ ] Reject functionality works correctly
- [ ] Cancel option works
- [ ] Invalid inputs are handled gracefully
- [ ] Success messages use UIHelper formatting
- [ ] Error messages use UIHelper formatting
- [ ] Application status updates correctly in database
- [ ] Only pending applications are shown (not Successful/Unsuccessful)
- [ ] Only applications for rep's internships are shown
- [ ] Database saves automatically after processing

## Sample Data Setup

To test with sample data, you can:

1. **Login as Student** (create applications):

   - ID: S001, Password: password
   - Apply to available internships

2. **Login as Staff** (approve internships):

   - ID: STAFF001, Password: password
   - Approve company reps and internships

3. **Login as Company Rep** (process applications):
   - ID: JH, Password: password
   - Process student applications

## Integration Tests

Verify integration with rest of system:

1. **Create Internship** → **Student Applies** → **Process Application** → **Check Status**
2. **Multiple Applications** → **Process One** → **Verify Only Pending Shown**
3. **Different Reps** → **Each Sees Only Their Applications**
4. **Career Staff Reports** → **Verify Processed Applications Reflected**

## Edge Cases

Test these edge cases:

- [ ] Process application after another rep already processed it
- [ ] Process application for internship that was deleted
- [ ] Process application when internship status changed
- [ ] Multiple reps trying to process same application
- [ ] Very long student names or internship titles (display formatting)
- [ ] Special characters in input
- [ ] Case sensitivity (approve vs Approve vs APPROVE)

## Performance Tests

- [ ] Processing with 1 application
- [ ] Processing with 10 applications
- [ ] Processing with 50+ applications (if max slots allows)

## Notes

- All tests should complete without exceptions
- Database should auto-save after each operation
- UI should use consistent formatting (UIHelper)
- Error messages should be clear and helpful
- Success messages should provide confirmation
