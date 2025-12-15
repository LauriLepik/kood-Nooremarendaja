package game;

import io.WordleDB;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import model.UserStats;

// Handles the core game loop, guess validation, and letter tracking.
public class GameEngine {
    private static final int MAX_ATTEMPTS = 6;
    
    private final Scanner scanner;
    private final WordleDB wordleDB;
    private final FeedbackGenerator feedbackGenerator;
    
    private Set<Character> remainingLetters;
    private int attemptsRemaining;
    private int attemptsUsed;
    private boolean won;

    public GameEngine(Scanner scanner, WordleDB wordleDB) {
        this.scanner = scanner;
        this.wordleDB = wordleDB;
        this.feedbackGenerator = new FeedbackGenerator();
        this.attemptsUsed = 0;
        this.won = false;
        initializeLetters();
    }
    
    private String getSecretWord() {
        return wordleDB.getSecretWord();
    }

    private void initializeLetters() {
        attemptsRemaining = MAX_ATTEMPTS;
        remainingLetters = new HashSet<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            remainingLetters.add(c);
        }
    }

    // ==================== MAIN GAME LOOP ====================

    public boolean play() {
        int length = getSecretWord().length();
        System.out.println("Welcome to Wordle! Guess the " + length + "-letter word.");
        
        while (attemptsRemaining > 0) {
            String guess = getValidGuess();
            if (guess == null) {
                return false; // EOF
            }
            
            attemptsRemaining--;
            attemptsUsed++;
            
            if (guess.equals(getSecretWord())) {
                System.out.println("Congratulations! You've guessed the word correctly.");
                won = true;
                return true;
            }
            
            String feedback = feedbackGenerator.generateFeedback(guess, getSecretWord());
            System.out.println("Feedback: " + feedback);
            
            updateRemainingLetters(guess);
            showRemainingLetters();
            System.out.println("Attempts remaining: " + attemptsRemaining);
        }
        
        System.out.println("Game over. The correct word was: " + getSecretWord());
        return true;
    }

    // ==================== INPUT VALIDATION ====================

    private String getValidGuess() {
        int length = getSecretWord().length();
        while (true) {
            System.out.print("Enter your guess:  ");
            
            if (!scanner.hasNextLine()) {
                return null;
            }
            
            String guess = scanner.nextLine().trim().toLowerCase();
            
            if (guess.length() != length) {
                System.out.println("Your guess must be exactly " + length + " letters long.");
                continue;
            }
            
            if (!guess.matches("^[a-z]+$")) {
                System.out.println("Your guess must only contain lowercase letters.");
                continue;
            }
            
            if (!wordleDB.isWordInList(guess)) {
                System.out.println("Word not in list. Please enter a valid word.");
                continue;
            }
            
            return guess;
        }
    }

    // ==================== LETTER TRACKING ====================

    private void updateRemainingLetters(String guess) {
        for (char c : guess.toUpperCase().toCharArray()) {
            if (!getSecretWord().toUpperCase().contains(String.valueOf(c))) {
                remainingLetters.remove(c);
            }
        }
    }

    private void showRemainingLetters() {
        StringBuilder sb = new StringBuilder("Remaining letters: ");
        for (char c = 'A'; c <= 'Z'; c++) {
            if (remainingLetters.contains(c)) {
                sb.append(c).append(" ");
            }
        }
        System.out.println(sb.toString().trim());
    }

    // ==================== RESULT GETTERS ====================

    public boolean hasWon() {
        return won;
    }

    public int getAttemptsUsed() {
        return attemptsUsed;
    }

    public void updateStats(UserStats stats) {
        stats.addAttempts(attemptsUsed);
        if (won) {
            stats.addWin();
        } else {
            stats.addLoss();
        }
        stats.setLastSecretWord(getSecretWord());
    }
}
