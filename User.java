public class User {
    protected String userID;
    protected String name;
    protected String password;
    protected boolean isLoggedIn;

    public User(String userID, String name, String password) {
        this.userID = userID;
        this.name = name;
        this.password = password;
        this.isLoggedIn = false;
    }

    public boolean login(String password) {
        if (this.password.equals(password)) {
            this.isLoggedIn = true;
            return true;
        }
        return false;
    }

    public void logout() {
        this.isLoggedIn = false;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}