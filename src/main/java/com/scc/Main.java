package com.scc;

import com.scc.dao.DatabaseConnection;
import com.scc.ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection.initializeDatabase();

        ConsoleUI consoleUI = new ConsoleUI();
        consoleUI.start();
    }
}