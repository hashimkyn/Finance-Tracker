package financetracker.model;

/**
 * Represents an application user.
 * Encapsulates credentials and identity.
 */
public class User {
    private final String username;
    private final String passwordHash;  // stored as simple hash (no external libs)

    public User(String username, String password) {
        this.username = username;
        this.passwordHash = hashPassword(password);
    }

    // For file loading (password already hashed)
    public static User fromFile(String username, String passwordHash) {
        User u = new User(username, "");          // placeholder
        return new User(username, passwordHash) {
            // override so the stored hash isn't double-hashed
            @Override public boolean authenticate(String pwd) {
                return hashPassword(pwd).equals(passwordHash);
            }
        };
    }

    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }

    public boolean authenticate(String password) {
        return hashPassword(password).equals(this.passwordHash);
    }

    /** Simple deterministic hash (not cryptographic, for demo purposes). */
    public static String hashPassword(String password) {
        int h = 31;
        for (char c : password.toCharArray()) h = h * 31 + c;
        return Integer.toHexString(h);
    }

    public String toFileString() {
        return username + "|" + passwordHash;
    }

    @Override public String toString() { return "User(" + username + ")"; }
}
