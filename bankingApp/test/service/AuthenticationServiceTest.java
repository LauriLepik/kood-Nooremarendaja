package service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import model.User;
import java.io.ByteArrayInputStream;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {
    
    private UserManager userManager;
    private AuthenticationService authService;
    
    @BeforeEach
    void setUp() {
        userManager = new UserManager(true);  // In-memory mode
    }
    
    @Test
    void testAuthenticateWithExitChoice() {
        String input = "0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        authService = new AuthenticationService(userManager, scanner);
        
        User result = authService.authenticate();
        
        assertNull(result);
    }
    
    @Test
    void testRegisterUserFlow() {
        // Input: Choose register (2), then provide user details
        String input = "2\nJohn\nDoe\njohndoe\npassword123\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        authService = new AuthenticationService(userManager, scanner);
        
        authService.authenticate();
        
        assertTrue(userManager.userExists("johndoe"));
    }
    
    @Test
    void testRegisterUserWithEmptyFirstNameRetries() {
        // Try empty first name, then provide valid one
        String input = "2\n\nAlice\nSmith\nalice\npass\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        authService = new AuthenticationService(userManager, scanner);
        
        authService.authenticate();
        
        assertTrue(userManager.userExists("alice"));
    }
    
    @Test
    void testRegisterUserWithInvalidUsernameRetries() {
        // Try username with spaces, then valid one
        String input = "2\nBob\nJones\nuser name\nbob\npassword\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        authService = new AuthenticationService(userManager, scanner);
        
        authService.authenticate();
        
        assertTrue(userManager.userExists("bob"));
    }
    
    @Test
    void testRegisterUserWithDuplicateUsernameRetries() {
        // Register first user
        userManager.registerUser("Charlie", "Brown", "charlie", "pwd");
        
        // Try to register with same username, then use different one
        String input = "2\nDave\nMiller\ncharlie\ndave\npassword\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        authService = new AuthenticationService(userManager, scanner);
        
        authService.authenticate();
        
        assertTrue(userManager.userExists("dave"));
    }
    
    @Test
    void testLoginWithValidCredentials() {
        // Register user first
        userManager.registerUser("Eve", "Wilson", "eve", "secret");
        
        // Login with correct credentials
        String input = "1\neve\nsecret\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        authService = new AuthenticationService(userManager, scanner);
        
        User result = authService.authenticate();
        
        assertNotNull(result);
        assertEquals("eve", result.getUsername());
    }
    
    @Test
    void testLoginWithInvalidPassword() {
        // Register user
        userManager.registerUser("Frank", "Taylor", "frank", "correct");
        
        // Try wrong password, then back out
        String input = "1\nfrank\nwrong\nback\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        authService = new AuthenticationService(userManager, scanner);
        
        User result = authService.authenticate();
        
        assertNull(result);
    }
    
    @Test
    void testLoginWithNonExistentUser() {
        // Try to login with non-existent user, then back out
        String input = "1\nnonexistent\nback\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        authService = new AuthenticationService(userManager, scanner);
        
        User result = authService.authenticate();
        
        assertNull(result);
    }
}
