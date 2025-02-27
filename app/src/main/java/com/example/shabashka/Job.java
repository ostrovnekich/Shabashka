package com.example.shabashka;

public class Job {
    private String title;
    private String description;
    private String location;
    private String salary;
    private String imageUrl;

    public Job() {}

    public Job(String title, String description, String location, String salary, String imageUrl) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.salary = salary;
        this.imageUrl = imageUrl;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getSalary() { return salary; }
    public String getImageUrl() { return imageUrl; }
}

