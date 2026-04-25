package financetracker.model;

/**
 * Represents an application user.
 */
public class User {
    private final String username;
    private final String password;  

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // For file loading 
    public static User fromFile(String username, String password) {
        return new User(username, password);
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public boolean authenticate(String password) {
        return password.equals(this.password);
    }


    public String toFileString() {
        return username + "|" + password;
    }
    @Override public String toString() { return "User(" + username + ")"; }
}
