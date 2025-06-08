package com.example.shabashka;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView tvUserName = view.findViewById(R.id.tvUserName);
        ImageView userIcon = view.findViewById(R.id.userIcon);
        MaterialButton btnMyJobs = view.findViewById(R.id.btnMyJobs);
        MaterialButton btnResponded = view.findViewById(R.id.btnResponded);
        MaterialButton btnResponses = view.findViewById(R.id.btnResponses);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            String email = currentUser.getEmail();

            if (displayName != null && !displayName.isEmpty()) {
                tvUserName.setText(displayName);
            } else if (email != null) {
                tvUserName.setText(email);
            }
        }

        btnMyJobs.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.content_frame, new MyJobsFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        btnResponded.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.content_frame, new RespondedFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        btnResponses.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.content_frame, new ResponsesFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }
}
