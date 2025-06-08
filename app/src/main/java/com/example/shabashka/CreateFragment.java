package com.example.shabashka;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateFragment extends Fragment {
    private EditText etTitle, etDescription, etSalary, etPhone;
    private AutoCompleteTextView etLocation;
    private CheckBox cbHourly;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private PlacesClient placesClient;

    private static final int TITLE_MAX_LENGTH = 70;
    private static final int DESCRIPTION_MAX_LENGTH = 2000;
    private static final int LOCATION_MAX_LENGTH = 100;
    private static final int SALARY_MAX_LENGTH = 20;
    private static final int PHONE_MAX_LENGTH = 13;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        etTitle = view.findViewById(R.id.et_title);
        etDescription = view.findViewById(R.id.et_description);
        etLocation = view.findViewById(R.id.et_location);
        etSalary = view.findViewById(R.id.et_salary);
        etPhone = view.findViewById(R.id.et_phone);
        cbHourly = view.findViewById(R.id.cb_hourly);
        Button btnCreateJob = view.findViewById(R.id.btn_create_job);

        addTextLimitWatcher(etTitle, TITLE_MAX_LENGTH);
        addTextLimitWatcher(etDescription, DESCRIPTION_MAX_LENGTH);
        addTextLimitWatcher(etLocation, LOCATION_MAX_LENGTH);
        addTextLimitWatcher(etSalary, SALARY_MAX_LENGTH);
        addTextLimitWatcher(etPhone, PHONE_MAX_LENGTH);

        // Places init
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.google_maps_key), new Locale("ru"));
        }
        placesClient = Places.createClient(requireContext());

        etLocation.setThreshold(1);
        etLocation.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 2) {
                    findPlacePredictions(s.toString());
                }
            }
        });

        btnCreateJob.setOnClickListener(v -> saveJobToFirestore());

        return view;
    }

    private void findPlacePredictions(String query) {
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .setSessionToken(null)
                .build();

        placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener(response -> {
                    List<String> suggestions = new ArrayList<>();
                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                        suggestions.add(prediction.getFullText(null).toString());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            suggestions
                    );
                    etLocation.setAdapter(adapter);
                    etLocation.showDropDown();
                });
    }

    private void saveJobToFirestore() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String salary = etSalary.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        boolean hourly = cbHourly.isChecked();
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "unknown";

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)
                || TextUtils.isEmpty(location) || TextUtils.isEmpty(salary)
                || TextUtils.isEmpty(phone)) {
            ToastHelper.show(getActivity(), getString(R.string.fill_fields));
            return;
        }

        DocumentReference newJobRef = firestore.collection(getString(R.string.jobs_collection_path)).document();
        String jobId = newJobRef.getId();

        Map<String, Object> job = new HashMap<>();
        job.put("title", title);
        job.put("description", description);
        job.put("location", location);
        job.put("salary", salary);
        job.put("hourly", hourly);
        job.put("userId", userId);
        job.put("phone", phone);
        job.put("jobId", jobId);
        job.put("approved", false);

        newJobRef.set(job)
                .addOnSuccessListener(unused ->
                        ToastHelper.show(getActivity(), getString(R.string.for_moderation))
                )
                .addOnFailureListener(e ->
                        ToastHelper.show(getActivity(), getString(R.string.error) + e.getMessage())
                );
    }


    private void addTextLimitWatcher(EditText editText, int maxLength) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > maxLength) {
                    editText.setText(s.subSequence(0, maxLength));
                    editText.setSelection(maxLength);
                    ToastHelper.show(getActivity(), getString(R.string.max_length) + maxLength);
                }
            }
        });
    }
}
