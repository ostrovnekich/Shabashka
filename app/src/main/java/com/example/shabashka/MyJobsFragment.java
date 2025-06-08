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
import java.util.List;

public class MyJobsFragment extends Fragment {

    private MyJobAdapter myJobAdapter;
    private List<Job> jobList;
    private List<Job> fullJobList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_jobs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.myJobsRecyclerView);
        EditText searchInput = view.findViewById(R.id.searchInput);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        jobList = new ArrayList<>();
        fullJobList = new ArrayList<>();
        myJobAdapter = new MyJobAdapter(requireActivity(), jobList);
        recyclerView.setAdapter(myJobAdapter);

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

        loadMyJobs();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadMyJobs() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String currentUserId = currentUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(getString(R.string.users_collection_path))
                .document(currentUserId)
                .collection(getString(R.string.favorites_collection_path))
                .get()
                .addOnSuccessListener(favoritesSnapshot -> {
                    List<String> favoriteIds = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : favoritesSnapshot) {
                        favoriteIds.add(doc.getId());
                    }

                    db.collection("jobs")
                            .whereEqualTo("userId", currentUserId)
                            .whereEqualTo("approved", true)
                            .get()
                            .addOnSuccessListener(querySnapshot -> {
                                fullJobList.clear();
                                jobList.clear();

                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    Job job = document.toObject(Job.class);
                                    job.setFavorite(favoriteIds.contains(document.getId()));
                                    fullJobList.add(job);
                                    jobList.add(job);
                                }

                                myJobAdapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e ->
                                    ToastHelper.show(requireContext(), getString(R.string.error) + e.getMessage()));
                })
                .addOnFailureListener(e ->
                        ToastHelper.show(requireContext(), getString(R.string.error) + e.getMessage()));
    }


    @SuppressLint("NotifyDataSetChanged")
    private void filterJobs(String query) {
        List<Job> filtered = FilterUtils.filterJobs(fullJobList, query);
        jobList.clear();
        jobList.addAll(filtered);
        myJobAdapter.notifyDataSetChanged();
    }
}
