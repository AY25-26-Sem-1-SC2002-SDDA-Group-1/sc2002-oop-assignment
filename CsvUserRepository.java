import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvUserRepository implements IUserRepository {
    private static final List<User> users = new ArrayList<>();
    private static int companyRepCounter = 1;
    private static boolean isLoaded = false; // Track if data already loaded
    private final IInternshipRepository internshipRepository;
    private final IApplicationRepository applicationRepository;

    public CsvUserRepository(IInternshipRepository internshipRepository, IApplicationRepository applicationRepository) {
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
        // Only load once per program execution
        if (!isLoaded) {
            loadUsers();
            isLoaded = true;
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
                    internshipRepository,
                    applicationRepository
                );
                users.add(student);
            }
        }
        reader.close();
    }

    private void loadStaff() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("sample_staff_list.csv"));
        String line = reader.readLine();
        while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
            String[] parts = line.split(",");
            if (parts.length >= 4) {
                CareerCenterStaff staff = new CareerCenterStaff(
                    parts[0].trim(),
                    parts[1].trim(),
                    "password",
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
        PrintWriter writer = new PrintWriter(new FileWriter("sample_student_list.csv"));
        writer.println("UserID,Name,Major,Year,GPA");
        for (User user : users) {
            if (user instanceof Student) {
                Student s = (Student) user;
                writer.println(s.getUserID() + "," + s.getName() + "," + s.getMajor() + "," + s.getYearOfStudy() + "," + s.getGpa());
            }
        }
        writer.close();
    }

    private void saveStaff() throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter("sample_staff_list.csv"));
        writer.println("UserID,Name,Password,Department");
        for (User user : users) {
            if (user instanceof CareerCenterStaff) {
                CareerCenterStaff s = (CareerCenterStaff) user;
                writer.println(s.getUserID() + "," + s.getName() + ",password," + s.getStaffDepartment());
            }
        }
        writer.close();
    }

    private void saveCompanyRepresentatives() throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter("sample_company_representative_list.csv"));
        writer.println("CompanyRepID,Name,CompanyName,Department,Position,Email,Status");
        for (User user : users) {
            if (user instanceof CompanyRepresentative) {
                CompanyRepresentative r = (CompanyRepresentative) user;
                String status = r.isApproved() ? "Approved" : (r.isRejected() ? "Rejected" : "Pending");
                writer.println(r.getUserID() + "," + r.getName() + "," + r.getCompanyName() + "," + r.getDepartment() + "," + r.getPosition() + "," + r.getEmail() + "," + status);
            }
        }
        writer.close();
    }

    @Override
    public String generateCompanyRepId() {
        return "CR" + String.format("%03d", companyRepCounter++);
    }
}