package model;

import java.math.BigDecimal;

public class SavingsAccount extends Account {
    private static final BigDecimal INTEREST_RATE = new BigDecimal("0.01");

    public SavingsAccount() {
        super();
    }

    @Override
    public void applyGains() {
        BigDecimal interest = this.balance.multiply(INTEREST_RATE);
        this.balance = this.balance.add(interest).setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
