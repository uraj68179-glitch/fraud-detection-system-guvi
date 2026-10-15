package com.fraud;

import java.time.LocalDateTime;

public class Transaction {
    private String transactionId;
    private String accountId;
    private double amount;
    private String location;
    private LocalDateTime timestamp;

    public Transaction(String transactionId, String accountId, double amount, String location, LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.amount = amount;
        this.location = location;
        this.timestamp = timestamp;
    }

    public String getTransactionId() { return transactionId; }
    public String getAccountId() { return accountId; }
    public double getAmount() { return amount; }
    public String getLocation() { return location; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
