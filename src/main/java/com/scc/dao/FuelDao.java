package com.scc.dao;

import com.scc.model.Fuel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuelDao {
    public List<Fuel> getAllFuel() {
        List<Fuel> fuels = new ArrayList<>();
        String sql = "SELECT * FROM fuel_inventory";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                fuels.add(new Fuel(
                        rs.getInt("fuel_id"),
                        rs.getString("fuel_type"),
                        rs.getDouble("price_per_liter"),
                        rs.getDouble("stock_level_liters")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching fuel data: " + e.getMessage());
        }
        return fuels;
    }

    public boolean updateFuelPriceAndStock(int fuelId, double newPrice, double addedStock) {
        String sql = "UPDATE fuel_inventory SET price_per_liter = ?, stock_level_liters = stock_level_liters + ? WHERE fuel_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newPrice);
            pstmt.setDouble(2, addedStock);
            pstmt.setInt(3, fuelId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
            return false;
        }
    }
}