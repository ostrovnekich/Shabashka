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

public class ResponsesFragment extends Fragment {
    private ResponsesJobAdapter jobAdapter;
    private final List<Job> allResponses = new ArrayList<>();
    private final List<Job> filteredResponses = new ArrayList<>();
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        jobAdapter = new ResponsesJobAdapter(requireActivity(), filteredResponses);
        recyclerView.setAdapter(jobAdapter);

        EditText searchInput = view.findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterResponses(s.toString());
            }
        });

        db = FirebaseFirestore.getInstance();
        loadUserResponses();
    }

    private void loadUserResponses() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();

        db.collection(getString(R.string.users_collection_path)).document(userId)
                .collection(getString(R.string.favorites_collection_path))
                .get()
                .addOnSuccessListener(favoritesSnapshot -> {
                    Set<String> favoriteJobIds = new HashSet<>();
                    for (QueryDocumentSnapshot favDoc : favoritesSnapshot) {
                        favoriteJobIds.add(favDoc.getId());
                    }

                    db.collection(getString(R.string.jobs_collection_path))
                            .whereEqualTo("approved", true)
                            .get()
                            .addOnSuccessListener(jobsSnapshot -> {
                                allResponses.clear();
                                filteredResponses.clear();

                                for (QueryDocumentSnapshot jobDoc : jobsSnapshot) {
                                    String jobId = jobDoc.getId();

                                    db.collection(getString(R.string.jobs_collection_path))
                                            .document(jobId)
                                            .collection(getString(R.string.applications_collection_path))
                                            .document(userId)
                                            .get()
                                            .addOnSuccessListener(applicationDoc -> {
                                                if (applicationDoc.exists()) {
                                                    Job job = jobDoc.toObject(Job.class);
                                                    if (favoriteJobIds.contains(jobId)) {
                                                        job.setFavorite(true);
                                                    }

                                                    allResponses.add(job);
                                                    filterResponses(getCurrentSearchQuery());
                                                }
                                            });
                                }
                            });
                });
    }


    @SuppressLint("NotifyDataSetChanged")
    private void filterResponses(String query) {
        filteredResponses.clear();
        filteredResponses.addAll(FilterUtils.filterJobs(allResponses, query));
        jobAdapter.notifyDataSetChanged();
    }

    private String getCurrentSearchQuery() {
        View view = getView();
        if (view != null) {
            EditText searchInput = view.findViewById(R.id.searchInput);
            return searchInput.getText().toString();
        }
        return "";
    }
}
