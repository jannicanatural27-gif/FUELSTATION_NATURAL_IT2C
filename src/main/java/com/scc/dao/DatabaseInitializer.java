package com.scc.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                role TEXT NOT NULL,
                status TEXT NOT NULL
            );
            """;

        String createFuelInventoryTable = """
            CREATE TABLE IF NOT EXISTS fuel_inventory (
                fuel_id INTEGER PRIMARY KEY AUTOINCREMENT,
                fuel_type TEXT NOT NULL UNIQUE,
                price_per_liter REAL NOT NULL,
                stock_level_liters REAL NOT NULL
            );
            """;

        String createTransactionsTable = """
            CREATE TABLE IF NOT EXISTS transactions (
                transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                fuel_id INTEGER NOT NULL,
                liters_pumped REAL NOT NULL,
                total_amount REAL NOT NULL,
                date TEXT NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY (fuel_id) REFERENCES fuel_inventory(fuel_id) ON DELETE CASCADE
            );
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsersTable);
            stmt.execute(createFuelInventoryTable);
            stmt.execute(createTransactionsTable);

            System.out.println("✅ [DatabaseInitializer] Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("❌ [DatabaseInitializer] Initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}