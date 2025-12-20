# Banking Application Test Suite

Comprehensive JUnit 5 test suite with unit tests and integration/E2E tests covering all 20 classes plus complete workflows.

## Test Coverage

### Exception Tests (2)

- `InsufficientFundsExceptionTest` - Exception message and inheritance
- `InvalidAmountExceptionTest` - Exception message and inheritance

### Model Tests (5)

- `FundTest` - Fund types and rates
- `SavingsAccountTest` - Deposits, withdrawals, 1% interest calculation
- `InvestmentAccountTest` - Fund investments, gains calculation, tracking
- `TransactionTest` - Transaction creation, types, formatting
- `UserTest` - User initialization, accounts, transactions, properties

### Service Tests (7)

- `AuthenticationServiceTest` - Registration, login, validation, error handling
- `BankingServiceTest` - Banking operations, account state verification
- `FraudDetectorTest` - Suspicious transaction detection, account freezing logic
- `InputValidatorTest` - Amount validation, error handling
- `TimeManagerTest` - Time advancement, date calculations, gains application
- `TransferServiceTest` - Account transfers, money sending, insufficient funds handling
- `UserManagerTest` - User registration, retrieval, validation

### Util Tests (4)

- `ConsoleUtilsTest` - Formatting functions, banner printing
- `IbanGeneratorTest` - IBAN generation, validation, format requirements
- `SecurityUtilsTest` - Encryption/decryption round-trip verification
- `DataStoreTest` - Normal operations + 14 database corruption scenarios

## Running the Tests

### Quick Start (Recommended)

**Option 1: Convenience Scripts**

Windows:

```bash
.\run_tests.bat
```

Linux/Mac:

```bash
./run_tests.sh
```

**Option 2: Direct Execution**
Run the runner source file directly (requires Java 11+):

```bash
java TestRunner.java
```

### Manual Compilation & Execution

If you are on an older Java version or prefer manual steps:

**1. Compile:**

```bash
javac -cp ".;junit-platform-console-standalone-1.9.3.jar" -d build BankingApp.java service/*.java model/*.java exception/*.java util/*.java test/exception/*.java test/model/*.java test/service/*.java test/util/*.java test/integration/*.java TestRunner.java
```

**2. Run:**

```bash
java -jar junit-platform-console-standalone-1.9.3.jar --class-path build --scan-class-path
```

```bash
java -cp ".;build;junit-platform-console-standalone-1.9.3.jar" TestRunner
```

## Test Isolation

 All tests are fully isolated:

- **Unit tests** test individual classes/methods independently
- **Integration tests** run in **In-Memory Mode** (`new UserManager(true)`)
  - zero file I/O for maximum speed
  - complete isolation between tests
  - no cleanup required
- **DataStore tests** use unique temporary directories
  - `test-db-{timestamp}` folders created for each test
  - Automatically deleted after execution
- **No production database interaction** - tests never touch the production `database/` folder
- **Independent execution** - tests can run in any order

## Integration Test Workflows

The `BankingWorkflowIntegrationTest` class verifies complete user scenarios:

1. **New User Flow**: Registration → Verify initial state → First deposit → Transaction verification
2. **Transfer Flow**: Register 2 users → Bob deposits → Bob transfers to Carol → Verify both balances
3. **Fraud Detection**: Register user → Make 3 large transactions → Verify fraud flags → Freeze account
4. **Investment Flow**: Deposit to investment → Invest in 3 funds → Apply gains → Withdraw all → Verify
5. **Time Simulation**: Register 2 users → Both deposit → Simulate 5 days → Verify compound interest
6. **Persistence**: Create user → Configure accounts → Save → Clear memory → Load → Verify all restored

## Database Corruption Test Scenarios

The `DataStoreTest` class includes comprehensive corruption recovery testing:

1. Malformed CSV (wrong field counts)
2. Invalid numbers (non-numeric balances/amounts)
3. Invalid dates (unparseable timestamps)
4. Corrupted passwords (decryption failures)
5. Missing files (empty datasets)
6. Empty CSV files
7. Extra whitespace in fields
8. Invalid boolean values
9. Negative balances
10. Empty required fields (username, password, name)
11. Partially corrupted files (mix of valid/invalid records)
12. Directory creation verification

Each corruption scenario verifies the app skips bad records and loads valid ones.

## Created For

Comprehensive test suite created for the Green Day Bank App project.
