package com.scc;

import com.scc.dao.DatabaseInitializer;
import com.scc.ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {

        DatabaseInitializer.initializeDatabase();

        ConsoleUI consoleUI = new ConsoleUI();
        consoleUI.start();
    }
}