package financetracker.service;

import financetracker.model.User;
import financetracker.util.FileManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles user registration and authentication.
 */
public class AuthService {

    private final List<User> users;

    public AuthService() {
        this.users = new ArrayList<>(FileManager.loadUsers());
    }

    /**
     * Register a new user.
     * @return true if registration succeeded, false if username already taken.
     */
    public boolean register(String username, String password) {
        if (username == null || username.isBlank())  return false;
        if (password == null || password.length() < 4) return false;

        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) return false;
        }
        User newUser = new User(username, password);
        users.add(newUser);
        FileManager.saveUsers(users);
        return true;
    }

    /**
     * Authenticate an existing user.
     * @return the User object on success, null on failure.
     */
    public User login(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username) && u.authenticate(password)) {
                return u;
            }
        }
        return null;
    }

    public boolean usernameExists(String username) {
        return users.stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }
}
