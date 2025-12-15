import game.GameEngine;
import io.PlayerStats;
import io.WordleDB;
import java.util.Scanner;
import model.UserStats;

// Main entry point for the Wordle game.
public class WordleGame {
    private Scanner scanner;
    private WordleDB wordleDB;
    private PlayerStats playerStats;

    public static void main(String[] args) {
        WordleGame game = new WordleGame();
        game.run(args);
    }

    private void run(String[] args) {
        initialize();
        
        int length = 0;
        int argIndex = 0;
        
        // Parse Flags
        if (args.length > argIndex && (args[argIndex].equals("-l") || args[argIndex].equals("--length"))) {
            if (args.length > argIndex + 1) {
                try {
                    length = Integer.parseInt(args[argIndex + 1]);
                    argIndex += 2;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid length argument.");
                    return;
                }
            } else {
                System.out.println("Missing length argument.");
                return;
            }
        }
        
        // Parse Main Args (Remaining)
        String[] remainingArgs = new String[args.length - argIndex];
        System.arraycopy(args, argIndex, remainingArgs, 0, remainingArgs.length);
        
        if (!validateArgs(remainingArgs)) {
            return;
        }
        
        // If length not provided via flag, ask for it
        if (length == 0) {
            length = promptLength();
            if (length == 0) return; // EOF
        }

        // Initial Selection
        boolean wordSelected;
        if (remainingArgs.length == 1) {
            // Provided via CLI
            wordSelected = setupInitialWord(remainingArgs, length);
            if (!wordSelected) {
                 System.out.println("Failed to setup game with length " + length);
                 return;
            }
        } else {
            // Interactive
            wordSelected = selectNextWord(length);
            if (!wordSelected) return; // EOF
        }
        
        String username = promptUsername();
        if (username == null) {
            return; // EOF
        }
        
        // Game Loop
        while (true) {
            UserStats stats = playerStats.loadStats(username);
            GameEngine engine = new GameEngine(scanner, wordleDB);
            
            if (!engine.play()) {
                return; // EOF or exit during game
            }
            
            engine.updateStats(stats);
            playerStats.saveStats(stats);
            promptAndShowStats(stats);
            
            if (!askPlayAgain()) {
                break;
            }
            
            // For replay, ask length again
            length = promptLength();
            if (length == 0) break;
            
            if (!selectNextWord(length)) {
                break; // EOF or exit during selection
            }
        }
        
        waitForExit();
    }
    
    // ... promptLength method ...

    private boolean validateArgs(String[] args) {
        if (args.length > 1) {
            System.out.println("Too many arguments. Provide at most one (index or 'random').");
            return false;
        }
        
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("random")) {
                return true;
            }
            try {
                Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid command-line argument. Please launch with a valid number.");
                return false;
            }
        }
        
        return true;
    }

    private int promptLength() {
        while (true) {
            System.out.print("Enter word length (e.g. 5): ");
            if (!scanner.hasNextLine()) return 0;
            
            String input = scanner.nextLine().trim();
            try {
                int len = Integer.parseInt(input);
                if (len < 1) { // Basic sanity check
                     System.out.println("Invalid length.");
                     continue;
                }
                return len;
            } catch (NumberFormatException e) {
                 System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private boolean setupInitialWord(String[] args, int length) {
        int wordIndex;
        if (args.length == 1 && args[0].equalsIgnoreCase("random")) {
            wordIndex = getRandomIndex(length);
            if (wordIndex == -1) return false; // Error printed in getRandomIndex
            System.out.println("Random mode selected. Word #" + wordIndex);
        } else {
            wordIndex = Integer.parseInt(args[0]);
        }
        return wordleDB.fetchSecretWord(wordIndex, length);
    }

    private int getRandomIndex(int length) {
        int count = wordleDB.getWordCount(length);
        if (count == 0) {
            System.out.println("Error: No words found with length " + length);
            return -1;
        }
        return (int) (Math.random() * count);
    }

    private boolean askPlayAgain() {
        while (true) {
            System.out.print("Play again? (yes/no): ");
            if (!scanner.hasNextLine()) return false;
            
            String input = scanner.nextLine().trim().toLowerCase();
            
            if (input.equals("yes") || input.equals("y")) {
                return true;
            } else if (input.equals("no") || input.equals("n") || 
                       input.equals("exit") || input.equals("e") || input.isEmpty()) {
                return false;
            } else {
                System.out.println("Invalid input. Please enter 'yes' or 'no'.");
            }
        }
    }

    private boolean selectNextWord(int length) {
        while (true) {
            System.out.print("Enter word index or 'random': ");
            if (!scanner.hasNextLine()) return false;
            
            String input = scanner.nextLine().trim();
            int wordIndex;
            
            if (input.equalsIgnoreCase("random")) {
                wordIndex = getRandomIndex(length);
                if (wordIndex == -1) {
                     return false; 
                }
                System.out.println("Random mode selected. Word #" + wordIndex);
            } else {
                try {
                    wordIndex = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number or 'random'.");
                    continue;
                }
            }
            
            if (wordleDB.fetchSecretWord(wordIndex, length)) {
                return true;
            }
            System.out.println("Invalid word index for length " + length + ". Please try again.");
        }
    }


    private void initialize() {
        scanner = new Scanner(System.in);
        wordleDB = new WordleDB();
        playerStats = new PlayerStats();
    }



    private String promptUsername() {
        while (true) {
            System.out.print("Enter your username: ");
            
            if (!scanner.hasNextLine()) {
                return null;
            }
            
            String username = scanner.nextLine().trim();
            
            if (!username.isEmpty()) {
                return username;
            }
        }
    }

    private void promptAndShowStats(UserStats stats) {
        System.out.print("Do you want to see your stats? (yes/no): ");
        
        if (!scanner.hasNextLine()) {
            return;
        }
        
        String response = scanner.nextLine().trim().toLowerCase();
        
        if (response.equals("yes")) {
            System.out.println("Stats for " + stats.getUsername() + ":");
            System.out.println("Games played: " + stats.getGamesPlayed());
            System.out.println("Games won: " + stats.getGamesWon());
            System.out.printf("Average attempts per game: %.1f%n", stats.getAverageAttempts());
        }
    }

    private void waitForExit() {
        System.out.println("Press Enter to exit...");
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }
}
