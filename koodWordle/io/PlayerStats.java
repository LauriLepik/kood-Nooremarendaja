package io;

import model.UserStats;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

// Handles reading and writing player statistics to CSV file.
public class PlayerStats {
    private final Path STATS_FILE = Paths.get("stats.csv");
    private final Map<String, UserStats> allUsers = new HashMap<>();

    // ==================== PUBLIC METHODS ====================

    public UserStats loadStats(String username) {
        readAllUsersFromFile();
        
        if (allUsers.containsKey(username)) {
            return allUsers.get(username);
        } else {
            UserStats newUser = new UserStats(username);
            allUsers.put(username, newUser);
            return newUser;
        }
    }

    public void saveStats(UserStats stats) {
        allUsers.put(stats.getUsername(), stats);
        writeAllUsersToFile();
    }

    // ==================== FILE READING ====================

    private void readAllUsersFromFile() {
        allUsers.clear();
        
        if (!Files.exists(STATS_FILE)) {
            return;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(STATS_FILE)) {
            String line;
            UserStats currentUser = null;
            
            while ((line = reader.readLine()) != null) {
                currentUser = parseLine(line, currentUser);
            }
        } catch (IOException e) {
            System.out.println("Error loading stats: " + e.getMessage());
        }
    }

    private UserStats parseLine(String line, UserStats currentUser) {
        String[] parts = line.split(",", 2);
        if (parts.length != 2) {
            return currentUser;
        }
        
        String key = parts[0].trim();
        String value = parts[1].trim();
        
        if (key.equals("username")) {
            currentUser = new UserStats(value);
            allUsers.put(value, currentUser);
        } else if (currentUser != null) {
            parseUserField(currentUser, key, value);
        }
        
        return currentUser;
    }

    private void parseUserField(UserStats user, String key, String value) {
        switch (key) {
            case "number of attempts" -> user.setNumberOfAttempts(parseIntOrDefault(value, 0));
            case "games won" -> user.setGamesWon(parseIntOrDefault(value, 0));
            case "games lost" -> user.setGamesLost(parseIntOrDefault(value, 0));
            case "secret word" -> user.setLastSecretWord(value);
        }
    }

    // ==================== FILE WRITING ====================

    private void writeAllUsersToFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(STATS_FILE)) {
            for (UserStats user : allUsers.values()) {
                writeUserData(writer, user);
            }
        } catch (IOException e) {
            System.out.println("Error saving stats: " + e.getMessage());
        }
    }

    private void writeUserData(BufferedWriter writer, UserStats user) throws IOException {
        writer.write("username," + user.getUsername());
        writer.newLine();
        writer.write("secret word," + user.getLastSecretWord());
        writer.newLine();
        writer.write("number of attempts," + user.getNumberOfAttempts());
        writer.newLine();
        writer.write("games won," + user.getGamesWon());
        writer.newLine();
        writer.write("games lost," + user.getGamesLost());
        writer.newLine();
    }

    // ==================== MISC HELPERS ====================

    private int parseIntOrDefault(String value, int defaultValue) {
        if (value == null || value.equals("null")) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
