package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final UserDao userDao;
    private final Scanner scanner;
    private final User currentAdmin;

    public AdminMenu(UserDao userDao, Scanner scanner, User currentAdmin) {
        this.userDao = userDao;
        this.scanner = scanner;
        this.currentAdmin = currentAdmin;
    }

    public void start() {
        while (true) {
            System.out.println("\n=================================");
            System.out.println("         ADMIN DASHBOARD         ");
            System.out.println("=================================");
            System.out.println("[1] View All System Users");
            System.out.println("[2] View Pending Registrations");
            System.out.println("[3] Approve User Account");
            System.out.println("[4] Update User Role and Status");
            System.out.println("[5] Logout");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> displayAllUsers();
                case "2" -> displayPendingUsers();
                case "3" -> handleUserApproval();
                case "4" -> handleUserUpdate();
                case "5" -> {
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

        int targetId = readIntInput("Enter Target User ID to approve (Enter 0 to cancel): ");
        if (targetId == 0) {
            System.out.println("🚫 Approval action cancelled.");
            return;
        }

        boolean existsInPending = pendingUsers.stream().anyMatch(u -> u.id() == targetId);
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

    private void handleUserUpdate() {
        List<User> users = userDao.getAllUsers();
        printUserTable(users, "ALL SYSTEM USERS FOR UPDATE");

        int targetId = readIntInput("Enter Target User ID to update (Enter 0 to cancel): ");
        if (targetId == 0) {
            System.out.println("🚫 Update action cancelled.");
            return;
        }

        // Self-Edit Restriction Rule
        if (targetId == currentAdmin.id()) {
            System.out.println("⚠️ WARNING: Modifying the currently active admin account is restricted.");
            return;
        }

        boolean userExists = users.stream().anyMatch(u -> u.id() == targetId);
        if (!userExists) {
            System.out.println("❌ Error: User ID " + targetId + " does not exist.");
            return;
        }

        String newRole = readValidRole();
        if (newRole.equalsIgnoreCase("CANCEL")) {
            System.out.println("🚫 Update action cancelled.");
            return;
        }

        String newStatus = readValidStatus();
        if (newStatus.equalsIgnoreCase("CANCEL")) {
            System.out.println("🚫 Update action cancelled.");
            return;
        }

        boolean success = userDao.updateUserRoleAndStatus(targetId, newRole, newStatus);
        if (success) {
            System.out.println("✅ Success! User ID " + targetId + " updated to Role: " + newRole + ", Status: " + newStatus);
        } else {
            System.out.println("❌ Failed to update user profile.");
        }
    }

    // Input Validation Wrappers
    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid numeric input. Please enter numbers only.");
            }
        }
    }

    private String readValidRole() {
        while (true) {
            System.out.print("Enter new Role (ADMIN, RESIDENT, ATTENDANT, or 0 to cancel): ");
            String role = scanner.nextLine().trim().toUpperCase();
            if (role.equals("0")) {
                return "CANCEL";
            }
            if (role.equals("ADMIN") || role.equals("RESIDENT") || role.equals("ATTENDANT") || role.equals("STUDENT") || role.equals("TEACHER")) {
                return role;
            }
            System.out.println("❌ Invalid role. Please choose a valid role (e.g., ADMIN, RESIDENT, ATTENDANT).");
        }
    }

    private String readValidStatus() {
        while (true) {
            System.out.print("Enter new Status (APPROVED, PENDING, or 0 to cancel): ");
            String status = scanner.nextLine().trim().toUpperCase();
            if (status.equals("0")) {
                return "CANCEL";
            }
            if (status.equals("APPROVED") || status.equals("PENDING")) {
                return status;
            }
            System.out.println("❌ Invalid status. Please enter APPROVED or PENDING.");
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
                    user.id(),
                    user.username(),
                    user.role(),
                    user.status()
            );
        }
        System.out.println(border);
    }
}