package com.scc.model;

public record Transaction(int transactionId, int userId, int fuelId, double litersPumped, double totalAmount, String date) {}