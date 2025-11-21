import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvUserRepository implements IUserRepository {
    private static final List<User> users = new ArrayList<>();
    private static int companyRepCounter = 1;
    private static boolean isLoaded = false; // Track if data already loaded
    private IInternshipRepository internshipRepository;
    private IApplicationRepository applicationRepository;

    public CsvUserRepository(IInternshipRepository internshipRepository, IApplicationRepository applicationRepository) {
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
        // Only load once per program execution
        if (!isLoaded) {
            loadUsers();
            isLoaded = true;
        }
    }

    // Setters for dependency injection after initialization
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

    private void loadUsers() {
        users.clear(); // Clear before loading to avoid duplicates
        try {
            loadStudents();
            loadStaff();
            loadCompanyRepresentatives();
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    private void loadStudents() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("data/sample_student_list.csv"));
        String line = reader.readLine(); // Skip header
        while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
            String[] parts = line.split(",");
            if (parts.length >= 7) { // Updated to include hash and salt
                Student student = new Student(
                    parts[0].trim(),
                    parts[1].trim(),
                    parts[5].trim(), // password hash
                    parts[6].trim(), // salt
                    Integer.parseInt(parts[3].trim()),
                    parts[2].trim(),
                    Double.parseDouble(parts[4].trim()),
                    internshipRepository,
                    applicationRepository
                );
                users.add(student);
            } else if (parts.length >= 5) {
                // Backward compatibility: if no hash/salt columns, use default
                String defaultSalt = PasswordUtil.generateSalt();
                String defaultHash = PasswordUtil.hashPassword("password", defaultSalt);
                Student student = new Student(
                    parts[0].trim(),
                    parts[1].trim(),
                    defaultHash,
                    defaultSalt,
                    Integer.parseInt(parts[3].trim()),
                    parts[2].trim(),
                    Double.parseDouble(parts[4].trim()),
                    internshipRepository,
                    applicationRepository
                );
                users.add(student);
            }
        }
        reader.close();
    }

    private void loadStaff() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("data/sample_staff_list.csv"));
        String line = reader.readLine();
        while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
            String[] parts = line.split(",");
            if (parts.length >= 6) { // Updated to include hash and salt
                CareerCenterStaff staff = new CareerCenterStaff(
                    parts[0].trim(),
                    parts[1].trim(),
                    parts[4].trim(), // password hash
                    parts[5].trim(), // salt
                    parts[3].trim(),
                    this,
                    internshipRepository,
                    applicationRepository
                );
                users.add(staff);
            } else if (parts.length >= 4) {
                // Backward compatibility: if no hash/salt columns, use default
                String defaultSalt = PasswordUtil.generateSalt();
                String defaultHash = PasswordUtil.hashPassword("password", defaultSalt);
                CareerCenterStaff staff = new CareerCenterStaff(
                    parts[0].trim(),
                    parts[1].trim(),
                    defaultHash,
                    defaultSalt,
                    parts[3].trim(),
                    this,
                    internshipRepository,
                    applicationRepository
                );
                users.add(staff);
            }
        }
        reader.close();
    }

    private void loadCompanyRepresentatives() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("data/sample_company_representative_list.csv"));
        String line = reader.readLine();
        while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
            String[] parts = line.split(",");
            if (parts.length >= 8) { // Updated to include hash and salt
                CompanyRepresentative rep = new CompanyRepresentative(
                    parts[0].trim(),
                    parts[1].trim(),
                    parts[6].trim(), // password hash
                    parts[7].trim(), // salt
                    parts[2].trim(),
                    parts[3].trim(),
                    parts[4].trim(),
                    parts[5].trim(),
                    internshipRepository,
                    applicationRepository
                );
                if (parts.length >= 9) {
                    String status = parts[8].trim();
                    if (status.equalsIgnoreCase("Approved")) {
                        rep.setApproved(true);
                    } else if (status.equalsIgnoreCase("Rejected")) {
                        rep.setRejected(true);
                    }
                }
                users.add(rep);
            } else if (parts.length >= 6) {
                // Backward compatibility: if no hash/salt columns, use default
                String defaultSalt = PasswordUtil.generateSalt();
                String defaultHash = PasswordUtil.hashPassword("password", defaultSalt);
                CompanyRepresentative rep = new CompanyRepresentative(
                    parts[0].trim(),
                    parts[1].trim(),
                    defaultHash,
                    defaultSalt,
                    parts[2].trim(),
                    parts[3].trim(),
                    parts[4].trim(),
                    parts[5].trim(),
                    internshipRepository,
                    applicationRepository
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

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    @Override
    public User getUserById(String userId) {
        return users.stream().filter(u -> u.getUserID().equals(userId)).findFirst().orElse(null);
    }

    @Override
    public void addUser(User user) {
        users.add(user);
    }

    @Override
    public void removeUser(String userId) {
        users.removeIf(u -> u.getUserID().equals(userId));
    }

    @Override
    public void saveUsers() {
        try {
            saveStudents();
            saveStaff();
            saveCompanyRepresentatives();
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    private void saveStudents() throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter("data/sample_student_list.csv"));
        writer.println("UserID,Name,Major,Year,GPA,PasswordHash,Salt");
        for (User user : users) {
            if (user.isStudent()) {
                Student s = user.asStudent();
                writer.println(s.getUserID() + "," + s.getName() + "," + s.getMajor() + "," +
                              s.getYearOfStudy() + "," + s.getGpa() + "," +
                              s.getPasswordHash() + "," + s.getSalt());
            }
        }
        writer.close();
    }

    private void saveStaff() throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter("data/sample_staff_list.csv"));
        writer.println("UserID,Name,Department,PasswordHash,Salt");
        for (User user : users) {
            if (user.isCareerCenterStaff()) {
                CareerCenterStaff s = user.asCareerCenterStaff();
                writer.println(s.getUserID() + "," + s.getName() + "," + s.getStaffDepartment() + "," +
                              s.getPasswordHash() + "," + s.getSalt());
            }
        }
        writer.close();
    }

    private void saveCompanyRepresentatives() throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter("data/sample_company_representative_list.csv"));
        writer.println("CompanyRepID,Name,CompanyName,Department,Position,Email,PasswordHash,Salt,Status");
        for (User user : users) {
            if (user.isCompanyRepresentative()) {
                CompanyRepresentative r = user.asCompanyRepresentative();
                String status = r.isApproved() ? "Approved" : (r.isRejected() ? "Rejected" : "Pending");
                writer.println(r.getUserID() + "," + r.getName() + "," + r.getCompanyName() + "," +
                              r.getDepartment() + "," + r.getPosition() + "," + r.getEmail() + "," +
                              r.getPasswordHash() + "," + r.getSalt() + "," + status);
            }
        }
        writer.close();
    }

    @Override
    public String generateCompanyRepId() {
        return "CR" + String.format("%03d", companyRepCounter++);
    }
}