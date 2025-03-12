package com.example.shabashka;

import android.content.Context;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class JobLoader {
    private final FirebaseFirestore db;
    private final Context context;

    public JobLoader(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    public void loadJobs(JobLoadCallback callback) {
        db.collection(context.getString(R.string.jobs_collection_path)).get().addOnCompleteListener(task -> {
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
