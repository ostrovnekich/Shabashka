package com.example.shabashka;

public class Job {
    private String title;
    private String description;
    private String location;
    private String salary;
    private boolean hourly;
    private String userId;
    private String phone;
    private String email;
    private String jobId;
    private boolean isFavorite;

    public Job() {}

    public Job(String title, String description, String location, String salary,
               boolean hourly, String userId, String phone, String email, String jobId) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.salary = salary;
        this.hourly = hourly;
        this.userId = userId;
        this.phone = phone;
        this.email = email;
        this.jobId = jobId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getSalary() { return salary; }
    public boolean isHourly() { return hourly; }
    public String getUserId() { return userId; }
    public String getPhone() { return phone; }
    public String getJobId() { return jobId; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }
    public void setSalary(String salary) { this.salary = salary; }
    public void setHourly(boolean hourly) { this.hourly = hourly; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
}
