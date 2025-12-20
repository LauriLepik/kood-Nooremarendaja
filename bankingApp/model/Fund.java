package model;

public enum Fund {
    LOW_RISK(0.02),
    MEDIUM_RISK(0.05),
    HIGH_RISK(0.10);
    
    private final double rate;
    
    Fund(double rate) {
        this.rate = rate;
    }
    
    public double getRate() {
        return rate;
    }
}

