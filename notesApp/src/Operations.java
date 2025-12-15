import java.util.List;
import java.util.Scanner;

//Handles individual note operations.
public class Operations {

    //Show all notes in the collection.
    public static void showNotes(String collection) {
        List<String> notes = Database.readDatabaseLines(collection);
        if (notes.isEmpty()) {
            System.out.println("Collection \"" + collection + "\" is empty. Add some notes first!");
        } else {
            System.out.println();
            System.out.println("Notes:");
            Database.displayDatabase(collection);
            System.out.println();
            System.out.println("---");
        }
    }

    //Add a new note to the collection.
    public static void addNote(String collection, Scanner scanner) {
        if (!Database.databaseExists(collection)) {
            Database.createDatabase(collection);
        }
        System.out.println();
        System.out.println("Enter the note:");
        System.out.print("$> ");
        String note = scanner.nextLine().trim();
        if (note.isEmpty()) {
            System.out.println("Cannot add an empty note.");
            return;
        }
        Database.addEntry(collection, note);
        System.out.println();
        System.out.println("\"" + note + "\" added to " + collection);
        System.out.println();
        System.out.println("---");
    }

    //Edit an existing note.
    public static void editNote(String collection, Scanner scanner) {
        List<String> notes = Database.readDatabaseLines(collection);
        if (notes.isEmpty()) {
            System.out.println("Collection \"" + collection + "\" is empty. Nothing to edit.");
            return;
        }
        System.out.println();
        System.out.println("Enter the number of the note to edit (0 to cancel):");
        Database.displayDatabase(collection);
        System.out.print("$> ");
        String editInput = scanner.nextLine().trim();
        try {
            int editNum = Integer.parseInt(editInput);
            if (editNum == 0) {
                System.out.println("Edit canceled.");
                return;
            }
            String originalNote = Database.getEntry(collection, editNum);
            if (originalNote != null) {
                System.out.println();
                System.out.println("Original: " + originalNote);
                System.out.println("Enter new text:");
                System.out.print("$> ");
                String newText = scanner.nextLine().trim();
                Database.editEntry(collection, editNum, newText);
                System.out.println();
                System.out.println("Note updated successfully!");
            } else {
                System.out.println("Invalid note number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid note number.");
        }
        System.out.println();
        System.out.println("---");
    }

    //Reorder notes - swap or move.
    public static void reorderNotes(String collection, Scanner scanner) {
        List<String> notes = Database.readDatabaseLines(collection);
        if (notes.size() < 2) {
            System.out.println("Need at least 2 notes to reorder.");
            return;
        }
        System.out.println();
        System.out.println("Reorder options:");
        System.out.println("1. Swap two notes");
        System.out.println("2. Move a note to a new position");
        System.out.print("$> ");
        String choice = scanner.nextLine().trim();

        if (choice.equals("1") || choice.equals("swap")) {
            swapNotes(collection, notes.size(), scanner);
        } else if (choice.equals("2") || choice.equals("move")) {
            moveNote(collection, notes.size(), scanner);
        } else {
            System.out.println("Invalid choice.");
        }
        System.out.println();
        System.out.println("---");
    }
    
    //Swap two notes.
    private static void swapNotes(String collection, int size, Scanner scanner) {
        System.out.println();
        Database.displayDatabase(collection);
        System.out.print("Enter first note number: ");
        String first = scanner.nextLine().trim();
        System.out.print("Enter second note number: ");
        String second = scanner.nextLine().trim();
        try {
            int pos1 = Integer.parseInt(first);
            int pos2 = Integer.parseInt(second);
            if (pos1 > 0 && pos1 <= size && pos2 > 0 && pos2 <= size) {
                Database.swapEntries(collection, pos1, pos2);
                System.out.println("Notes swapped!");
            } else {
                System.out.println("Invalid note numbers.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    //Move a note to a new position.
    private static void moveNote(String collection, int size, Scanner scanner) {
        System.out.println();
        Database.displayDatabase(collection);
        System.out.print("Enter note number to move: ");
        String from = scanner.nextLine().trim();
        System.out.print("Enter new position: ");
        String to = scanner.nextLine().trim();
        try {
            int fromPos = Integer.parseInt(from);
            int toPos = Integer.parseInt(to);
            if (fromPos > 0 && fromPos <= size && toPos > 0 && toPos <= size) {
                Database.moveEntry(collection, fromPos, toPos);
                System.out.println("Note moved!");
            } else {
                System.out.println("Invalid positions.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    //Delete a note from the collection.
    public static void deleteNote(String collection, Scanner scanner) {
        List<String> notes = Database.readDatabaseLines(collection);
        if (notes.isEmpty()) {
            System.out.println("Collection \"" + collection + "\" is empty. Nothing to delete.");
            return;
        }
        System.out.println();
        System.out.println("Enter the number of the note to remove (0 to cancel):");
        Database.displayDatabase(collection);
        System.out.print("$> ");

        String inputNum = scanner.nextLine().trim();
        System.out.println();

        try {
            int noteNum = Integer.parseInt(inputNum);

            if (noteNum == 0) {
                System.out.println("Deletion canceled.");
                return;
            }

            String deletedNote = Database.getEntry(collection, noteNum);

            if (deletedNote != null) {
                Database.deleteEntry(collection, noteNum);
                System.out.println("\"" + deletedNote + "\" deleted from " + collection);
            } else {
                System.out.println("Invalid note number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid note number.");
        }
        System.out.println();
        System.out.println("---");
    }

    //Change to a different collection.
    public static String changeCollection(String currentCollection, Scanner scanner) {
        System.out.println();
        System.out.println("Enter collection name (existing or new):");
        System.out.print("$> ");
        String newCollection = scanner.nextLine().trim();
        if (newCollection.isEmpty()) {
            System.out.println("Collection name cannot be empty.");
            return currentCollection;
        }
        if (!Database.databaseExists(newCollection)) {
            if (!CLI.checkOrCreateCollection(newCollection, scanner)) {
                return currentCollection; // User declined to create
            }
        }
        System.out.println();
        System.out.println("Now using collection: " + newCollection);
        System.out.println();
        System.out.println("---");
        return newCollection;
    }
}
