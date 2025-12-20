package util;

public class ConsoleUtils {
    // ANSI Color Constants
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String BLUE = "\u001B[34m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";

    public static void printWelcomeBanner() {
        System.out.println(GREEN + """
          _______________________________________________________
         |                                                       |
         |    _____                         _____                |
         |   / ____|                       |  __ \\               |
         |  | |  __ _ __ ___  ___ _ __     | |  | | __ _ _   _   |
         |  | | |_ | '__/ _ \\/ _ \\ '_ \\    | |  | |/ _` | | | |  |
         |  | |__| | | |  __/  __/ | | |   | |__| | (_| | |_| |  |
         |   \\_____|_|  \\___|\\___|_| |_|   |_____/ \\__,_|\\__, |  |
         |                                                __/ |  |
         |          BANK     APP                         |___/   |
         |_______________________________________________________|
        """ + RESET);
    }
    
    public static void printAccountFrozenBanner() {
        System.out.println(RED + """
         #########################################################
         #                                                       #
         #    ACCOUNT SUSPENDED - SECURITY VIOLATION DETECTED    #
         #                                                       #
         #            ________  ________  ________               #
         #           |        ||        ||        |              #
         #           | LOCKED || LOCKED || LOCKED |              #
         #           |________||________||________|              #
         #                                                       #
         #    Reason: Multiple Transaction Patterns Detected     #
         #    Action: Contact Security Department                #
         #                                                       #
         #########################################################
        """ + RESET);
    }

    public static String formatSuccess(String msg) {
        return GREEN + "[OK] " + msg + RESET;
    }
    
    public static String formatError(String msg) {
        return RED + "[ERROR] " + msg + RESET;
    }
    
    public static String formatMoney(java.math.BigDecimal amount) {
        if (amount.compareTo(java.math.BigDecimal.ZERO) >= 0) {
            return GREEN + "$" + amount + RESET;
        } else {
            return RED + "-$" + amount.abs() + RESET;
        }
    }
}
