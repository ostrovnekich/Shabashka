package com.example.shabashka;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private JobAdapter jobAdapter;
    private List<Job> jobList;
    private JobLoader jobLoader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        jobList = new ArrayList<>();
        jobAdapter = new JobAdapter(requireActivity(), jobList);
        recyclerView.setAdapter(jobAdapter);

        jobLoader = new JobLoader(requireContext());
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
