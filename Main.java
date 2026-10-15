package com.fraud;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final FraudDetectionEngine engine = new FraudDetectionEngine();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=========================================");
            System.out.println("   AI-POWERED FRAUD DETECTION SYSTEM     ");
            System.out.println("=========================================");
            System.out.println("Select Role:");
            System.out.println("1. User Dashboard (Make Transactions & Alerts)");
            System.out.println("2. Admin Dashboard (Config, Reports & Algo)");
            System.out.println("3. Exit System");
            System.out.print("Enter choice: ");

            if (!scanner.hasNextLine()) break;
            String role = scanner.nextLine().trim();

            if (role.equals("3")) {
                System.out.println("Shutting down system. Goodbye!");
                break;
            } else if (role.equals("1")) {
                userDashboard();
            } else if (role.equals("2")) {
                adminDashboard();
            } else {
                System.out.println("Invalid selection. Choose 1, 2, or 3.");
            }
        }
    }

    // USER DASHBOARD
    private static void userDashboard() {
        while (true) {
            System.out.println("\n--- USER DASHBOARD ---");
            System.out.println("1. Submit New Transaction");
            System.out.println("2. Back to Main Menu");
            System.out.print("Choice: ");

            if (!scanner.hasNextLine()) break;
            String choice = scanner.nextLine().trim();

            if (choice.equals("2")) break;
            if (!choice.equals("1")) {
                System.out.println("Invalid option.");
                continue;
            }

            String txnId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            System.out.print("Enter Account ID: ");
            String accountId = scanner.nextLine().trim();

            System.out.print("Enter Amount (INR): ");
            double amount;
            try {
                amount = Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid amount!");
                continue;
            }

            System.out.print("Enter City/Location: ");
            String location = scanner.nextLine().trim();

            Transaction txn = new Transaction(txnId, accountId, amount, location, LocalDateTime.now());
            System.out.println("\n--- Processing Transaction ---");
            engine.process(txn);
        }
    }

    // ADMIN DASHBOARD
    private static void adminDashboard() {
        while (true) {
            System.out.println("\n--- ADMIN DASHBOARD ---");
            System.out.println("1. System Configuration (Update Thresholds)");
            System.out.println("2. Detection Monitoring (Summary Report)");
            System.out.println("3. Algorithm Management (Sensitivity Tuning)");
            System.out.println("4. View Detailed Audit Logs");
            System.out.println("5. Back to Main Menu");
            System.out.print("Choice: ");

            if (!scanner.hasNextLine()) break;
            String choice = scanner.nextLine().trim();

            if (choice.equals("5")) break;

            switch (choice) {
                case "1":
                    System.out.print("Enter New High Amount Threshold (INR): ");
                    try {
                        double amt = Double.parseDouble(scanner.nextLine().trim());
                        System.out.print("Enter Velocity Time Window (in seconds): ");
                        int sec = Integer.parseInt(scanner.nextLine().trim());
                        engine.updateThresholds(amt, sec);
                    } catch (NumberFormatException e) {
                        System.out.println("[Error] Invalid numbers entered!");
                    }
                    break;
                case "2":
                    engine.printAnalyticsReport();
                    break;
                case "3":
                    System.out.println("Available Modes: BALANCED, STRICT, RELAXED");
                    System.out.print("Enter Mode: ");
                    String mode = scanner.nextLine().trim();
                    engine.setAlgorithmMode(mode);
                    break;
                case "4":
                    viewLogs();
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void viewLogs() {
        File file = new File("transactions.log");
        if (!file.exists()) {
            System.out.println("[No logs found yet.]");
            return;
        }

        System.out.println("\n--- AUDIT LOG RECORDS ---");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading log: " + e.getMessage());
        }
        System.out.println("-------------------------");
    }
}
