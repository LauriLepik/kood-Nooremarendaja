# Java Algorithm Highlights 🧩

During the **Java Sprint** of the [kood/Nooremarendaja](https://kood.tech/kood-nooremarendaja/) curriculum, I solved over **60 daily algorithmic challenges** (combining online platform exercises and local Git struggles). These exercises focused on raw logic, data structures, and algorithmic thinking without the use of external libraries.

This directory contains three selected highlights that demonstrate different aspects of core programming.

## 1. Recursive Parentheses Balance Checker ⚖️

**File:** [`ParenthesesBalanceChecker.java`](./ParenthesesBalanceChecker.java)

### The Challenge 🎯

Implement a method to check if the parentheses in a string are balanced (e.g., `(())` is valid, `)(` is invalid).

* **Constraint:** You **cannot** use loops (`for`, `while`, `do-while`). The solution must be purely recursive.

### Key Logic 🧠

The solution is **purely recursive** (relying on the call stack instead of an explicit `Stack<Character>` data structure). It implements a **fail-fast** optimization: if the balance counter ever drops below zero (e.g., trying to close a parenthesis that wasn't opened), the recursion aborts immediately with `false`, significantly speeding up validation for invalid strings.

### Usage 💻

```java
ParenthesesBalanceChecker checker = new ParenthesesBalanceChecker();
System.out.println(checker.isBalanced("(())"));     // true
System.out.println(checker.isBalanced("(()"));      // false
System.out.println(checker.isBalanced(")( "));      // false
```

---

## 2. Calendar Builder 📅

**File:** [`CalendarBuilder.java`](./CalendarBuilder.java)

### The Challenge 🎯

Generate a visually correct, formatted string representation of a calendar month for any given year.

* **Requirements:** Precise whitespace formatting (single vs double digit padding), handling leap years, and correctly aligning the weekdays (Mon-Sun).

### Key Logic 🧠

* **Efficient String Construction**: Uses a single `StringBuilder` to construct the entire grid, avoiding the memory overhead of concatenation in loops.

* **Conditional Formatting**: Implements complex boundary logic to handle start-of-month offsets and end-of-week newlines (ensuring no trailing newlines if the month ends exactly on a Sunday).

### Usage 💻

```java
// buildCalendar("May", 1993);
MAY 1993
Mon Tue Wed Thu Fri Sat Sun
                      1   2
  3   4   5   6   7   8   9
 10  11  12  13  14  15  16
 17  18  19  20  21  22  23
 24  25  26  27  28  29  30
 31
```

---

## 3. Combinations of N 🔢

**File:** [`Combinations.java`](./Combinations.java)

### The Challenge 🎯

Reveal all possible combinations of ascending digits of length `n`.

* **Goal:** Return a list of strings where each string represents a unique combination (e.g., `012`, `013`...).

### Key Logic 🧠

The solution uses **recursive backtracking** with **search space pruning**. By passing `i + 1` to the next recursive call, the algorithm guarantees the "ascending" property without needing a post-validation step. This reduces the time complexity effectively from exponential to combinatorial.

### Usage 💻

```java
Combinations comb = new Combinations();
System.out.println(comb.combN(3));
// Output: ["012", "013", ... "789"]
```

---
*Developed as part of the **Java Sprint** in the **[kood/Nooremarendaja](https://kood.tech/kood-nooremarendaja/)** curriculum (Free, **[NextGenEU](https://kood.tech/meist/toetused/)** funded, 5-month intensive).*
