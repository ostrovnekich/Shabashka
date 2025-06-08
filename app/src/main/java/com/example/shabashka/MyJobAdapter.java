package com.example.shabashka;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class MyJobAdapter extends RecyclerView.Adapter<MyJobAdapter.MyJobViewHolder> {

    private final FragmentActivity activity;
    private final List<Job> jobList;

    public MyJobAdapter(FragmentActivity activity, List<Job> jobList) {
        this.activity = activity;
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public MyJobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_item_my, parent, false);
        return new MyJobViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyJobViewHolder holder, int position) {
        Job job = jobList.get(position);

        holder.jobTitle.setText(job.getTitle());
        holder.jobLocation.setText(job.getLocation());
        holder.jobSalary.setText(job.isHourly()
                ? job.getSalary() + " " + activity.getString(R.string.salary_per_hour)
                : job.getSalary() + " " + activity.getString(R.string.salary_fixed));

        holder.btnFavorite.setImageResource(job.isFavorite()
                ? R.drawable.ic_favorite_filled
                : R.drawable.ic_favorite_border);

        holder.btnFavorite.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                return;
            }

            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference favoriteDoc = db.collection(activity.getString(R.string.users_collection_path))
                    .document(userId)
                    .collection(activity.getString(R.string.favorites_collection_path))
                    .document(job.getJobId());

            if (job.isFavorite()) {
                favoriteDoc.delete()
                        .addOnSuccessListener(unused -> {
                            job.setFavorite(false);
                            notifyItemChanged(position);
                        })
                        .addOnFailureListener(e ->
                                ToastHelper.show(activity, activity.getString(R.string.error) + e.getMessage()));
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("jobId", job.getJobId());
                data.put("timestamp", FieldValue.serverTimestamp());

                favoriteDoc.set(data)
                        .addOnSuccessListener(unused -> {
                            job.setFavorite(true);
                            notifyItemChanged(position);
                        })
                        .addOnFailureListener(e ->
                                ToastHelper.show(activity, activity.getString(R.string.error) + e.getMessage()));
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                return;
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String jobId = job.getJobId();

            db.collection("jobs").document(jobId)
                    .delete()
                    .addOnSuccessListener(unused -> {
                        jobList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, jobList.size());
                    })
                    .addOnFailureListener(e ->
                            ToastHelper.show(activity, activity.getString(R.string.error) + e.getMessage()));
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    static class MyJobViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitle, jobLocation, jobSalary;
        ImageButton btnFavorite;
        Button btnDelete;
        public MyJobViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.jobTitle);
            jobLocation = itemView.findViewById(R.id.jobLocation);
            jobSalary = itemView.findViewById(R.id.jobSalary);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
