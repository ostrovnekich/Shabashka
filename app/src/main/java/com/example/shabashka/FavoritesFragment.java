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

public class FavoritesFragment extends Fragment {
    private JobAdapter jobAdapter;
    private final List<Job> allFavorites = new ArrayList<>();
    private final List<Job> filteredFavorites = new ArrayList<>();
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

        jobAdapter = new JobAdapter(requireActivity(), filteredFavorites);
        recyclerView.setAdapter(jobAdapter);

        EditText searchInput = view.findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFavorites(s.toString());
            }
        });

        db = FirebaseFirestore.getInstance();
        loadFavoriteJobs();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadFavoriteJobs() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            allFavorites.clear();
            filteredFavorites.clear();
            jobAdapter.notifyDataSetChanged();
            return;
        }

        String userId = currentUser.getUid();

        db.collection(getString(R.string.users_collection_path)).document(userId).collection(getString(R.string.favorites_collection_path))
                .get()
                .addOnSuccessListener(favoritesSnapshot -> {
                    if (favoritesSnapshot.isEmpty()) {
                        allFavorites.clear();
                        filteredFavorites.clear();
                        jobAdapter.notifyDataSetChanged();
                        return;
                    }

                    allFavorites.clear();
                    filteredFavorites.clear();

                    for (QueryDocumentSnapshot favDoc : favoritesSnapshot) {
                        String jobId = favDoc.getId();

                        db.collection(getString(R.string.jobs_collection_path)).document(jobId)
                                .get()
                                .addOnSuccessListener(jobDoc -> {
                                    if (jobDoc.exists()) {
                                        Job job = jobDoc.toObject(Job.class);
                                        if (job != null) {
                                            job.setFavorite(true);
                                            allFavorites.add(job);
                                            filterFavorites(getCurrentSearchQuery());
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    allFavorites.clear();
                    filteredFavorites.clear();
                    jobAdapter.notifyDataSetChanged();
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterFavorites(String query) {
        filteredFavorites.clear();
        filteredFavorites.addAll(FilterUtils.filterJobs(allFavorites, query));
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
