# Java Notes CLI Tool 📝

A robust, interactive command-line interface (CLI) application designed for efficient note management. This tool allows users to create, view, organize, and persistent text-based notes across multiple collections.

This project focuses on clean architecture, file persistence, and user-centric CLI design.

## Features ✨

- **Collection Management**: Create and switch between multiple named collections (e.g., `packing_list`, `ideas`, `shopping`).
- **Persistent Storage**: Collections are automatically saved as `.txt` files in a `collections/` directory, ensuring data survives between runs.
- **Rich Note Operations**:
  - **Add & Edit**: Quickly append new thoughts or refine existing ones.
  - **Reorder**: Organize your list with **Swap** and **Move** commands.
  - **Delete**: Remove completed or irrelevant items.
- **Robust Error Handling**: Gracefully handles invalid inputs and file operations without crashing.

## Bonus Features 🎁

Beyond the standard requirements, this application includes several advanced capabilities:

- **Note Editing**: Users can modify the content of existing notes in-place.
- **Advanced Reordering**: Support for both **swapping** notes and **moving** them to specific positions.
- **Dynamic Collection Switching**: Create new collections or switch between existing ones directly during runtime without restarting the application.

## Technical Highlights 💡

This project was built to demonstrate proficiency in Java and CLI development features:

- **Defensive Programming**: The application gracefully handles edge cases, such as a collection file being deleted externally while the program is running. The `CLI.java` includes specific logic (`handleMissingCollection`) to detect this and offer recovery options (recreate or switch).
- **Smart Path Resolution**: `Database.java` dynamically locates the `collections/` directory, allowing the app to run correctly whether executed from the project root or the `src` directory (great for testing!).
- **Separation of Concerns**: The codebase follows update strict modularity:
  - `CLI.java` handles all user inputs and outputs.
  - `Operations.java` contains the business logic for manipulating notes.
  - `Database.java` manages low-level file storage and retrieval using `java.nio`.
- **Modern I/O**: Utilizes Java's NIO (Non-blocking I/O) package (`Files`, `Paths`) for efficient file handling, preferring it over legacy `java.io` classes.

## What I Learned 🧠

Working on this project reinforced key software engineering concepts:

- **State Management**: Keeping the in-memory application state synchronized with the persistent file storage.
- **User Experience (UX) in CLI**: designing a clear, navigable menu system that guides the user and prevents invalid states.
- **Collaborative Development**: Coordinating contributions and code style across a 3-person team efficiently.

## Potential Future Improvements 🔮

Potential features that could be implemented on top of existing functionality:

- **Search Functionality**: Ability to find notes matching a specific keyword.
- **Task completion**: Mark notes as "done" (e.g., `[x]`) without deleting them.
- **Export**: Save collections to other formats like JSON or Markdown.

## Prerequisites ⚙️

- **Java JDK 17** or higher.
- A terminal or command prompt.

## Installation 📥

1. **Clone the repository**:

   ```bash
   git clone <repository-url>
   cd notesApp
   ```

2. **Compile the source code**:

   ```bash
   javac -d out ./src/*.java
   ```

## Usage 🚀

The application requires exactly one argument: the name of the collection you want to manage.

**Syntax:**

```bash
java -cp out Main [COLLECTION]
```

### Examples 📌

**1. Manage your packing list:**

```bash
java -cp out Main packing_list
```

*Creates `collections/packing_list.txt` if it doesn't exist.*

**2. Manage your shopping list:**

```bash
java -cp out Main shopping
```

**3. View Help:**

```bash
java -cp out Main --help
```

Output:

```text
Usage: java Main [COLLECTION]

This tool allows users to manage short single-line notes within a collection.

Options:
-h, --help       Show this help message and exit
[COLLECTION]     The name of the collection to manage
```

## Project Structure 📂

The codebase is organized into logical components:

```text
notesApp/
├── src/
│   ├── Main.java        # Entry point: Argument parsing & startup
│   ├── CLI.java         # User Interface: Menus and user interaction
│   ├── Operations.java  # Logic: Add, Edit, Delete, Reorder algorithms
│   └── Database.java    # Persistence: File I/O for collections
├── collections/         # Data Storage: Text files for each collection
└── README.md
```

## Data Management 💾

- **Collections**: Each collection is stored as a separate `.txt` file in the `collections/` folder.
- **Persistence**: Changes are saved immediately to disk. You can modify files externally, and the tool will read the updates on next load.

## Contributors 👥

This was a group project developed by:

- **[Lauri Lepik](https://github.com/LauriLepik)** (👑 Team Lead)
- **Kaarel Leib**
- **Martin Rahusoov**

---
*Developed as a group project for the **[kood/Nooremarendaja](https://kood.tech/kood-nooremarendaja/)** curriculum (Free, **[NextGenEU](https://kood.tech/meist/toetused/)** funded, 5-month intensive).*
