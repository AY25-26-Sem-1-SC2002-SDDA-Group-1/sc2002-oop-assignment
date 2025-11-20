import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final List<User> users = new ArrayList<>();
    private static final List<InternshipOpportunity> internships = new ArrayList<>();
    private static final List<Application> applications = new ArrayList<>();
    private static int applicationCounter = 1;
    private static int internshipCounter = 1;
    private static int companyRepCounter = 1;

    static {
        loadUsersFromCSV();
        try {
            loadApplications();
        } catch (IOException e) {
            System.err.println("Error loading applications: " + e.getMessage());
        }
    }

    public static void loadUsersFromCSV() {
        users.clear(); // Clear before loading to avoid duplicates
        try {
            loadStudents();
            loadStaff();
            loadCompanyRepresentatives();
            loadApplications();
        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }

    private static void loadStudents() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("sample_student_list.csv"));
        String line = reader.readLine();
        while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
            String[] parts = line.split(",");
            if (parts.length >= 5) {
                Student student = new Student(
                    parts[0].trim(),
                    parts[1].trim(),
                    "password",
                    Integer.parseInt(parts[3].trim()),
                    parts[2].trim(),
                    Double.parseDouble(parts[4].trim()),
                    null, // Legacy Database class - repositories not used
                    null  // Legacy Database class - repositories not used
                );
                users.add(student);
            }
        }
        reader.close();
    }

    private static void loadStaff() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("sample_staff_list.csv"));
        String line = reader.readLine();
        while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
            String[] parts = line.split(",");
            if (parts.length >= 4) {
                CareerCenterStaff staff = new CareerCenterStaff(
                    parts[0].trim(),
                    parts[1].trim(),
                    "password",
                    parts[3].trim()
                );
                users.add(staff);
            }
        }
        reader.close();
    }

    private static void loadCompanyRepresentatives() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("sample_company_representative_list.csv"));
        String line = reader.readLine();
        while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
            String[] parts = line.split(",");
            if (parts.length >= 6) {
                CompanyRepresentative rep = new CompanyRepresentative(
                    parts[0].trim(),
                    parts[1].trim(),
                    "password",
                    parts[2].trim(),
                    parts[3].trim(),
                    parts[4].trim(),
                    parts[5].trim(),
                    null,
                    null
                );
                if (parts.length >= 7) {
                    String status = parts[6].trim();
                    if (status.equalsIgnoreCase("Approved")) {
                        rep.setApproved(true);
                    } else if (status.equalsIgnoreCase("Rejected")) {
                        rep.setRejected(true);
                    }
                }
                users.add(rep);
            }
        }
        reader.close();
    }

    private static void loadApplications() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("applications.csv"));
        String line = reader.readLine(); // Skip header
        while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
            String[] parts = line.split(",");
            if (parts.length >= 5) {
                String applicationID = parts[0].trim();
                String studentID = parts[1].trim();
                String opportunityID = parts[2].trim();
                String status = parts[3].trim();
                boolean manuallyWithdrawn = Boolean.parseBoolean(parts[4].trim());

                Student student = (Student) getUser(studentID);
                InternshipOpportunity opportunity = getInternship(opportunityID);

                if (student != null && opportunity != null) {
                    Application application = new Application(applicationID, student, opportunity, status);
                    // Set manually withdrawn if needed
                    if (manuallyWithdrawn) {
                        application.setManuallyWithdrawn(true);
                    }
                    applications.add(application);
                }
            }
        }
        reader.close();
    }

    public static User getUser(String userID) {
        for (User user : users) {
            if (user.getUserID().equals(userID)) {
                return user;
            }
        }
        return null;
    }

    public static List<User> getUsers() {
        return users;
    }

    public static void addUser(User user) {
        users.add(user);
    }

    public static void removeUser(String userID) {
        users.removeIf(u -> u.getUserID().equals(userID));
    }

    public static InternshipOpportunity getInternship(String opportunityID) {
        for (InternshipOpportunity opportunity : internships) {
            if (opportunity.getOpportunityID().equals(opportunityID)) {
                return opportunity;
            }
        }
        return null;
    }

    public static List<InternshipOpportunity> getInternships() {
        return internships;
    }

    public static void addInternship(InternshipOpportunity opportunity) {
        internships.add(opportunity);
    }
    
    public static void removeInternship(String opportunityID) {
        internships.removeIf(opp -> opp.getOpportunityID().equals(opportunityID));
        // Also remove all applications for this internship
        applications.removeIf(app -> app.getOpportunity().getOpportunityID().equals(opportunityID));
    }
    
    public static void updateInternshipTitle(String opportunityID, String newTitle) {
        InternshipOpportunity opp = getInternship(opportunityID);
        if (opp != null) {
            opp.setTitle(newTitle);
        }
    }
    
    public static void updateInternshipDescription(String opportunityID, String newDescription) {
        InternshipOpportunity opp = getInternship(opportunityID);
        if (opp != null) {
            opp.setDescription(newDescription);
        }
    }

    public static Application getApplication(String applicationID) {
        for (Application application : applications) {
            if (application.getApplicationID().equals(applicationID)) {
                return application;
            }
        }
        return null;
    }

    public static List<Application> getApplications() {
        return applications;
    }

    public static void addApplication(Application application) {
        applications.add(application);
    }

    public static String generateApplicationID() {
        return "APP" + String.format("%04d", applicationCounter++);
    }

    public static String generateInternshipID() {
        return "INT" + String.format("%04d", internshipCounter++);
    }

    public static String generateCompanyRepID() {
        return "CR" + String.format("%03d", companyRepCounter++);
    }

    public static void saveData() {
        try {
            saveStudents();
            saveStaff();
            saveCompanyRepresentatives();
            saveApplications();
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }
    
    private static void saveStudents() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("sample_student_list.csv"));
        writer.write("UserID,Name,Major,YearOfStudy,GPA");
        writer.newLine();

        for (User user : users) {
            if (user instanceof Student) {
                Student student = (Student) user;
                writer.write(student.getUserID() + "," +
                           student.getName() + "," +
                           student.getMajor() + "," +
                           student.getYearOfStudy() + "," +
                           student.getGpa());
                writer.newLine();
            }
        }
        writer.close();
    }

    private static void saveApplications() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("applications.csv"));
        writer.write("ApplicationID,StudentID,OpportunityID,Status,ManuallyWithdrawn");
        writer.newLine();

        for (Application app : applications) {
            writer.write(app.getApplicationID() + "," +
                        app.getApplicant().getUserID() + "," +
                        app.getOpportunity().getOpportunityID() + "," +
                        app.getStatus() + "," +
                        app.isManuallyWithdrawn());
            writer.newLine();
        }
        writer.close();
    }

    private static void saveStaff() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("sample_staff_list.csv"));
        writer.write("StaffID,Name,Email,StaffDepartment");
        writer.newLine();
        
        for (User user : users) {
            if (user instanceof CareerCenterStaff) {
                CareerCenterStaff staff = (CareerCenterStaff) user;
                writer.write(staff.getUserID() + "," +
                           staff.getName() + "," +
                           staff.getUserID() + "@ntu.edu.sg," +
                           staff.getStaffDepartment());
                writer.newLine();
            }
        }
        writer.close();
    }

    private static void saveCompanyRepresentatives() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("sample_company_representative_list.csv"));
        writer.write("CompanyRepID,Name,CompanyName,Department,Position,Email,Status");
        writer.newLine();
        
        for (User user : users) {
            if (user instanceof CompanyRepresentative) {
                CompanyRepresentative rep = (CompanyRepresentative) user;
                String status = rep.isApproved() ? "Approved" : (rep.isRejected() ? "Rejected" : "Pending");
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s",
                    rep.getUserID(),
                    rep.getName(),
                    rep.getCompanyName(),
                    rep.getDepartment(),
                    rep.getPosition(),
                    rep.getEmail(),
                    status
                ));
                writer.newLine();
            }
        }
        writer.close();
    }
}