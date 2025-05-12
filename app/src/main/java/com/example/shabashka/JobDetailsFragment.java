package com.example.shabashka;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class JobDetailsFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_LOCATION = "location";
    private static final String ARG_SALARY = "salary";
    private static final String ARG_HOURLY = "hourly";
    private static final String ARG_DESCRIPTION = "description";

    public static JobDetailsFragment newInstance(String title, String location, String salary, boolean hourly, String description) {
        JobDetailsFragment fragment = new JobDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_LOCATION, location);
        args.putString(ARG_SALARY, salary);
        args.putBoolean(ARG_HOURLY, hourly);
        args.putString(ARG_DESCRIPTION, description);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_job_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvLocation = view.findViewById(R.id.tvLocation);
        TextView tvSalary = view.findViewById(R.id.tvSalary);
        TextView tvDescription = view.findViewById(R.id.tvDescription);

        Bundle args = getArguments();
        if (args != null) {
            String title = args.getString(ARG_TITLE);
            String location = args.getString(ARG_LOCATION);
            String salary = args.getString(ARG_SALARY);
            boolean hourly = args.getBoolean(ARG_HOURLY);
            String description = args.getString(ARG_DESCRIPTION);

            tvTitle.setText(title);
            tvLocation.setText(location);
            tvSalary.setText(hourly
                    ? String.format("%s %s", salary, getString(R.string.salary_per_hour))
                    : String.format("%s %s", salary, getString(R.string.salary_fixed)));
            tvDescription.setText(description);
        }
    }
}
