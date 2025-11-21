1. OBJECTIVES
The main objective of this assignment is
• to apply the Object-Oriented (OO) concepts you have learnt in the course,
• to model, design and develop an OO application.
• to gain familiarity with using Java as an object-oriented programming language.
• to work collaboratively as a group to achieve a common goal.
2. LABORATORY
Assigned CCDS lab.
3. EQUIPMENT
Hardware: PC (or Laptop)
Software: Your preferred Java IDE (e.g., IntelliJ, Eclipse, NetBeans) or
text editor with JDK installed.
Visual paradigm.
4. THE ASSIGNMENT
The assignment for your group will be to design and develop a:
Internship Placement Management System
The following are information about the system:
Overview of the System:
• The system will act as a centralized hub for all Students, Company
Representatives, and Career Center Staff.
• All users will need to login to this hub using their account.
o Students’ IDs will start with U, followed by 7-digit numbers
and ends with a letter (e.g., U2345123F)
o Company Representatives ID is their company email address.
o Career Center Staff’s ID is their NTU account.
o Assume all users use the default password, which is password.
o A user can change password in the system.
• Additional Information about the user:
o Student: Year of Study and Major
o Company Representatives: Company Name, Department, and
position.
o Carrer Center Staff: Staff Department
• A user list can be initiated through a file uploaded into the system at
initialization.
SC/CE/CZ2002 Object-Oriented Design C Programming Assignment
User’s capabilities:
1. All Users
• All users should have a User ID, Name and Password
• They should all have access to the basic user management features,
including login, logout and change password
2. Student
• Possesses all the base user capabilities
• Registration is automatic by reading in from the student list file.
• Can only view the list of internship opportunities based:
o Student’s own profile:
 Year of study (Year 1 to 4)
 Major (CSC, EEE, MAE, etc...)
o Visibility has been toggled “on”
• Able to apply for a maximum of 3 internship opportunities at once
o Year 1 and 2 students can ONLY apply for Basic-level internships
o Year 3 and above students can apply for any level (Basic,
Intermediate, Advanced)
• Able to view the internship he/she applied for, even after visibility is
turned off, and the application status (“Pending”, “Successful” or
“Unsuccessful”)
o Status will be “Pending” by default, and updated based on the input
from the Company Representative
• If application status is “Successful”, students can accept the
internship placement
o Only 1 internship placement can be accepted
o All other applications will be withdrawn once an internship
placement is accepted
• Allowed to request withdrawal for their internship application before/after
placement confirmation
o Subject to approval from Career Center Staff
3. Company Representatives
• Company Representative list is empty at very beginning.
• Company Representatives must register as a representative of a
specific company, and they can only log in once approved by the Career
Center Staff.
• Able to create internship opportunities (up to 5) for their companies,
which should include the following details:
o Internship Title
o Description
o Internship Level (Basic, Intermediate, Advanced)
o Preferred Majors (Assume 1 preferred major will do)
o Application opening date
o Application closing date
o Status (“Pending”, “Approved”, “Rejected”, “Filled”)
o Company Name
o Company Representatives in charge (automatically assigned)
o Number of slots (max of 10)
• Internship opportunities created must be approved by the career center
staff
o Once status is “Approved”, students may apply for them
o If “Filled” or after the Closing Date, students will not be able to
apply for them anymore
o Able to view application details and student details for each of their
internship opportunities
• May Approve or Reject the internship application
o Once approved, student application status becomes "Successful"
o Student can then accept the placement confirmation
o Internship opportunity status becomes "Filled" only when all
available slots are confirmed by students
• Able to toggle the visibility of the internship opportunity to "on" or "off".
This will be reflected in the internship list that will be visible to Students
4. Career Center Staff
• Registration is automatic by reading in from the staff list file.
• Able to authorize or reject the account creation of Company
Representatives
• Able to approve or reject internship opportunities submitted by
Company Representatives
o Once approved, internship opportunity status changes to
"Approved" and becomes visible to eligible students
• Able to approve or reject student withdrawal requests (both before and
after placement confirmation)
• Able to generate comprehensive reports regarding internship
opportunities created:
o There should be filters to generate filter opportunities based on
their Status, Preferred Majors, Internship Level, etc...
Miscellaneous:
• All users can use filters to view internship opportunities (Status,
Preferred Majors, Internship Level, Closing Date, etc.) Assume that
default is by alphabetical order. User filter settings are saved when
they switch menu pages.
