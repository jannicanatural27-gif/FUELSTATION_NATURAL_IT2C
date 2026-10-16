package com.scc.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:fuelstation_2c.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "role TEXT NOT NULL, " +
                    "status TEXT NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS fuel_inventory (" +
                    "fuel_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "fuel_type TEXT UNIQUE NOT NULL, " +
                    "price_per_liter REAL NOT NULL, " +
                    "stock_level_liters REAL NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS transactions (" +
                    "transaction_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER NOT NULL, " +
                    "fuel_id INTEGER NOT NULL, " +
                    "liters_pumped REAL NOT NULL, " +
                    "total_amount REAL NOT NULL, " +
                    "date TEXT NOT NULL, " +
                    "FOREIGN KEY(user_id) REFERENCES users(id), " +
                    "FOREIGN KEY(fuel_id) REFERENCES fuel_inventory(fuel_id))");

            stmt.execute("INSERT OR IGNORE INTO fuel_inventory (fuel_id, fuel_type, price_per_liter, stock_level_liters) VALUES " +
                    "(1, 'Unleaded', 65.50, 5000.0), " +
                    "(2, 'Diesel', 60.00, 5000.0), " +
                    "(3, 'Premium', 72.00, 3000.0)");

        } catch (SQLException e) {
            System.out.println("Database initialization error: " + e.getMessage());
        }
    }
}