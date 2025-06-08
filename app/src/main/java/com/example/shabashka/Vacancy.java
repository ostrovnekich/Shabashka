package com.example.shabashka;

public class Vacancy {
    private String id;
    private String title;
    private String description;
    private String salary;
    private boolean hourly;
    private String location;
    private String phone;
    private String userId;

    public Vacancy() {
        // Пустой конструктор нужен для Firebase
    }

    // --- ID ---
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // --- Title ---
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // --- Description ---
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // --- Salary ---
    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    // --- Hourly ---
    public boolean isHourly() {
        return hourly;
    }

    public void setHourly(boolean hourly) {
        this.hourly = hourly;
    }

    // --- Location ---
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // --- Phone ---
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // --- User ID ---
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
