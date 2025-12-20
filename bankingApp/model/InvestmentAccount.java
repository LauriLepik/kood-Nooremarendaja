package model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InvestmentAccount extends Account {
    private Map<Fund, BigDecimal> fundInvestments = new HashMap<>();
    private Set<Fund> fundsEverInvested = new HashSet<>();

    public InvestmentAccount() {
        super();
    }

    public BigDecimal getFundBalance(Fund fund) {
        return fundInvestments.getOrDefault(fund, BigDecimal.ZERO);
    }

    public BigDecimal getTotalFundInvestments() {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal amount : fundInvestments.values()) {
            total = total.add(amount);
        }
        return total;
    }

    public Set<Fund> getFundsEverInvested() {
        return new HashSet<>(fundsEverInvested);
    }

    public void investInFund(Fund fund, BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
        BigDecimal current = fundInvestments.getOrDefault(fund, BigDecimal.ZERO);
        fundInvestments.put(fund, current.add(amount));
        fundsEverInvested.add(fund);
    }

    public void withdrawAllFunds() {
        BigDecimal total = getTotalFundInvestments();
        this.balance = this.balance.add(total);
        for (Fund fund : fundInvestments.keySet()) {
            fundInvestments.put(fund, BigDecimal.ZERO);
        }
    }

    @Override
    public void applyGains() {
        for (Fund fund : fundInvestments.keySet()) {
            BigDecimal current = fundInvestments.get(fund);
            BigDecimal gain = current.multiply(new BigDecimal(String.valueOf(fund.getRate())));
            BigDecimal newBalance = current.add(gain).setScale(2, java.math.RoundingMode.HALF_UP);
            fundInvestments.put(fund, newBalance);
        }
    }
}
