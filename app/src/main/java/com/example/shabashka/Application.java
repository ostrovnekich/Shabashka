package com.example.shabashka;

import com.google.firebase.Timestamp;

public class Application {
    private final String jobTitle;
    private final String applicantName;
    private final Timestamp timestamp;
    private final String jobId;
    private final String applicantId;
    private float rating = 0f;
    private String phone;

    public Application(String jobTitle, String applicantName, Timestamp timestamp, String jobId, String applicantId) {
        this.jobTitle = jobTitle;
        this.applicantName = applicantName;
        this.timestamp = timestamp;
        this.jobId = jobId;
        this.applicantId = applicantId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getJobId() {
        return jobId;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
