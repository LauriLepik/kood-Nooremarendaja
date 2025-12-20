package util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import model.User;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class DataStoreTest {
    
    private String testDbDir;
    private DataStore dataStore;
    
    @BeforeEach
    void setUp() {
        // Create unique temporary directory for each test
        testDbDir = "test-db-" + System.currentTimeMillis();
        dataStore = new DataStore(testDbDir);
    }
    
    @AfterEach
    void tearDown() {
        // Clean up test database directory
        deleteDirectory(new File(testDbDir));
    }
    
    // ============ NORMAL OPERATIONS ============
    
    @Test
    void testSaveDataCreatesProperCSVFiles() {
        Map<String, User> users = Map.of(
            "alice", new User("Alice", "Smith", "alice", "password")
        );
        
        dataStore.saveData(users);
        
        File usersFile = new File(testDbDir + "/users.csv");
        assertTrue(usersFile.exists());
    }
    
    @Test
    void testLoadDataRetrievesUsers() {
        // Save a user first
        User user = new User("Bob", "Jones", "bob", "secret");
        Map<String, User> usersToSave = Map.of("bob", user);
        dataStore.saveData(usersToSave);
        
        // Load and verify
        Map<String, User> loadedUsers = dataStore.loadData();
        
        assertEquals(1, loadedUsers.size());
        assertTrue(loadedUsers.containsKey("bob"));
        assertEquals("Bob", loadedUsers.get("bob").getFirstName());
    }
    
    @Test
    void testRoundTripSaveAndLoadPreservesData() {
        User user = new User("Charlie", "Brown", "charlie", "pwd123");
        user.addCash(new BigDecimal("500.00"));
        user.getSavingsAccount().deposit(new BigDecimal("1000.00"));
        
        Map<String, User> usersToSave = Map.of("charlie", user);
        dataStore.saveData(usersToSave);
        
        Map<String, User> loadedUsers = dataStore.loadData();
        User loadedUser = loadedUsers.get("charlie");
        
        assertNotNull(loadedUser);
        assertEquals("Charlie", loadedUser.getFirstName());
        assertEquals("Brown", loadedUser.getLastName());
        assertEquals(new BigDecimal("10500.00"), loadedUser.getCash());
    }
    
    // ============ CORRUPTION SCENARIOS ============
    
    @Test
    void testLoadDataWithMalformedUserCSV() throws IOException {
        // Create CSV with wrong number of fields
        String content = "alice,encpass,Alice\n" + // Missing fields
                        "bob,encpass,Bob,Jones,10000.00,0,0\n"; // Valid
        
        writeFile(testDbDir + "/users.csv", content);
        
        Map<String, User> users = dataStore.loadData();
        
        // Should load only valid record
        assertEquals(1, users.size());
        assertTrue(users.containsKey("bob"));
    }
    
    @Test
    void testLoadDataWithInvalidBigDecimal() throws IOException {
        String content = "alice,encpass,Alice,Smith,notanumber,0,0\n";
        writeFile(testDbDir + "/users.csv", content);
        
        Map<String, User> users = dataStore.loadData();
        
        assertEquals(0, users.size());
    }
    
    @Test
    void testLoadDataWithInvalidDateFormat() throws IOException {
        // Valid user but with invalid date
        String encryptedPwd = SecurityUtils.encrypt("password");
        String content = "alice," + encryptedPwd + ",Alice,Smith,10000.00,0,0,invalid-date,false,false,EE1234567890\n";
        writeFile(testDbDir + "/users.csv", content);
        
        Map<String, User> users = dataStore.loadData();
        
        // Should load user but with null lastLogin
        assertEquals(1, users.size());
        assertNull(users.get("alice").getLastLogin());
    }
    
    @Test
    void testLoadDataWithMissingUserFile() {
        Map<String, User> users = dataStore.loadData();
        
        assertNotNull(users);
        assertEquals(0, users.size());
    }
    
    @Test
    void testLoadDataWithEmptyCSVFile() throws IOException {
        writeFile(testDbDir + "/users.csv", "");
        
        Map<String, User> users = dataStore.loadData();
        
        assertNotNull(users);
        assertEquals(0, users.size());
    }
    
    @Test
    void testLoadDataWithExtraWhitespace() throws IOException {
        String encryptedPwd = SecurityUtils.encrypt("password");
        String content = "  alice  ,  " + encryptedPwd + "  ,  Alice  ,  Smith  ,  10000.00  ,  0  ,  0  \n";
        writeFile(testDbDir + "/users.csv", content);
        
        Map<String, User> users = dataStore.loadData();
        
        assertEquals(1, users.size());
        assertEquals("Alice", users.get("alice").getFirstName());
    }
    
    @Test
    void testLoadDataWithInvalidBooleanValues() throws IOException {
        String encryptedPwd = SecurityUtils.encrypt("password");
        String content = "alice," + encryptedPwd + ",Alice,Smith,10000.00,0,0,null,notabool,alsonotbool,EE1234567890\n";
        writeFile(testDbDir + "/users.csv", content);
        
        Map<String, User> users = dataStore.loadData();
        
        assertEquals(1, users.size());
        // Should default to false for invalid booleans
        assertFalse(users.get("alice").hasFraudWarning());
        assertFalse(users.get("alice").isFrozen());
    }
    
    @Test
    void testLoadDataWithNegativeBalances() throws IOException {
        String encryptedPwd = SecurityUtils.encrypt("password");
        String content = "alice," + encryptedPwd + ",Alice,Smith,-100.00,0,0\n";
        writeFile(testDbDir + "/users.csv", content);
        
        Map<String, User> users = dataStore.loadData();
        
        // Should skip record with negative balance
        assertEquals(0, users.size());
    }
    
    @Test
    void testLoadDataWithEmptyUsername() throws IOException {
        String encryptedPwd = SecurityUtils.encrypt("password");
        String content = "," + encryptedPwd + ",Alice,Smith,10000.00,0,0\n";
        writeFile(testDbDir + "/users.csv", content);
        
        Map<String, User> users = dataStore.loadData();
        
        assertEquals(0, users.size());
    }
    
    @Test
    void testLoadDataWithEmptyPassword() throws IOException {
        String content = "alice,,Alice,Smith,10000.00,0,0\n";
        writeFile(testDbDir + "/users.csv", content);
        
        Map<String, User> users = dataStore.loadData();
        
        assertEquals(0, users.size());
    }
    
    @Test
    void testLoadDataWithEmptyFirstName() throws IOException {
        String encryptedPwd = SecurityUtils.encrypt("password");
        String content = "alice," + encryptedPwd + ",,Smith,10000.00,0,0\n";
        writeFile(testDbDir + "/users.csv", content);
        
        Map<String, User> users = dataStore.loadData();
        
        assertEquals(0, users.size());
    }
    
    @Test
    void testEnsureDatabaseDirectoryCreatesDirectory() {
        dataStore.loadData(); // This should create the directory
        
        File dir = new File(testDbDir);
        assertTrue(dir.exists());
        assertTrue(dir.isDirectory());
    }
    
    @Test
    void testLoadDataSkipsPartiallyCorruptedFile() throws IOException {
        String encryptedPwd = SecurityUtils.encrypt("password");
        String validLine1 = "alice," + encryptedPwd + ",Alice,Smith,10000.00,0,0\n";
        String corruptedLine = "bob,baddata,incomplete\n";
        String validLine2 = "charlie," + encryptedPwd + ",Charlie,Brown,10000.00,0,0\n";
        
        writeFile(testDbDir + "/users.csv", validLine1 + corruptedLine + validLine2);
        
        Map<String, User> users = dataStore.loadData();
        
        // Should load 2 valid users and skip the corrupted one
        assertEquals(2, users.size());
        assertTrue(users.containsKey("alice"));
        assertTrue(users.containsKey("charlie"));
        assertFalse(users.containsKey("bob"));
    }
    
    // ============ HELPER METHODS ============
    
    private void writeFile(String path, String content) throws IOException {
        File file = new File(path);
        file.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
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
