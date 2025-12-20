import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;


public class Database {
    // Cached paths - computed once when class loads
    private static final Path PROJECT_ROOT = findProjectRoot();
    private static final Path COLLECTIONS_PATH = PROJECT_ROOT.resolve("collections");

    //Finds the project root by checking if collections/ exists in current dir or parent dir. Allows for running from src/ as well (testing etc)
    private static Path findProjectRoot() {
        Path currentDir = Paths.get(System.getProperty("user.dir"));
        
        // Check if collections folder exists in current directory
        if (Files.isDirectory(currentDir.resolve("collections"))) {
            return currentDir;
        }
        
        // Otherwise, try parent directory (when running from src/)
        Path parentDir = currentDir.getParent();
        if (parentDir != null && Files.isDirectory(parentDir.resolve("collections"))) {
            return parentDir;
        }
        
        // Default to current directory (collections will be created here)
        return currentDir;
    }

    private static Path getDatabasePath(String dbName) {
        return COLLECTIONS_PATH.resolve(dbName + ".txt");
    }

    //Write data to file
    private static void writeDatabase(String dbName, List<String> lines) {
        try {
            Files.write(getDatabasePath(dbName), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Reads all lines from the database file.
    public static List<String> readDatabaseLines(String dbName) {
        try {
            return new ArrayList<>(Files.readAllLines(getDatabasePath(dbName)));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();  // Return empty mutable list on error
        }
    }

    //Check if database exists
    public static boolean databaseExists(String dbName) {
        return Files.exists(getDatabasePath(dbName));
    }

    //Create database if missing
    public static void createDatabase(String dbName) {
        try {
            Files.createFile(getDatabasePath(dbName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Delete database
    public static void deleteDatabase(String dbName) {
        try {
            Files.delete(getDatabasePath(dbName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //Display database contents
    public static void displayDatabase(String dbName) {
        List<String> lines = readDatabaseLines(dbName);
        int totalLines = lines.size();
        
        // Calculate digits needed for line numbers
        int digitCount = String.valueOf(totalLines).length();
        
        // Create format string 
        String format = "%0" + digitCount + "d";
        
        int lineIndex = 1;
        for (String line : lines) {
            System.out.println(String.format(format, lineIndex) + " - " + line);
            lineIndex++;
        }
    }

    //Add entry to database
    public static void addEntry(String dbName, String entry) {
        List<String> lines = readDatabaseLines(dbName);
        lines.add(entry);
        writeDatabase(dbName, lines);
    }

    //Delete specified entry
    public static void deleteEntry(String dbName, int entryNumber) {
        List<String> lines = readDatabaseLines(dbName);
        int index = entryNumber - 1;
        if (index >= 0 && index < lines.size()) {
            lines.remove(index);
            writeDatabase(dbName, lines);
        }
    }

    //Get a single entry by number
    public static String getEntry(String dbName, int entryNumber) {
        List<String> lines = readDatabaseLines(dbName);
        int index = entryNumber - 1;
        if (index >= 0 && index < lines.size()) {
            return lines.get(index);
        }
        return null;
    }

    //Edit entry - accepts the new value directly (CLI handles user input)
    public static void editEntry(String dbName, int entryNumber, String newEntry) {
        List<String> lines = readDatabaseLines(dbName);
        int index = entryNumber - 1;
        if (index >= 0 && index < lines.size()) {
            lines.set(index, newEntry);
            writeDatabase(dbName, lines);
        }
    }

    //Swap two entries by position
    public static void swapEntries(String dbName, int pos1, int pos2) {
        List<String> lines = readDatabaseLines(dbName);
        int idx1 = pos1 - 1;
        int idx2 = pos2 - 1;
        if (idx1 >= 0 && idx1 < lines.size() && idx2 >= 0 && idx2 < lines.size()) {
            java.util.Collections.swap(lines, idx1, idx2);
            writeDatabase(dbName, lines);
        }
    }

    //Move entry from one position to another
    public static void moveEntry(String dbName, int from, int to) {
        List<String> lines = readDatabaseLines(dbName);
        int fromIdx = from - 1;
        int toIdx = to - 1;
        if (fromIdx >= 0 && fromIdx < lines.size() && toIdx >= 0 && toIdx < lines.size()) {
            String entry = lines.remove(fromIdx);
            lines.add(toIdx, entry);
            writeDatabase(dbName, lines);
        }
    }
}