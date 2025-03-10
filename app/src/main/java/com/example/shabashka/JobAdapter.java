package com.example.shabashka;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {
    private final List<Job> jobList;
    private final Activity activity;
    public JobAdapter(Activity activity, List<Job> jobList) {
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

        if (job.isHourly()) {
            holder.jobSalary.setText(String.format(activity.getString(R.string.space), job.getSalary(), activity.getString(R.string.salary_per_hour)));
        } else {
            holder.jobSalary.setText(String.format(activity.getString(R.string.space), job.getSalary(), activity.getString(R.string.salary_fixed)));
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, JobDetailsActivity.class);
            intent.putExtra(activity.getString(R.string.title), job.getTitle());
            intent.putExtra(activity.getString(R.string.location), job.getLocation());
            intent.putExtra(activity.getString(R.string.salary), job.getSalary());
            intent.putExtra(activity.getString(R.string.hourly), job.isHourly());
            intent.putExtra(activity.getString(R.string.description), job.getDescription());
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitle, jobDescription, jobLocation, jobSalary;

        public JobViewHolder(View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.jobTitle);
            jobLocation = itemView.findViewById(R.id.jobLocation);
            jobSalary = itemView.findViewById(R.id.jobSalary);
        }
    }
}

