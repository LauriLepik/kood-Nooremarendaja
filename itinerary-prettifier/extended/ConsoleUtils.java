package extended;

// ANSI colors for pretty terminal output.
public class ConsoleUtils {
    
    public static final String RESET  = "\u001B[0m";
    public static final String WHITE  = "\u001B[37m";
    public static final String GREEN  = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE   = "\u001B[34m";
    public static final String CYAN   = "\u001B[36m";
    public static final String PURPLE = "\u001B[35m";
    public static final String BOLD   = "\u001B[1m";

    public static String ok(String msg) {
        return GREEN + msg + RESET;
    }

    public static String warning(String msg) {
        return YELLOW + msg + RESET;
    }

    public static String date(String msg) {
        return BLUE + msg + RESET;
    }

    public static String time(String msg) {
        return CYAN + msg + RESET;
    }

    public static String offset(String msg) {
        return PURPLE + msg + RESET;
    }

    public static String duration(String msg) {
        return YELLOW + msg + RESET;
    }
}
