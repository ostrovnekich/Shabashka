package com.example.shabashka;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateFragment extends Fragment {
    private EditText etTitle, etDescription, etLocation, etSalary;
    private CheckBox cbHourly;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    private static final int TITLE_MAX_LENGTH = 50;
    private static final int DESCRIPTION_MAX_LENGTH = 500;
    private static final int LOCATION_MAX_LENGTH = 100;
    private static final int SALARY_MAX_LENGTH = 20;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        etTitle = view.findViewById(R.id.et_title);
        etDescription = view.findViewById(R.id.et_description);
        etLocation = view.findViewById(R.id.et_location);
        etSalary = view.findViewById(R.id.et_salary);
        cbHourly = view.findViewById(R.id.cb_hourly);
        Button btnCreateJob = view.findViewById(R.id.btn_create_job);

        addTextLimitWatcher(etTitle, TITLE_MAX_LENGTH);
        addTextLimitWatcher(etDescription, DESCRIPTION_MAX_LENGTH);
        addTextLimitWatcher(etLocation, LOCATION_MAX_LENGTH);
        addTextLimitWatcher(etSalary, SALARY_MAX_LENGTH);

        btnCreateJob.setOnClickListener(v -> saveJobToFirestore());

        return view;
    }

    private void saveJobToFirestore() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String salary = etSalary.getText().toString().trim();
        boolean hourly = cbHourly.isChecked();
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "unknown";

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(location) || TextUtils.isEmpty(salary)) {
            ToastHelper.show(getActivity(), getString(R.string.fill_fields));
            return;
        }

        Map<String, Object> job = new HashMap<>();
        job.put("title", title);
        job.put("description", description);
        job.put("location", location);
        job.put("salary", salary);
        job.put("hourly", hourly);
        job.put("userId", userId);

        firestore.collection(getString(R.string.jobs_collection_path))
                .add(job)
                .addOnSuccessListener(documentReference ->
                        ToastHelper.show(getActivity(), getString(R.string.shabashka_added))
                )
                .addOnFailureListener(e ->
                        ToastHelper.show(getActivity(), getString(R.string.error) + e.getMessage())
                );
    }

    private void addTextLimitWatcher(EditText editText, int maxLength) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > maxLength) {
                    editText.setText(s.subSequence(0, maxLength));
                    editText.setSelection(maxLength);
                    ToastHelper.show(getActivity(), getString(R.string.max_length) + maxLength);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}
