package game;

// Generates colored feedback for Wordle guesses using ANSI codes.
public class FeedbackGenerator {

    // ANSI color codes
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String WHITE = "\u001B[37m";
    private static final String RESET = "\u001B[0m";

    // Generates colored feedback for a guess.
    // Green = correct position, Yellow = wrong position, White = not in word.
    public String generateFeedback(String guess, String secretWord) {
        int length = secretWord.length();
        StringBuilder feedback = new StringBuilder();
        boolean[] secretUsed = new boolean[length];

        // Mark exact matches
        for (int i = 0; i < length; i++) {
            if (guess.charAt(i) == secretWord.charAt(i)) {
                secretUsed[i] = true;
            }
        }

        // Build colored feedback
        for (int i = 0; i < length; i++) {
            char c = Character.toUpperCase(guess.charAt(i));

            if (guess.charAt(i) == secretWord.charAt(i)) {
                feedback.append(GREEN).append(c).append(RESET);
            } else if (findYellowMatch(guess.charAt(i), secretWord, secretUsed)) {
                feedback.append(YELLOW).append(c).append(RESET);
            } else {
                feedback.append(WHITE).append(c).append(RESET);
            }
        }

        return feedback.toString();
    }

    private boolean findYellowMatch(char guessChar, String secretWord, boolean[] secretUsed) {
        for (int j = 0; j < secretWord.length(); j++) {
            if (!secretUsed[j] && guessChar == secretWord.charAt(j)) {
                secretUsed[j] = true;
                return true;
            }
        }
        return false;
    }
}
