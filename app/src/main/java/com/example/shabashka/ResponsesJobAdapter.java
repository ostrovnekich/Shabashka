package com.example.shabashka;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponsesJobAdapter extends RecyclerView.Adapter<ResponsesJobAdapter.JobViewHolder> {
    private final List<Job> jobList;
    private final FragmentActivity activity;

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<Job> filteredList) {
        jobList.clear();
        jobList.addAll(filteredList);
        notifyDataSetChanged();
    }

    public ResponsesJobAdapter(FragmentActivity activity, List<Job> jobList) {
        this.activity = activity;
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_item_responses, parent, false);
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
                        job.getPhone()
                );
                dialog.show(activity.getSupportFragmentManager(), "ContactSheet");
            });
        }

        holder.btnFavorite.setImageResource(job.isFavorite() ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);

        final int pos = position;

        holder.btnFavorite.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                return;
            }

            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            assert activity != null;
            DocumentReference favoriteDoc = db.collection(activity.getString(R.string.users_collection_path))
                    .document(userId)
                    .collection(activity.getString(R.string.favorites_collection_path))
                    .document(job.getJobId());

            if (job.isFavorite()) {
                favoriteDoc.delete()
                        .addOnSuccessListener(unused -> {
                            job.setFavorite(false);
                            notifyItemChanged(pos);
                        })
                        .addOnFailureListener(e -> ToastHelper.show(activity, activity.getString(R.string.error) + e.getMessage()));
            } else {
                Map<String, Object> favoriteData = new HashMap<>();
                favoriteData.put("jobId", job.getJobId());
                favoriteData.put("timestamp", FieldValue.serverTimestamp());

                favoriteDoc.set(favoriteData)
                        .addOnSuccessListener(unused -> {
                            job.setFavorite(true);
                            notifyItemChanged(pos);
                        })
                        .addOnFailureListener(e -> ToastHelper.show(activity, activity.getString(R.string.error) + e.getMessage()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        public View btnContact;
        public View btnApply;
        public ImageButton btnFavorite;
        TextView jobTitle, jobLocation, jobSalary;

        public JobViewHolder(View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.jobTitle);
            jobLocation = itemView.findViewById(R.id.jobLocation);
            jobSalary = itemView.findViewById(R.id.jobSalary);
            btnContact = itemView.findViewById(R.id.btnContact);
            btnApply = itemView.findViewById(R.id.btnApply);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}
