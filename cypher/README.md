# Java Cypher Tool 🔐

A diverse command-line cryptography utility capable of encrypting and decrypting messages using classic substitution ciphers. This tool features a robust interactive loop and modular implementation of three distinct algorithms.

## Key Features ✨

- **Supported Algorithms 🧩**:
  - **ROT13**: A specific case of the Caesar cipher (shift by 13).
  - **Atbash**: Maps 'A' to 'Z', 'B' to 'Y', etc.
  - **Affine (Custom)**: Uses linear math functions for stronger obfuscation.
- **Interactive CLI**: Menu-driven interface for selecting operations and algorithms.
- **Input Validation**: Robust handling to ensure only valid operations and non-empty messages are processed.
- **Feedback System**: Alerts users when characters (like numbers or symbols) cannot be processed by specific ciphers.

## Algorithms Explained 📖

### 1. ROT13 🔄

A simple substitution cipher that replaces a letter with the 13th letter after it in the alphabet.

- **Encryption**: $x + 13 \pmod{26}$
- **Decryption**: Same as encryption (symmetric).

### 2. Atbash 🔁

A monoalphabetic substitution cipher originally used to encode the Hebrew alphabet.

- **Logic**: Reverses the alphabet (A ↔ Z).

### 3. Affine Cipher (The "Custom" Choice) 📐

A more complex monoalphabetic substitution cipher using linear algebra.

- **Encryption Function**: $E(x) = (ax + b) \pmod{26}$
  - Uses key pair: $a=5, b=8$
- **Decryption Function**: $D(x) = a^{-1}(x - b) \pmod{26}$
  - *Note*: Accurately calculates the modular multiplicative inverse ($21$ for $a=5$).

## Usage 🚀

Compile and run the tool from the command line:

```bash
javac CypherTool.java InputData.java
java CypherTool
```

**Example Session:**

```text
Welcome to the Cypher Tool!
Please select an operation:
1. Encrypt
2. Decrypt
$> 1

Please select a cypher:
1. Rot13
2. Atbash
3. Affine
$> 3

Enter the message:
$> hello

Encrypted message: rcllkt
```

## Technical Highlights 💡

- **Mathematical Correctness**: The Affine decryption logic handles Java's modulo operator quirks (which can return negative numbers) by ensuring positive results: `if (decryptedFn < 0) decryptedFn += 26;`.
- **Object-Oriented Data Transfer**: Uses an `InputData` class to cleanly encapsulate user inputs (Operation, Cypher, Message) and pass them between UI and logic layers.
- **Code Reuse**: Efficiently implements symmetric algorithms (ROT13, Atbash) by reusing the encryption method for decryption.

## What I Learned 🧠

- **Modular Arithmetic**: Implementing the Affine cipher required a deep dive into modular inverses and linear equations.
- **Input Validation Loops**: Building `while(true)` loops to trap invalid user input until correct data is received.
- **Clean Code**: Separating the "Input Gathering" logic (`getInput`) from the "Business Logic" (`main` control flow).

## Project Structure 📂

```text
cypher/
├── CypherTool.java    # Main control flow and cipher algorithms
├── InputData.java     # Data Transfer Object (DTO) for user input
└── Main.java          # Entry point wrapper
```

## Contributors 👥

This was a group project developed by:

- **Lauri Lepik** (👑 Team Lead)
- **Joosep Verbu**
- **Rain Kuuseorg**

---
*Developed as a group project for the **[kood/Nooremarendaja](https://kood.tech/kood-nooremarendaja/)** curriculum (Free, **[NextGenEU](https://kood.tech/meist/toetused/)** funded, 5-month intensive).*
