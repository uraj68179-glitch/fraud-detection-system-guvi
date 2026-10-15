package com.fraud;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.*;

public class FraudDetectionEngine {
    private final Map<String, Transaction> history = new HashMap<>();
    private static final String LOG_FILE = "transactions.log";

    // Dynamic Admin Configurations
    private double highAmountThreshold = 50000.0;
    private int velocitySecondsLimit = 5;
    private String algorithmMode = "BALANCED"; // BALANCED, STRICT, RELAXED

    // Analytics counters for Admin Report
    private int totalTransactions = 0;
    private int approvedCount = 0;
    private int reviewCount = 0;
    private int blockedCount = 0;

    public void process(Transaction current) {
        totalTransactions++;
        System.out.println("Processing Txn [" + current.getTransactionId() + "] | Account: " + current.getAccountId() + " | Amount: Rs." + current.getAmount());
        Transaction previous = history.get(current.getAccountId());

        int riskScore = 0;
        List<String> triggers = new ArrayList<>();

        // Algorithm Sensitivity Multiplier
        double sensitivityMultiplier = algorithmMode.equals("STRICT") ? 1.3 : (algorithmMode.equals("RELAXED") ? 0.8 : 1.0);

        // 1. High Amount Check
        if (current.getAmount() > (highAmountThreshold * 2)) {
            riskScore += (int)(60 * sensitivityMultiplier);
            triggers.add("Critical amount > Rs." + (highAmountThreshold * 2));
        } else if (current.getAmount() > highAmountThreshold) {
            riskScore += (int)(40 * sensitivityMultiplier);
            triggers.add("High amount > Rs." + highAmountThreshold);
        }

        // 2. Velocity Check
        if (previous != null) {
            long seconds = Math.abs(Duration.between(previous.getTimestamp(), current.getTimestamp()).getSeconds());
            if (seconds < velocitySecondsLimit) {
                riskScore += (int)(50 * sensitivityMultiplier);
                triggers.add("Burst velocity (< " + velocitySecondsLimit + "s)");
            } else if (seconds < 30) {
                riskScore += (int)(20 * sensitivityMultiplier);
                triggers.add("Rapid activity (< 30s)");
            }
        }

        // 3. Location Mismatch Check
        if (previous != null && !current.getLocation().equalsIgnoreCase(previous.getLocation())) {
            long minutes = Math.abs(Duration.between(previous.getTimestamp(), current.getTimestamp()).toMinutes());
            if (minutes < 30) {
                riskScore += (int)(45 * sensitivityMultiplier);
                triggers.add("Location mismatch within " + minutes + "m");
            }
        }

        // Decision based on Final Score
        String status;
        if (riskScore >= 70) {
            status = "BLOCKED";
            blockedCount++;
        } else if (riskScore >= 35) {
            status = "MANUAL_REVIEW";
            reviewCount++;
        } else {
            status = "APPROVED";
            approvedCount++;
        }

        String reason = triggers.isEmpty() ? "Normal behavior patterns" : String.join(", ", triggers);

        System.out.println("  --> [RISK SCORE]: " + riskScore + "/100 (Mode: " + algorithmMode + ")");
        System.out.println("  --> [STATUS]    : " + status);
        System.out.println("  --> [USER ALERT]: " + reason);

        saveToLog(current, riskScore, status, reason);
        history.put(current.getAccountId(), current);
        System.out.println("-------------------------------------------------------------------");
    }

    private void saveToLog(Transaction txn, int score, String status, String reason) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(txn.getTimestamp() + " | ID: " + txn.getTransactionId() + 
                       " | Acc: " + txn.getAccountId() + 
                       " | Rs." + txn.getAmount() + 
                       " | City: " + txn.getLocation() + 
                       " | Score: " + score + 
                       " | Status: " + status + 
                       " | Triggers: " + reason);
        } catch (IOException e) {
            System.out.println("Error saving audit log: " + e.getMessage());
        }
    }

    // Admin Functionalities
    public void printAnalyticsReport() {
        System.out.println("\n========== ADMIN DETECTION REPORT ==========");
        System.out.println("Total Processed  : " + totalTransactions);
        System.out.println("Approved (Safe)  : " + approvedCount);
        System.out.println("Manual Review    : " + reviewCount);
        System.out.println("Blocked (Fraud)  : " + blockedCount);
        System.out.println("Current Engine   : " + algorithmMode);
        System.out.println("Amount Threshold : Rs." + highAmountThreshold);
        System.out.println("Velocity Window  : " + velocitySecondsLimit + " seconds");
        System.out.println("============================================");
    }

    public void updateThresholds(double newAmount, int newVelocity) {
        this.highAmountThreshold = newAmount;
        this.velocitySecondsLimit = newVelocity;
        System.out.println("[Admin] Configurations updated successfully!");
    }

    public void setAlgorithmMode(String mode) {
        this.algorithmMode = mode.toUpperCase();
        System.out.println("[Admin] Algorithm mode updated to: " + this.algorithmMode);
    }
}
