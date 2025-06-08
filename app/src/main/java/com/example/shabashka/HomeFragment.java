package com.example.shabashka;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment {
    private JobAdapter jobAdapter;
    private List<Job> jobList;
    private List<Job> allJobs;
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
        EditText searchInput = view.findViewById(R.id.searchInput);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        jobList = new ArrayList<>();
        allJobs = new ArrayList<>();
        jobAdapter = new JobAdapter(requireActivity(), jobList);
        recyclerView.setAdapter(jobAdapter);

        jobLoader = new JobLoader(requireContext());

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterJobs(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadJobs();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadJobs() {
        jobLoader.loadJobs(jobs -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (!isAdded()) return;

            String usersPath = getString(R.string.users_collection_path);
            String favoritesPath = getString(R.string.favorites_collection_path);

            if (currentUser == null) {
                allJobs.clear();
                allJobs.addAll(jobs);

                jobList.clear();
                jobList.addAll(jobs);
                jobAdapter.notifyDataSetChanged();
                return;
            }

            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection(usersPath)
                    .document(userId)
                    .collection(favoritesPath)
                    .get()
                    .addOnSuccessListener(favSnapshot -> {
                        if (!isAdded()) return;

                        Set<String> favoriteJobIds = new HashSet<>();
                        for (QueryDocumentSnapshot doc : favSnapshot) {
                            favoriteJobIds.add(doc.getId());
                        }

                        for (Job job : jobs) {
                            job.setFavorite(favoriteJobIds.contains(job.getJobId()));
                        }

                        allJobs.clear();
                        allJobs.addAll(jobs);

                        jobList.clear();
                        jobList.addAll(jobs);
                        jobAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        if (!isAdded()) return;

                        allJobs.clear();
                        allJobs.addAll(jobs);

                        jobList.clear();
                        jobList.addAll(jobs);
                        jobAdapter.notifyDataSetChanged();
                    });
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterJobs(String query) {
        query = query.toLowerCase().trim();
        jobList.clear();
        for (Job job : allJobs) {
            if (job.getTitle().toLowerCase().contains(query) ||
                    job.getLocation().toLowerCase().contains(query)) {
                jobList.add(job);
            }
        }
        jobAdapter.notifyDataSetChanged();
    }
}
