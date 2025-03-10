package com.example.shabashka;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private JobAdapter jobAdapter;
    private List<Job> jobList;
    private JobLoader jobLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        jobList = new ArrayList<>();
        jobAdapter = new JobAdapter(this, jobList);
        recyclerView.setAdapter(jobAdapter);

        jobLoader = new JobLoader(this );

        loadJobs();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadJobs() {
        jobLoader.loadJobs(jobs -> {
            jobList.clear();
            jobList.addAll(jobs);
            jobAdapter.notifyDataSetChanged();
        });
    }
}