# Green Day Bank (WIP) 🏦

A command-line banking application where your investments *only grow and never shrink*. This project simulates a "magical" bank with savings, investments, and risk-based funds.

> [!IMPORTANT]
> **Status**: 🚧 Work In Progress 🚧
> This project is currently under active development.

## The Situation 🌟

How nice it would be if your savings and investments in your bank would only grow and never shrink. In this task, we build such a magical bank.

## Features ✨

- **User Interaction**:
  - **Single Scanner Instance**: centralized input handling.
  - **Robust Error Handling**: Gracefully handles invalid inputs and EOF.
- **Login System**: Supports 4 users (Alice, Bob, Charlie, Diana).
- **Account Management**:
  - **Savings Account**: Earns **1% interest** on every balance check.
  - **Investment Account**: Used to buy into funds.
  - **Currency**: All balances use `BigDecimal` for precision.
- **Investment Funds 📈**:
  - **LOW_RISK**: +2% growth
  - **MEDIUM_RISK**: +5% growth
  - **HIGH_RISK**: +10% growth
- **Session Persistence**: User state remains active until explicit logout.

## Usage 🚀

The application provides a menu-driven interface:

```text
 --- Banking App Menu ---
1. Show balance
2. Deposit money
3. Withdraw money
4. Send money to a person
5. Invest in funds
6. Transfer between accounts
7. Withdraw all investments
8. Logout
9. Exit
```

## Project Structure 📂

```text
bankingApp/
├── BankingApp.java           # Entrypoint containing main & Banking Service instantiation
├── model/
│   ├── Account.java          # Abstract base class
│   ├── SavingsAccount.java   # Extends Account
│   ├── InvestmentAccount.java# Extends Account
│   ├── User.java             # User entity
│   └── Fund.java             # Enum for LOW/MEDIUM/HIGH risk
├── service/
│   └── BankingService.java   # Core logic options
└── exception/
    └── InvalidAmountException.java
```

## Requirements 📋

### Functional ⚙️

1. **User Interaction**: standard `Scanner` usage, handle Ctrl+D.
2. **Accounts**: Each user starts with $1000 cash (not in bank).
3. **Interest Logic**:
   - Savings: +1% on every "Show balance".
   - Funds: Gain added on every "Show balance".
4. **Operations**:
   - Cash -> Savings (Deposit)
   - Savings -> Cash (Withdraw)
   - Savings -> User (Send)
   - Savings <-> Investment (Transfer)
   - Investment -> Funds (Invest)
   - Funds -> Investment (Withdraw all)

---
*Developed as a solo project for the **[kood/Nooremarendaja](https://kood.tech/kood-nooremarendaja/)** curriculum (Free, **[NextGenEU](https://kood.tech/meist/toetused/)** funded, 5-month intensive).*
