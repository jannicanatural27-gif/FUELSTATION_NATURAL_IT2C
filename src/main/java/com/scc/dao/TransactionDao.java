package com.scc.dao;

import com.scc.model.Transaction;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao {
    public boolean createTransaction(int userId, int fuelId, double liters, double totalAmount) {
        String sqlTx = "INSERT INTO transactions(user_id, fuel_id, liters_pumped, total_amount, date) VALUES(?, ?, ?, ?, ?)";
        String sqlStock = "UPDATE fuel_inventory SET stock_level_liters = stock_level_liters - ? WHERE fuel_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtTx = conn.prepareStatement(sqlTx);
                 PreparedStatement pstmtStock = conn.prepareStatement(sqlStock)) {

                pstmtTx.setInt(1, userId);
                pstmtTx.setInt(2, fuelId);
                pstmtTx.setDouble(3, liters);
                pstmtTx.setDouble(4, totalAmount);
                pstmtTx.setString(5, LocalDate.now().toString());
                pstmtTx.executeUpdate();

                pstmtStock.setDouble(1, liters);
                pstmtStock.setInt(2, fuelId);
                pstmtStock.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Transaction failed: " + e.getMessage());
            return false;
        }
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Transaction(
                        rs.getInt("transaction_id"),
                        rs.getInt("user_id"),
                        rs.getInt("fuel_id"),
                        rs.getDouble("liters_pumped"),
                        rs.getDouble("total_amount"),
                        rs.getString("date")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error reading transactions: " + e.getMessage());
        }
        return list;
    }
}
