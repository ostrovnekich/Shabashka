package com.example.shabashka;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {
    private final List<Job> jobList;
    private final FragmentActivity activity;

    public JobAdapter(FragmentActivity activity, List<Job> jobList) {
        this.activity = activity;
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_item, parent, false);
        return new JobViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.jobTitle.setText(job.getTitle());
        holder.jobLocation.setText(job.getLocation());

        if (activity != null) {
            if (job.isHourly()) {
                holder.jobSalary.setText(String.format("%s %s", job.getSalary(), activity.getString(R.string.salary_per_hour)));
            } else {
                holder.jobSalary.setText(String.format("%s %s", job.getSalary(), activity.getString(R.string.salary_fixed)));
            }

            holder.itemView.setOnClickListener(v -> {
                JobDetailsFragment fragment = new JobDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("title", job.getTitle());
                bundle.putString("location", job.getLocation());
                bundle.putString("salary", job.getSalary());
                bundle.putBoolean("hourly", job.isHourly());
                bundle.putString("description", job.getDescription());
                fragment.setArguments(bundle);

                if (activity instanceof BaseActivity) {
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            });

            holder.btnContact.setOnClickListener(v -> {
                ContactBottomSheetDialogFragment dialog = new ContactBottomSheetDialogFragment(
                        job.getPhone(),
                        job.getEmail()
                );
                dialog.show(activity.getSupportFragmentManager(), "ContactSheet");
            });
        }
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        public View btnContact;
        TextView jobTitle, jobLocation, jobSalary;

        public JobViewHolder(View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.jobTitle);
            jobLocation = itemView.findViewById(R.id.jobLocation);
            jobSalary = itemView.findViewById(R.id.jobSalary);
            btnContact = itemView.findViewById(R.id.btnContact);
        }
    }
}
