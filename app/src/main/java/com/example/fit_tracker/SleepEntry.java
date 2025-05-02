package com.example.fit_tracker;

public class SleepEntry {
    private String id;
    private String userId;
    private String date;
    private double hours;

    public SleepEntry() {}

    public SleepEntry(String id, String userId, String date, double hours) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.hours = hours;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }
}
