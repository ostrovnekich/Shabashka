package com.example.shabashka;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {
    private final List<Job> jobList;
    private final FragmentActivity activity;

    public JobAdapter(FragmentActivity activity, List<Job> jobList) {
        this.activity = activity;
        this.jobList = jobList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<Job> filteredList) {
        jobList.clear();
        jobList.addAll(filteredList);
        notifyDataSetChanged();
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

            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        holder.btnContact.setOnClickListener(v -> {
            ContactBottomSheetDialogFragment dialog = new ContactBottomSheetDialogFragment(job.getPhone());
            dialog.show(activity.getSupportFragmentManager(), "ContactSheet");
        });

        holder.btnApply.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                return;
            }

            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(userId)
                    .get(Source.SERVER)
                    .addOnSuccessListener(userDoc -> {
                        String phone = null;
                        if (userDoc.exists() && userDoc.contains("phone")) {
                            phone = userDoc.getString("phone");
                        }

                        if (phone == null || phone.trim().isEmpty()) {
                            showPhoneInputDialog(activity, userId, () -> sendApplication(job, currentUser));
                        } else {
                            sendApplication(job, currentUser);
                        }
                    });
        });

        holder.btnFavorite.setImageResource(job.isFavorite() ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
        final int pos = position;

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

    private void showPhoneInputDialog(FragmentActivity activity, String userId, Runnable onPhoneSaved) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.enter_phone));

        final EditText input = new EditText(activity);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        input.setHint(activity.getString(R.string.number_pattern));
        builder.setView(input);

        builder.setPositiveButton(activity.getString(R.string.save), (dialog, which) -> {
            String phone = input.getText().toString().trim();
            if (!phone.isEmpty()) {
                FirebaseFirestore.getInstance()
                        .collection(activity.getString(R.string.users_collection_path))
                        .document(userId)
                        .set(Collections.singletonMap("phone", phone), SetOptions.merge())
                        .addOnSuccessListener(unused -> onPhoneSaved.run())
                        .addOnFailureListener(e -> ToastHelper.show(activity, activity.getString(R.string.error) + e.getMessage()));
            } else {
                ToastHelper.show(activity, activity.getString(R.string.enter_correct_number));
            }
        });

        builder.setNegativeButton(activity.getString(R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void sendApplication(Job job, FirebaseUser currentUser) {
        String userId = currentUser.getUid();
        String userName = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : activity.getString(R.string.user);

        Map<String, Object> application = new HashMap<>();
        application.put("userId", userId);
        application.put("userName", userName);
        application.put("timestamp", FieldValue.serverTimestamp());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(activity.getString(R.string.jobs_collection_path))
                .document(job.getJobId())
                .collection(activity.getString(R.string.applications_collection_path))
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        ToastHelper.show(activity, activity.getString(R.string.already_responded));
                    } else {
                        db.collection(activity.getString(R.string.jobs_collection_path))
                                .document(job.getJobId())
                                .collection(activity.getString(R.string.applications_collection_path))
                                .document(userId)
                                .set(application)
                                .addOnSuccessListener(unused -> ToastHelper.show(activity, activity.getString(R.string.response_sent)))
                                .addOnFailureListener(e -> ToastHelper.show(activity, activity.getString(R.string.error) + e.getMessage()));
                    }
                });
    }
}
