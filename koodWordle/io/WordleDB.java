package io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

public class WordleDB {
    private final Path WORDS_FILE = Paths.get("wordle-words.txt");
    private String secretWord;

    // ==================== PUBLIC METHODS ====================

    public String getSecretWord() {
        return secretWord;
    }



    public int getWordCount(int length) {
        if (!fileExists()) {
            return 0;
        }
        try (Stream<String> lines = Files.lines(WORDS_FILE)) {
            return (int) lines
                    .map(String::trim)
                    .filter(w -> w.length() == length)
                    .count();
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return 0;
        }
    }

    public boolean fetchSecretWord(int wordleNumber, int length) {
        if (!fileExists()) {
            return false;
        }
        
        try (Stream<String> lines = Files.lines(WORDS_FILE)) {
            Optional<String> word = lines
                    .map(String::trim)
                    .filter(w -> w.length() == length)
                    .skip(wordleNumber)
                    .findFirst();
            
            if (word.isPresent()) {
                secretWord = word.get().toLowerCase();
                return true;
            }
            return false;
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return false;
        }
    }

    public boolean isWordInList(String word) {
        if (!fileExists()) {
            return false;
        }
        
        try (Stream<String> lines = Files.lines(WORDS_FILE)) {
            return lines
                    .map(String::trim)
                    .anyMatch(line -> line.equalsIgnoreCase(word));
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return false;
        }
    }

    // ==================== HELPERS ====================

    private boolean fileExists() {
        return Files.exists(WORDS_FILE);
    }
}
