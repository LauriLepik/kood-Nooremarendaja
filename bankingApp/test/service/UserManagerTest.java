package service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import model.User;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class UserManagerTest {
    
    private UserManager userManager;
    private static final String TEST_DB_DIR = "test-db-" + System.currentTimeMillis();
    
    @BeforeEach
    void setUp() {
        // Use a temporary database directory for testing
        userManager = new UserManager();
    }
    
    @AfterEach
    void tearDown() {
        // Clean up test database directory
        deleteDirectory(new File(TEST_DB_DIR));
    }
    
    @Test
    void testRegisterUserCreatesNewUser() {
        userManager.registerUser("Alice", "Smith", "alice", "password123");
        
        assertTrue(userManager.userExists("alice"));
    }
    
    @Test
    void testUserExistsReturnsTrueForExistingUser() {
        userManager.registerUser("Bob", "Jones", "bob", "pass");
        
        assertTrue(userManager.userExists("bob"));
    }
    
    @Test
    void testUserExistsReturnsFalseForNonExistingUser() {
        assertFalse(userManager.userExists("nonexistent"));
    }
    
    @Test
    void testUserExistsIsCaseInsensitive() {
        userManager.registerUser("Charlie", "Brown", "charlie", "pwd");
        
        assertTrue(userManager.userExists("Charlie"));
        assertTrue(userManager.userExists("CHARLIE"));
        assertTrue(userManager.userExists("charlie"));
    }
    
    @Test
    void testFindUserRetrievesCorrectUser() {
        userManager.registerUser("David", "Lee", "david", "secret");
        
        User user = userManager.findUser("david");
        
        assertNotNull(user);
        assertEquals("David", user.getFirstName());
        assertEquals("Lee", user.getLastName());
        assertEquals("david", user.getUsername());
    }
    
    @Test
    void testFindUserIsCaseInsensitive() {
        userManager.registerUser("Eve", "Wilson", "eve", "pass");
        
        User user1 = userManager.findUser("eve");
        User user2 = userManager.findUser("Eve");
        User user3 = userManager.findUser("EVE");
        
        assertSame(user1, user2);
        assertSame(user2, user3);
    }
    
    @Test
    void testRegisterUserWithEmptyFirstNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            userManager.registerUser("", "Last", "user", "pass");
        });
    }
    
    @Test
    void testRegisterUserWithEmptyUsernameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            userManager.registerUser("First", "Last", "", "pass");
        });
    }
    
    @Test
    void testRegisterUserWithEmptyPasswordThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            userManager.registerUser("First", "Last", "user", "");
        });
    }
    
    @Test
    void testGetAllUsersReturnsMap() {
        userManager.registerUser("Frank", "Miller", "frank", "pwd");
        
        var users = userManager.getAllUsers();
        
        assertNotNull(users);
        assertTrue(users.containsKey("frank"));
    }
    
    
    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }
}
