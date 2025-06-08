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

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RespondedFragment extends Fragment {

    private ApplicationAdapter adapter;
    private final List<Application> applicationList = new ArrayList<>();
    private final List<Application> originalList = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText searchInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_responded, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.RespondedRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ApplicationAdapter(requireActivity(), applicationList);
        recyclerView.setAdapter(adapter);

        searchInput = view.findViewById(R.id.searchInput);
        setupSearch();

        loadRespondedApplications();
    }

    private void setupSearch() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filter(String text) {
        List<Application> filteredList = new ArrayList<>();
        for (Application app : originalList) {
            if (app.getApplicantName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(app);
            }
        }
        applicationList.clear();
        applicationList.addAll(filteredList);
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadRespondedApplications() {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        db.collection(getString(R.string.jobs_collection_path))
                .whereEqualTo("userId", userId)
                .whereEqualTo("approved", true)
                .get(Source.SERVER)
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot jobDoc : querySnapshot) {
                        String jobId = jobDoc.getId();
                        String jobTitle = jobDoc.getString("title");

                        db.collection(getString(R.string.jobs_collection_path))
                                .document(jobId)
                                .collection(getString(R.string.applications_collection_path))
                                .get(Source.SERVER)
                                .addOnSuccessListener(applicationsSnapshot -> {
                                    for (QueryDocumentSnapshot appDoc : applicationsSnapshot) {
                                        String applicantId = appDoc.getId();
                                        Timestamp timestamp = appDoc.getTimestamp("timestamp");

                                        db.collection(getString(R.string.users_collection_path))
                                                .document(applicantId)
                                                .get(Source.SERVER)
                                                .addOnSuccessListener(userDoc -> {
                                                    String email = userDoc.getString("email");
                                                    String name = userDoc.getString("name");
                                                    String surname = userDoc.getString("surname");
                                                    String phone = userDoc.getString("phone");

                                                    String displayName = (name != null && surname != null)
                                                            ? name + " " + surname
                                                            : Objects.requireNonNullElseGet(name, () -> Objects.requireNonNullElse(email, ""));

                                                    Application application = new Application(
                                                            jobTitle,
                                                            displayName,
                                                            timestamp,
                                                            jobId,
                                                            applicantId
                                                    );
                                                    application.setPhone(phone);

                                                    db.collection(getString(R.string.users_collection_path))
                                                            .document(applicantId)
                                                            .collection(getString(R.string.ratings_collection_path))
                                                            .get(Source.SERVER)
                                                            .addOnSuccessListener(ratingsSnapshot -> {
                                                                float total = 0;
                                                                int count = ratingsSnapshot.size();

                                                                for (DocumentSnapshot ratingDoc : ratingsSnapshot) {
                                                                    Number rating = ratingDoc.getDouble("rating");
                                                                    if (rating != null) {
                                                                        total += rating.floatValue();
                                                                    }
                                                                }

                                                                if (count > 0) {
                                                                    float average = total / count;
                                                                    application.setRating(average);
                                                                }

                                                                applicationList.add(application);
                                                                originalList.add(application);
                                                                adapter.notifyDataSetChanged();
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                applicationList.add(application);
                                                                originalList.add(application);
                                                                adapter.notifyDataSetChanged();
                                                            });
                                                });
                                    }
                                });
                    }
                });
    }

}
