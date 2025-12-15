import java.util.Scanner;

//Handles the CLI menu loop and user interaction.
public class CLI {

    //Prints the welcome banner.
    public static void printBanner() {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║     _   _       _                     ║");
        System.out.println("║    | \\ | | ___ | |_ ___  ___          ║");
        System.out.println("║    |  \\| |/ _ \\| __/ _ \\/ __|         ║");
        System.out.println("║    | |\\  | (_) | ||  __/\\__ \\         ║");
        System.out.println("║    |_| \\_|\\___/ \\__\\___||___/         ║");
        System.out.println("║                                       ║");
        System.out.println("║      Welcome to the notes tool!       ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.println();
    }

    //Prints the menu options.
    public static void printMenu(String collection) {
        System.out.println();
        System.out.println("=== Collection: " + collection + " ===");
        System.out.println();
        System.out.println("Select operation:");
        System.out.println();
        System.out.println("1. Show notes");
        System.out.println("2. Add a note");
        System.out.println("3. Edit a note");
        System.out.println("4. Reorder notes");
        System.out.println("5. Delete a note");
        System.out.println("6. Change collection");
        System.out.println("7. Exit");
        System.out.print("$> ");
    }

    //Checks if the collection exists. If not, prompts the user to create it.
    public static boolean checkOrCreateCollection(String collection, Scanner scanner) {
        if (Database.databaseExists(collection)) {
            return true;
        }

        System.out.println("Collection \"" + collection + "\" does not exist.");
        System.out.print("Would you like to create it? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("y") || response.equals("yes")) {
            Database.createDatabase(collection);
            System.out.println("Collection \"" + collection + "\" created successfully!");
            return true;
        } else {
            System.out.println("Exiting without creating collection.");
            return false;
        }
    }

    //Handles the case when collection file is deleted mid-session.
    public static String handleMissingCollection(String collection, Scanner scanner) {
        System.out.println("Error: Collection \"" + collection + "\" no longer exists.");
        System.out.println();
        System.out.println("What would you like to do?");
        System.out.println("1. Recreate \"" + collection + "\" as empty");
        System.out.println("2. Switch to a different collection or create a new one");
        System.out.println("3. Exit");
        System.out.print("$> ");

        String choice = scanner.nextLine().trim().toLowerCase();
        switch (choice) {
            case "recreate":
            case "1":
                Database.createDatabase(collection);
                System.out.println("Collection \"" + collection + "\" recreated!");
                return collection;
            case "switch":
            case "switch to a different collection or create a new one":
            case "2":
                System.out.print("Enter collection name: ");
                String newColl = scanner.nextLine().trim();
                if (!Database.databaseExists(newColl)) {
                    if (!checkOrCreateCollection(newColl, scanner)) {
                        return null;
                    }
                }
                System.out.println("Switched to collection: " + newColl);
                return newColl;
            default:
                return null;
        }
    }

    //Main CLI loop - handles user input and dispatches to operations.
    public static void run(String collection) {
        Scanner scanner = new Scanner(System.in);

        printBanner();

        // Check if collection exists, prompt to create if not
        if (!checkOrCreateCollection(collection, scanner)) {
            scanner.close();
            return;
        }

        while (true) {
            printMenu(collection);

            String input = scanner.nextLine().trim().toLowerCase();

            // Check for missing collection before operations that need it
            if (needsExistingCollection(input) && !Database.databaseExists(collection)) {
                String result = handleMissingCollection(collection, scanner);
                if (result == null) {
                    scanner.close();
                    return;
                }
                collection = result;
                continue;
            }

            switch (input) {
                case "show notes":
                case "1":
                    Operations.showNotes(collection);
                    break;

                case "add note":
                case "2":
                    Operations.addNote(collection, scanner);
                    break;

                case "edit note":
                case "3":
                    Operations.editNote(collection, scanner);
                    break;

                case "reorder notes":
                case "4":
                    Operations.reorderNotes(collection, scanner);
                    break;

                case "delete note":
                case "5":
                    Operations.deleteNote(collection, scanner);
                    break;

                case "change collection":
                case "6":
                    collection = Operations.changeCollection(collection, scanner);
                    break;

                case "exit":
                case "e":
                case "7":
                    System.out.println("Bye!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid option. Please enter 1-7 or a command (e.g. 'show notes', 'exit').");
            }
        }
    }
    
    //Checks if the given input requires an existing collection.
    private static boolean needsExistingCollection(String input) {
        return input.equals("1") || input.equals("show notes") ||
               input.equals("3") || input.equals("edit note") ||
               input.equals("4") || input.equals("reorder notes") ||
               input.equals("5") || input.equals("delete note");
    }
}
