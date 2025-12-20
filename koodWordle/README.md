# Java CLI Wordle 🟩

A robust, feature-rich command-line implementation of the popular Wordle game, written in Java.
This project demonstrates clean Object-Oriented compliant code, handling of standard streams (stdin/stdout), file I/O for consistency, and ANSI colorized output for a rich terminal experience.

## Features ✨

- **Classic Gameplay**: Guess the hidden word in 6 tries.
- **Rich Visual Feedback**: An interactive terminal UI using ANSI escape codes for Green/Yellow/White feedback.
- **Advanced Game Modes**:
  - **Random Mode**: Play endless games with randomly selected words.
  - **Campaign/Replay Mode**: Launch specific word puzzles by index.
  - **Multi-Length Support**: Challenge yourself with 5, 6, 7 (or more) letter words!
- **Data Persistence**:
  - **User Stats**: Tracks your games played, win rate, and streak across sessions (saved to `stats.csv`).
  - **Instant Replay**: Easily start a new game immediately after finishing one without restarting the app.

## Prerequisites ⚙️

- **Java JDK 17** or higher.
- A terminal that supports ANSI escape codes cases (e.g., VS Code, PowerShell 7+, Bash, iTerm2).

## Installation 📥

1. **Clone the repository**:

   ```bash
   git clone https://github.com/Startkood/koodWordle.git
   cd koodWordle
   ```

2. **Compile the source code**:

   ```bash
   javac WordleGame.java
   ```

3. **Setup Word List**:
   The `wordle-words.txt` file is not included in the repository (gitignored).
   - Create a file named `wordle-words.txt` in the root directory.
   - Paste a list of words into it (one word per line).
   - **[Example Word List Source](https://gist.github.com/dracos/dd0668f281e685bad51479e5acaadb93)** (Note: This list contains *only* 5-letter words).

## Usage 🚀

You can launch the game in interactive mode or using command-line arguments for quick setup.

### 1. Interactive Mode 🎮

Simply run the program. It will guide you through setting up the game.

```bash
java WordleGame
```

### 2. Quick Start Flags 🚩

You can skip the setup prompts by passing arguments:

**Syntax:**

```bash
java WordleGame [-l <length> | --length <length>] [word_index | random]
```

**Examples:**

- **Play a random 5-letter game (Standard):**

  ```bash
  java WordleGame -l 5 random
  ```

- **Play a random 6-letter game (Hard Mode):**

  ```bash
  java WordleGame --length 6 random
  ```

- **Play a specific puzzle (e.g., Word #10):**

  ```bash
  java WordleGame 10
  ```

## Gameplay Rules 📜

The goal is to guess the hidden secret word in 6 tries.
After each guess, the color of the tiles will change to show how close your guess was to the word.

- 🟩 **Green**: The letter is in the word and in the **correct spot**.
- 🟨 **Yellow**: The letter is in the word but in the **wrong spot**.
- ⬜ **White**: The letter is **not** in the word in any spot.

## Project Structure 📂

The codebase follows a modular design separating concerns between game logic, I/O, and data models.

```text
koodWordle/
├── WordleGame.java           # Entry point: Handles CLI args and orchestrates game flow
├── wordle-words.txt          # Database of valid words
├── stats.csv                 # Persistent user statistics
├── game/
│   ├── GameEngine.java       # Core Game Loop: Validates guesses, manages turns
│   └── FeedbackGenerator.java# Logic for generating colorful ANSI feedback
├── io/
│   ├── WordleDB.java         # Handles loading words from the text file
│   └── PlayerStats.java      # Manages reading/writing statistics to CSV
└── model/
    └── UserStats.java        # Data model for player statistics
```

## Data Management 💾

- **Word List**: The game requires a `wordle-words.txt` file in the root directory. This file should contain a list of valid words (one per line).
- **Statistics**: Player stats (wins/losses/attempts) are automatically saved to `stats.csv`.

## Technical Highlights 💡

This project showcases several advanced Java implementation details:

- **Custom CLI Argument Parsing**: Implemented a robust manual argument parser `WordleGame.java` to handle flags (`-l`, `--length`) and positional arguments without relying on external libraries.
- **ANSI Escape Codes**: Directly manipulates terminal output colors in `FeedbackGenerator.java` to create an immersive visual experience.
- **Data Persistence Strategy**: Uses a custom CSV-like format in `PlayerStats.java` to persist complex user state (wins, losses, last secret word, attempts) directly to disk.
- **Modular Architecture**: Clean separation between the Game Engine (logic), I/O (persistence), and Model (state), making the codebase easy to test and extend.

## What I Learned 🧠

Building this game utilized several core programming concepts:

- **Object-Oriented Design**: Structuring a complex game into interacting objects (`GameEngine`, `UserStats`, `WordleDB`).
- **Stream API & Memory Efficiency**: `WordleDB.java` uses `Files.lines()` to process the word list lazily. This means the game can handle huge dictionary files without loading the entire file into memory, demonstrating efficient resource management.
- **File I/O**: Using `java.nio` for robust file reading and writing.
- **Algorithm Design**: implementing the logic to correctly color-code guesses (handling duplicate letters correctly is trickier than it looks!).

## Potential Future Improvements 🔮

- **Difficulty Modes**: implementing a "Hard Mode" that forces users to use revealed hints.
- **GUI Version**: Porting the game logic to a JavaFX or Swing interface.
- **Global Leaderboard**: A server-side component to track high scores across different players.

## Creator 👨‍💻

**[Lauri Lepik](https://github.com/LauriLepik)**

---
*Developed as a solo project for the **[kood/Nooremarendaja](https://kood.tech/kood-nooremarendaja/)** curriculum (Free, **[NextGenEU](https://kood.tech/meist/toetused/)** funded, 5-month intensive).*
