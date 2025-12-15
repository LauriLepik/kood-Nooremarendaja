//Entry point for the Notes CLI application.
public class Main {
    public static void main(String[] args) {
        if (args.length != 1 || isHelp(args[0])) {
            printUsage();
            return;
        }

        String collection = args[0];
        CLI.run(collection);
    }

    private static boolean isHelp(String arg) {
        return arg.equals("-h") || arg.equals("--help") || arg.isEmpty();
    }

    private static void printUsage() {
        System.out.println("Assuming you have compiled the project per README, then usage: java -cp out Main [COLLECTION]");
        System.out.println();
        System.out.println("This tool allows users to manage short single-line notes within a collection.");
        System.out.println();
        System.out.println("Options:");
        System.out.println("-h, --help       Show this help message and exit");
        System.out.println("[COLLECTION]     The name of the collection to manage");
    }
}
