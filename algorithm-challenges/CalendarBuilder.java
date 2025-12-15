package sprint;

import java.time.Month;
import java.time.YearMonth;

public class CalendarBuilder {

    public static String buildCalendar(String monthName, int year) {
        return formatCalendar(monthName, year);
    }

    public static int getDaysInMonth(String monthName, int year) {
        Month month = Month.valueOf(monthName.toUpperCase());
        YearMonth yearMonth = YearMonth.of(year, month);
        return yearMonth.lengthOfMonth();
    }

    private static String formatCalendar(String monthName, int year) {
        StringBuilder sb = new StringBuilder();
        
        // Header
        sb.append(monthName.toUpperCase()).append(" ").append(year).append("\n");
        sb.append("Mon Tue Wed Thu Fri Sat Sun").append("\n");

        Month month = Month.valueOf(monthName.toUpperCase());
        YearMonth ym = YearMonth.of(year, month);
        int daysInMonth = ym.lengthOfMonth();
        
        // Determine the start day offset (Monday is 1, Sunday is 7)
        // We want 0-based index for Mon=0, ... Sun=6
        int startDayOffset = ym.atDay(1).getDayOfWeek().getValue() - 1;

        int currentDayOfWeek = 1; // 1=Mon ... 7=Sun

        // Print leading empty spaces
        for (int i = 0; i < startDayOffset; i++) {
            sb.append("    "); // 4 spaces
            currentDayOfWeek++;
        }

        for (int day = 1; day <= daysInMonth; day++) {
            // Print the day number, width 3
            if (day < 10) {
                sb.append("  ").append(day);
            } else {
                sb.append(" ").append(day);
            }

            // Check if we need a separator
            boolean isEndOfWeek = (currentDayOfWeek == 7);
            boolean isEndOfMonth = (day == daysInMonth);

            if (!isEndOfWeek && !isEndOfMonth) {
                sb.append(" ");
            }

            if (isEndOfWeek) {
                if (!isEndOfMonth) {
                    sb.append("\n"); // Newline at end of week only if not end of month
                }
                currentDayOfWeek = 1;
            } else {
                currentDayOfWeek++;
            }
        }
        
        return sb.toString();
    }
}
