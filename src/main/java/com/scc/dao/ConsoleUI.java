package com.scc.ui;

import com.scc.dao.FuelDao;
import com.scc.dao.TransactionDao;
import com.scc.dao.UserDao;
import com.scc.model.Fuel;
import com.scc.model.Transaction;
import com.scc.model.User;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final UserDao userDao = new UserDao();
    private final FuelDao fuelDao = new FuelDao();
    private final TransactionDao transactionDao = new TransactionDao();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        while (true) {
            System.out.println("\n=== FUELEXPRESS: GAS STATION MANAGEMENT SYSTEM ===");
            System.out.println("1. Register Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> handleRegister();
                case "2" -> handleLogin();
                case "3" -> {
                    System.out.println("Exiting system. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void handleRegister() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.println("Select Role:");
        System.out.println("1. CUSTOMER");
        System.out.println("2. ATTENDANT");
        System.out.println("3. MANAGER");
        System.out.print("Choose role (1-3): ");
        String roleChoice = scanner.nextLine();

        String role = switch (roleChoice) {
            case "2" -> "ATTENDANT";
            case "3" -> "MANAGER";
            default -> "CUSTOMER";
        };

        if (userDao.registerUser(username, password, role)) {
            System.out.println("Registration successful!");
        } else {
            System.out.println("Registration failed.");
        }
    }

    private void handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = userDao.loginUser(username, password);
        if (user != null) {
            System.out.println("\nLogin successful! Welcome, " + user.username() + " [" + user.role() + "]");
            routeDashboard(user);
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    private void routeDashboard(User user) {
        switch (user.role()) {
            case "CUSTOMER" -> customerMenu(user);
            case "ATTENDANT" -> attendantMenu(user);
            case "MANAGER" -> managerMenu();
            default -> System.out.println("Unknown role.");
        }
    }

    private void customerMenu(User user) {
        while (true) {
            System.out.println("\n--- Customer Dashboard ---");
            System.out.println("1. View Fuel Prices & Stock");
            System.out.println("2. Logout");
            System.out.print("Choose option: ");
            String ch = scanner.nextLine();
            if (ch.equals("1")) {
                showFuelList();
            } else if (ch.equals("2")) break;
        }
    }

    private void attendantMenu(User user) {
        while (true) {
            System.out.println("\n--- Attendant / Cashier Dashboard ---");
            System.out.println("1. View Fuel Inventory");
            System.out.println("2. Process Fuel Transaction (Dispense)");
            System.out.println("3. Logout");
            System.out.print("Choose option: ");
            String ch = scanner.nextLine();
            if (ch.equals("1")) {
                showFuelList();
            } else if (ch.equals("2")) {
                showFuelList();
                System.out.print("Enter Fuel ID to dispense: ");
                int fuelId = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter liters to pump: ");
                double liters = Double.parseDouble(scanner.nextLine());

                List<Fuel> fuels = fuelDao.getAllFuel();
                Fuel selected = fuels.stream().filter(f -> f.fuelId() == fuelId).findFirst().orElse(null);

                if (selected != null) {
                    double total = liters * selected.pricePerLiter();
                    if (transactionDao.createTransaction(user.id(), fuelId, liters, total)) {
                        System.out.println("Transaction recorded successfully! Total Amount Due: P" + total);
                    } else {
                        System.out.println("Transaction failed.");
                    }
                } else {
                    System.out.println("Invalid Fuel ID.");
                }
            } else if (ch.equals("3")) break;
        }
    }

    private void managerMenu() {
        while (true) {
            System.out.println("\n--- Station Manager Dashboard ---");
            System.out.println("1. View Fuel Inventory");
            System.out.println("2. Update Fuel Price & Restock");
            System.out.println("3. View Sales Report & Transactions");
            System.out.println("4. Logout");
            System.out.print("Choose option: ");
            String ch = scanner.nextLine();
            if (ch.equals("1")) {
                showFuelList();
            } else if (ch.equals("2")) {
                showFuelList();
                System.out.print("Enter Fuel ID to update: ");
                int fuelId = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter new price per liter: ");
                double price = Double.parseDouble(scanner.nextLine());
                System.out.print("Enter liters to add to stock (Restock): ");
                double stock = Double.parseDouble(scanner.nextLine());

                if (fuelDao.updateFuelPriceAndStock(fuelId, price, stock)) {
                    System.out.println("Fuel inventory updated successfully!");
                }
            } else if (ch.equals("3")) {
                System.out.println("\n--- Sales Report & Audit Logs ---");
                List<Transaction> txs = transactionDao.getAllTransactions();
                for (Transaction t : txs) {
                    System.out.println("TxID: " + t.transactionId() + " | FuelID: " + t.fuelId() + " | Liters: " + t.litersPumped() + " | Total: P" + t.totalAmount() + " | Date: " + t.date());
                }
            } else if (ch.equals("4")) break;
        }
    }

    private void showFuelList() {
        System.out.println("\n--- Current Fuel Inventory ---");
        List<Fuel> fuels = fuelDao.getAllFuel();
        for (Fuel f : fuels) {
            System.out.println("ID: " + f.fuelId() + " | Type: " + f.fuelType() + " | Price/L: P" + f.pricePerLiter() + " | Stock: " + f.stockLevelLiters() + "L");
        }
    }
}
