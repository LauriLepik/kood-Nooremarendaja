package model;

//Data class representing a user's game statistics.

public class UserStats {
    private String username;
    private String lastSecretWord;
    private int numberOfAttempts;
    private int gamesWon;
    private int gamesLost;

    // ==================== CONSTRUCTORS ====================

    public UserStats() {
        this.username = "null";
        this.lastSecretWord = "null";
        this.numberOfAttempts = 0;
        this.gamesWon = 0;
        this.gamesLost = 0;
    }

    public UserStats(String username) {
        this();
        this.username = username;
    }

    // ==================== STAT MODIFIERS ====================

    public void addAttempts(int attempts) {
        this.numberOfAttempts += attempts;
    }

    public void addWin() {
        this.gamesWon++;
    }

    public void addLoss() {
        this.gamesLost++;
    }

    // ==================== CALCULATED GETTERS ====================

    public int getGamesPlayed() {
        return gamesWon + gamesLost;
    }

    public double getAverageAttempts() {
        int played = getGamesPlayed();
        return played > 0 ? (double) numberOfAttempts / played : 0.0;
    }

    // ==================== GETTERS & SETTERS ====================

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastSecretWord() {
        return lastSecretWord;
    }

    public void setLastSecretWord(String lastSecretWord) {
        this.lastSecretWord = lastSecretWord;
    }

    public int getNumberOfAttempts() {
        return numberOfAttempts;
    }

    public void setNumberOfAttempts(int numberOfAttempts) {
        this.numberOfAttempts = numberOfAttempts;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public int getGamesLost() {
        return gamesLost;
    }

    public void setGamesLost(int gamesLost) {
        this.gamesLost = gamesLost;
    }
}
