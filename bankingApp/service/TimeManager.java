package service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import model.User;

public class TimeManager {
    private LocalDateTime currentSimulationTime;
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final UserManager userManager;

    public TimeManager(UserManager userManager) {
        this.userManager = userManager;
        this.currentSimulationTime = LocalDateTime.now();
    }

    public void advanceTime(int days) {
        currentSimulationTime = currentSimulationTime.plusDays(days);
        for (int i = 0; i < days; i++) {
            performDailyProcessing();
        }
    }
    
    public void performDailyProcessing() {
        for (User user : userManager.getAllUsers().values()) {
            applyDailyGains(user);
        }
    }

    public void applyDailyGains(User user) {
        user.getSavingsAccount().applyGains();
        user.getInvestmentAccount().applyGains();
    }

    public long calculateDaysPassed(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(from, to);
    }

    public String getCurrentFormattedTime() {
        return currentSimulationTime.format(DISPLAY_FORMAT);
    }
    
    public LocalDateTime getCurrentTime() {
        return currentSimulationTime;
    }
}
