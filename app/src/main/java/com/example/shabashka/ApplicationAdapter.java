package com.example.shabashka;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {

    private final FragmentActivity activity;
    private final List<Application> applicationList;

    public ApplicationAdapter(FragmentActivity activity, List<Application> applicationList) {
        this.activity = activity;
        this.applicationList = applicationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.application_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Application application = applicationList.get(position);

        holder.jobTitle.setText(application.getJobTitle());
        holder.applicantName.setText(activity.getString(R.string.responded) + application.getApplicantName());

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("ru"));
        holder.applicationDate.setText(sdf.format(application.getTimestamp().toDate()));

        FirebaseFirestore.getInstance()
                .collection(activity.getString(R.string.users_collection_path))
                .document(application.getApplicantId())
                .collection(activity.getString(R.string.ratings_collection_path))
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    float total = 0;
                    for (var doc : querySnapshot) {
                        Double value = doc.getDouble("value");
                        if (value != null) total += value;
                    }
                    float avg = !querySnapshot.isEmpty() ? total / querySnapshot.size() : 0;
                    holder.applicantRating.setRating(avg);
                })
                .addOnFailureListener(e -> holder.applicantRating.setRating(0));

        holder.btnContact.setOnClickListener(v -> {
            ContactBottomSheetDialogFragment dialog = new ContactBottomSheetDialogFragment(application.getPhone());
            dialog.show(activity.getSupportFragmentManager(), "ContactSheet");
        });

        holder.btnMarkDone.setOnClickListener(v -> markAsDone(application, position));
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    private void markAsDone(Application application, int position) {
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_rate_user, null);
        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);

        new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.grade))
                .setView(dialogView)
                .setPositiveButton(activity.getString(R.string.submit), (dialog, which) -> {
                    float rating = ratingBar.getRating();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection(activity.getString(R.string.users_collection_path))
                            .document(application.getApplicantId())
                            .collection(activity.getString(R.string.ratings_collection_path))
                            .add(new Rating(rating))
                            .addOnSuccessListener(unused -> db.collection(activity.getString(R.string.jobs_collection_path))
                                    .document(application.getJobId())
                                    .delete()
                                    .addOnSuccessListener(unused2 -> {
                                        applicationList.remove(position);
                                        notifyItemRemoved(position);
                                    }))
                            .addOnFailureListener(e -> ToastHelper.show(activity, activity.getString(R.string.error) + e.getMessage()));
                })
                .setNegativeButton(activity.getString(R.string.cancel), null)
                .show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitle, applicantName, applicationDate;
        Button btnMarkDone, btnContact;
        RatingBar applicantRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.jobTitle);
            applicantName = itemView.findViewById(R.id.applicantName);
            applicationDate = itemView.findViewById(R.id.applicationDate);
            btnMarkDone = itemView.findViewById(R.id.btnMarkDone);
            btnContact = itemView.findViewById(R.id.btnContact);
            applicantRating = itemView.findViewById(R.id.applicantRating);
        }
    }
}
