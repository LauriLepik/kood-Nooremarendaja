package service;

import java.util.HashMap;
import java.util.Map;
import model.User;

public class UserManager {
    private Map<String, User> users;
    private final util.DataStore dataStore;

    // Default constructor
    public UserManager() {
        this.dataStore = new util.DataStore();
        this.users = dataStore.loadData();
    }
    
    // Constructor for in-memory tests
    public UserManager(boolean inMemory) {
        if (inMemory) {
            this.dataStore = null;  // No database for tests
            this.users = new HashMap<>();
        } else {
            this.dataStore = new util.DataStore();
            this.users = dataStore.loadData();
        }
    }
    
    public void save() {
        if (dataStore != null) {
            dataStore.saveData(users);
        }
    }

    public boolean userExists(String name) {
        return users.containsKey(name.toLowerCase());
    }

    public User findUser(String name) {
        return users.get(name.toLowerCase());
    }

    public void registerUser(String firstName, String lastName, String username, String password) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First Name cannot be empty.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty or just spaces.");
        }
        if (userExists(username)) {
            System.out.println("User already exists.");
            return;
        }
        User newUser = new User(firstName.trim(), lastName == null ? "" : lastName.trim(), username.trim(), password);
        users.put(username.toLowerCase(), newUser);
        save();
    }
    
    public Map<String, User> getAllUsers() {
        return users;  // Return direct reference (tests need to manipulate for persistence simulation)
    }
}
