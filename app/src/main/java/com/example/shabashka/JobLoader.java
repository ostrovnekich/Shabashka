package com.example.shabashka;

import android.app.Activity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class JobLoader {
    private final FirebaseFirestore db;
    Activity activity;

    public JobLoader(Activity activity) {
        this.activity = activity;
        db = FirebaseFirestore.getInstance();
    }

    public void loadJobs(JobLoadCallback callback) {
        db.collection(activity.getString(R.string.jobs_collection_path)).get().addOnCompleteListener(task -> {
                List<Job> jobList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Job job = document.toObject(Job.class);
                    jobList.add(job);
                }
                callback.onJobsLoaded(jobList);
        });
    }

    public interface JobLoadCallback {
        void onJobsLoaded(List<Job> jobs);
    }
}
