import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvUserRepository implements IUserRepository {
    private static final List<User> users = new ArrayList<>();
    private static int companyRepCounter = 1;

    static {
        loadUsers();
    }

    private static void loadUsers() {
        try {
            loadStudents();
            loadStaff();
            loadCompanyRepresentatives();
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
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
                    Double.parseDouble(parts[4].trim())
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
                    parts[5].trim()
                );
                if (parts.length >= 7 && parts[6].trim().equalsIgnoreCase("Approved")) {
                    rep.setApproved(true);
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
        writer.println("UserID,Name,Password,Company,Department,Position,Contact,Status");
        for (User user : users) {
            if (user instanceof CompanyRepresentative) {
                CompanyRepresentative r = (CompanyRepresentative) user;
                writer.println(r.getUserID() + "," + r.getName() + ",password," + r.getCompanyName() + "," + r.getDepartment() + "," + r.getPosition() + "," + r.getEmail() + "," + (r.isApproved() ? "Approved" : "Pending"));
            }
        }
        writer.close();
    }

    @Override
    public String generateCompanyRepId() {
        return "CR" + String.format("%03d", companyRepCounter++);
    }
}