import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV-based repository implementation for managing users.
 * Loads and saves user data from/to CSV files for students, staff, and company representatives.
 */
public class CsvUserRepository implements IUserRepository {
    private static final List<User> users = new ArrayList<>();
    private static int companyRepCounter = 1;
    private static boolean isLoaded = false; // Track if data already loaded
    private IInternshipRepository internshipRepository;
    private IApplicationRepository applicationRepository;

    /**
     * Constructs a CsvUserRepository.
     *
     * @param internshipRepository the internship repository
     * @param applicationRepository the application repository
     */
    public CsvUserRepository(IInternshipRepository internshipRepository, IApplicationRepository applicationRepository) {
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
        // Only load once per program execution
        if (!isLoaded) {
            loadUsers();
            isLoaded = true;
        }
    }

    /**
     * Sets the internship repository and updates references in users.
     *
     * @param internshipRepository the internship repository
     */
    public void setInternshipRepository(IInternshipRepository internshipRepository) {
        this.internshipRepository = internshipRepository;
        // Update repository references in all user objects
        for (User user : users) {
            if (user.isStudent()) {
                user.asStudent().setInternshipRepository(internshipRepository);
            } else if (user.isCompanyRepresentative()) {
                user.asCompanyRepresentative().setInternshipRepository(internshipRepository);
            } else if (user.isCareerCenterStaff()) {
                user.asCareerCenterStaff().setInternshipRepository(internshipRepository);
            }
        }
    }

    /**
     * Sets the application repository and updates references in users.
     *
     * @param applicationRepository the application repository
     */
    public void setApplicationRepository(IApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
        // Update repository references in all user objects
        for (User user : users) {
            if (user.isStudent()) {
                user.asStudent().setApplicationRepository(applicationRepository);
            } else if (user.isCompanyRepresentative()) {
                user.asCompanyRepresentative().setApplicationRepository(applicationRepository);
            } else if (user.isCareerCenterStaff()) {
                user.asCareerCenterStaff().setApplicationRepository(applicationRepository);
            }
        }
    }

    /**
     * Loads all users from CSV files.
     */
    private void loadUsers() {
        users.clear(); // Clear before loading to avoid duplicates
        try {
            loadStudents();
            loadStaff();
            loadCompanyRepresentatives();
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    /**
     * Gets all users.
     *
     * @return list of all users
     */
    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    /**
     * Gets a user by ID.
     *
     * @param userId the user ID
     * @return the user or null if not found
     */
    @Override
    public User getUserById(String userId) {
        return users.stream().filter(u -> u.getUserID().equalsIgnoreCase(userId)).findFirst().orElse(null);
    }

    /**
     * Adds a new user.
     *
     * @param user the user to add
     */
    @Override
    public void addUser(User user) {
        users.add(user);
    }

    /**
     * Removes a user by ID.
     *
     * @param userId the user ID
     */
    @Override
    public void removeUser(String userId) {
        users.removeIf(u -> u.getUserID().equals(userId));
    }

    /**
     * Saves all users to CSV files.
     */
    @Override
    public void saveUsers() throws IOException {
        saveStudents();
        saveStaff();
        saveCompanyRepresentatives();
    }

    /**
     * Loads students from CSV file.
     */
    private void loadStudents() {
        try (BufferedReader br = new BufferedReader(new FileReader("data/sample_student_list.csv"))) {
            String line = br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    String userID = parts[0].trim();
                    String name = parts[1].trim();
                    String major = parts[2].trim();
                    int year = Integer.parseInt(parts[3].trim());
                    double gpa = Double.parseDouble(parts[4].trim());
                    String passwordHash = parts[5].trim();
                    String salt = parts[6].trim();
                    Student student = new Student(userID, name, passwordHash, salt, year, major, gpa, internshipRepository, applicationRepository);
                    users.add(student);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading students: " + e.getMessage());
        }
    }

    /**
     * Loads staff from CSV file.
     */
    private void loadStaff() {
        try (BufferedReader br = new BufferedReader(new FileReader("data/sample_staff_list.csv"))) {
            String line = br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String userID = parts[0].trim();
                    String name = parts[1].trim();
                    String department = parts[2].trim();
                    String passwordHash = parts[3].trim();
                    String salt = parts[4].trim();
                    CareerCenterStaff staff = new CareerCenterStaff(userID, name, passwordHash, salt, department, this, internshipRepository, applicationRepository);
                    users.add(staff);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading staff: " + e.getMessage());
        }
    }

    /**
     * Loads company representatives from CSV file.
     */
    private void loadCompanyRepresentatives() {
        try (BufferedReader br = new BufferedReader(new FileReader("data/sample_company_representative_list.csv"))) {
            String line = br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 9) {
                    String userID = parts[0].trim();
                    String name = parts[1].trim();
                    String companyName = parts[2].trim();
                    String department = parts[3].trim();
                    String position = parts[4].trim();
                    String email = parts[5].trim();
                    String passwordHash = parts[6].trim();
                    String salt = parts[7].trim();
                    String status = parts[8].trim();
                    CompanyRepresentative rep = new CompanyRepresentative(userID, name, passwordHash, salt, companyName, department, position, email, internshipRepository, applicationRepository);
                    if ("Approved".equalsIgnoreCase(status)) {
                        rep.setApproved(true);
                    }
                    users.add(rep);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading company representatives: " + e.getMessage());
        }
    }

    /**
     * Saves students to CSV file.
     */
    private void saveStudents() throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter("data/sample_student_list.csv"))) {
            pw.println("UserID,Name,Major,Year,GPA,PasswordHash,Salt");
            for (User user : users) {
                if (user.isStudent()) {
                    Student student = user.asStudent();
                    pw.printf("%s,%s,%s,%d,%.1f,%s,%s%n",
                        student.getUserID(),
                        student.getName(),
                        student.getMajor(),
                        student.getYearOfStudy(),
                        student.getGpa(),
                        student.getPasswordHash(),
                        student.getSalt());
                }
            }
        }
    }

    /**
     * Saves staff to CSV file.
     */
    private void saveStaff() throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter("data/sample_staff_list.csv"))) {
            pw.println("UserID,Name,Department,PasswordHash,Salt");
            for (User user : users) {
                if (user.isCareerCenterStaff()) {
                    CareerCenterStaff staff = user.asCareerCenterStaff();
                    pw.printf("%s,%s,%s,%s,%s%n",
                        staff.getUserID(),
                        staff.getName(),
                        staff.getStaffDepartment(),
                        staff.getPasswordHash(),
                        staff.getSalt());
                }
            }
        }
    }

    /**
     * Saves company representatives to CSV file.
     */
    private void saveCompanyRepresentatives() throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter("data/sample_company_representative_list.csv"))) {
            pw.println("CompanyRepID,Name,CompanyName,Department,Position,Email,PasswordHash,Salt,Status");
            for (User user : users) {
                if (user.isCompanyRepresentative()) {
                    CompanyRepresentative rep = user.asCompanyRepresentative();
                    String status = rep.isApproved() ? "Approved" : (rep.isRejected() ? "Rejected" : "Pending");
                    pw.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                        rep.getUserID(),
                        rep.getName(),
                        rep.getCompanyName(),
                        rep.getDepartment(),
                        rep.getPosition(),
                        rep.getEmail(),
                        rep.getPasswordHash(),
                        rep.getSalt(),
                        status);
                }
            }
        }
    }

    /**
     * Generates a new unique company representative ID.
     *
     * @return the generated ID
     */
    @Override
    public String generateCompanyRepId() {
        return "CR" + String.format("%03d", companyRepCounter++);
    }
}