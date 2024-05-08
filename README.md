Got it, here's the SQL script with comments indicating where you should put CREATE TABLE statements and INSERT statements:

```sql
-- Create Transaction table
CREATE TABLE Transaction (
    ID VARCHAR(255) PRIMARY KEY,
    AccountID VARCHAR(255),
    Type VARCHAR(255),
    State VARCHAR(255),
    Amount DECIMAL(18,2),
    CurrencyCode VARCHAR(3),
    PayerAccountNumber VARCHAR(255),
    PayerIFSC VARCHAR(255),
    PayeeAccountNumber VARCHAR(255),
    PayeeIFSC VARCHAR(255),
    PayeeMCC VARCHAR(255),
    PayeeType VARCHAR(255),
    ReferenceNumber VARCHAR(255),
    PurposeCode VARCHAR(255),
    InitiationMode VARCHAR(255),
    OrigTransactionID VARCHAR(255),
    ReconciliationState VARCHAR(255),
    ReconciliationTime TIMESTAMP,
    CreationTime TIMESTAMP,
    LastModifiedTime TIMESTAMP,
    TransactionTime TIMESTAMP,
    AdditionalData VARCHAR(255)
);

-- Create Account table
CREATE TABLE Account (
    ID VARCHAR(255) PRIMARY KEY,
    AccountNumber VARCHAR(255),
    IFSC VARCHAR(255),
    AccountType VARCHAR(255),
    State VARCHAR(255),
    Balance DECIMAL(18,2),
    CurrencyCode VARCHAR(3),
    LastReconciledBalance VARCHAR(255),
    LastReconciliationState VARCHAR(255),
    LastReconciliationTime TIMESTAMP,
    CreationTime TIMESTAMP,
    LastModifiedTime TIMESTAMP,
    AdditionalData VARCHAR(255)
);

-- Example data for Transaction table
INSERT INTO Transaction (ID, AccountID, Type, State, Amount, CurrencyCode, PayerAccountNumber, PayerIFSC, PayeeAccountNumber, PayeeIFSC, PayeeMCC, PayeeType, ReferenceNumber, PurposeCode, InitiationMode, OrigTransactionID, ReconciliationState, ReconciliationTime, CreationTime, LastModifiedTime, TransactionTime, AdditionalData)
VALUES 
('1', '1001', 'Credit', 'Completed', 100.00, 'USD', '123456789', 'ABCD1234', '987654321', 'WXYZ5678', '1234', 'Individual', 'REF123', 'General', 'Online', NULL, 'Reconciled', '2024-05-08 12:00:00', '2024-05-08 10:00:00', '2024-05-08 10:00:00', '2024-05-08 10:00:00', 'Additional info');

-- Example data for Account table
INSERT INTO Account (ID, AccountNumber, IFSC, AccountType, State, Balance, CurrencyCode, LastReconciledBalance, LastReconciliationState, LastReconciliationTime, CreationTime, LastModifiedTime, AdditionalData)
VALUES 
('1001', '123456789', 'ABCD1234', 'Savings', 'Active', 5000.00, 'USD', '5000.00', 'Reconciled', '2024-05-08 12:00:00', '2024-05-08 10:00:00', '2024-05-08 10:00:00', 'Additional info');
```

You should execute the CREATE TABLE statements first to create the tables, and then execute the INSERT statements to insert example data into the tables.
