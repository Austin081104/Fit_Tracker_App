package com.example.fit_tracker;

public class WaterEntry {
    private String id;
    private String userId;
    private String date;
    private double amount;

    public WaterEntry() {}

    public WaterEntry(String id, String userId, String date, double amount) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.amount = amount;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}
