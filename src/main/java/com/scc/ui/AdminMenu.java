package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final UserDao userDao;
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {
        while (true) {
            System.out.println("\n=================================");
            System.out.println("         ADMIN DASHBOARD         ");
            System.out.println("=================================");
            System.out.println("[1] View All System Users");
            System.out.println("[2] View Pending Registrations");
            System.out.println("[3] Approve User Account");
            System.out.println("[4] Logout");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> displayAllUsers();
                case "2" -> displayPendingUsers();
                case "3" -> handleUserApproval();
                case "4" -> {
                    System.out.println("Logging out from Admin dashboard...");
                    return;
                }
                default -> System.out.println("⚠️ Invalid choice. Please try again.");
            }
        }
    }

    private void displayAllUsers() {
        List<User> users = userDao.getAllUsers();
        printUserTable(users, "ALL SYSTEM USERS");
    }

    private void displayPendingUsers() {
        List<User> pendingUsers = userDao.getPendingUsers();
        printUserTable(pendingUsers, "PENDING REGISTRATIONS");
    }

    private void handleUserApproval() {
        List<User> pendingUsers = userDao.getPendingUsers();
        if (pendingUsers.isEmpty()) {
            System.out.println("\nℹ️ No pending registrations to approve.");
            return;
        }

        printUserTable(pendingUsers, "PENDING USERS AWAITING APPROVAL");

        System.out.print("\nEnter Target User ID to approve (Enter 0 to cancel): ");
        String input = scanner.nextLine().trim();

        int targetId;
        try {
            targetId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input. Please enter a valid numeric ID.");
            return;
        }

        if (targetId == 0) {
            System.out.println("🚫 Approval action cancelled.");
            return;
        }

        boolean existsInPending = pendingUsers.stream().anyMatch(u -> u.userId() == targetId);
        if (!existsInPending) {
            System.out.println("❌ Error: User ID " + targetId + " was not found in the pending list.");
            return;
        }

        boolean success = userDao.approveUser(targetId);
        if (success) {
            System.out.println("✅ Success! User ID " + targetId + " has been APPROVED.");
        } else {
            System.out.println("❌ Failed to approve user account.");
        }
    }

    private void printUserTable(List<User> users, String title) {
        System.out.println("\n--- " + title + " ---");
        if (users.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        String format = "%-5s | %-15s | %-12s | %-10s%n";
        String border = "--------------------------------------------------";

        System.out.println(border);
        System.out.printf(format, "ID", "USERNAME", "ROLE", "STATUS");
        System.out.println(border);

        for (User user : users) {
            System.out.printf(format,
                    user.userId(),
                    user.username(),
                    user.role(),
                    user.status()
            );
        }
        System.out.println(border);
    }
}