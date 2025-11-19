public class UIHelper {
    
    public static void printWelcomeBanner() {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                               ║");
        System.out.println("║        INTERNSHIP PLACEMENT MANAGEMENT SYSTEM (IPMS)          ║");
        System.out.println("║                                                               ║");
        System.out.println("║          Your Gateway to Professional Opportunities           ║");
        System.out.println("║                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    public static void printMainMenu() {
        System.out.println("\n┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                         MAIN MENU                           │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();
        System.out.println("  [1] Login to Your Account");
        System.out.println("  [2] Register New Account");
        System.out.println("  [3] Exit System");
        System.out.println();
        System.out.print(" Enter your choice: ");
    }
    
    public static void printRegistrationMenu() {
        System.out.println("\n┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                    REGISTRATION MENU                        │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();
        System.out.println("  [1] Register as Student");
        System.out.println("  [2] Register as Career Center Staff");
        System.out.println("  [3] Register as Company Representative");
        System.out.println("  [4] Back to Main Menu");
        System.out.println();
        System.out.print(" Enter your choice: ");
    }
    
    public static void printGoodbyeMessage() {
        System.out.println();
        System.out.println("  ╔═════════════════════════════════════════════════════════╗");
        System.out.println("  ║  Thank you for using the Internship Placement System    ║");
        System.out.println("  ║              Have a great day ahead!                    ║");
        System.out.println("  ╚═════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    public static void printLoginHeader() {
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                      USER LOGIN                             │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();
    }
    
    public static void printSuccessMessage(String message) {
        System.out.println();
        System.out.println("  " + message);
        System.out.println();
    }
    
    public static void printErrorMessage(String message) {
        System.out.println();
        System.out.println("  " + message);
    }
    
    public static void printWarningMessage(String message) {
        System.out.println();
        System.out.println("  " + message);
    }
    
    public static void printSectionHeader(String title) {
        System.out.println("\n=== " + title + " ===");
    }
    
    public static void printDivider() {
        System.out.println("-------------------");
    }

    // Additional menu headers
    public static void printStudentMenu() {
        System.out.println("\n╔═════════════════════════════════════════════╗");
        System.out.println("║          STUDENT MENU                       ║");
        System.out.println("╚═════════════════════════════════════════════╝");
    }

    public static void printCompanyRepMenu() {
        System.out.println("\n╔═════════════════════════════════════════════╗");
        System.out.println("║     COMPANY REPRESENTATIVE MENU             ║");
        System.out.println("╚═════════════════════════════════════════════╝");
    }

    public static void printCareerStaffMenu() {
        System.out.println("\n╔═════════════════════════════════════════════╗");
        System.out.println("║       CAREER CENTER STAFF MENU              ║");
        System.out.println("╚═════════════════════════════════════════════╝");
    }
}
