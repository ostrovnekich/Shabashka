package com.example.shabashka;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminVacancyAdapter extends RecyclerView.Adapter<AdminVacancyAdapter.VacancyViewHolder> {

    public interface OnApproveClickListener {
        void onApproveClick(Vacancy vacancy, int position);
    }

    private final List<Vacancy> vacancyList;
    private final OnApproveClickListener approveClickListener;

    public AdminVacancyAdapter(List<Vacancy> vacancyList, OnApproveClickListener listener) {
        this.vacancyList = vacancyList;
        this.approveClickListener = listener;
    }

    @NonNull
    @Override
    public VacancyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item, parent, false);
        return new VacancyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VacancyViewHolder holder, int position) {
        Vacancy vacancy = vacancyList.get(position);
        holder.titleText.setText(vacancy.getTitle());
        holder.descriptionText.setText(vacancy.getDescription());
        holder.locationTextView.setText("Местоположение: " + vacancy.getLocation()); // ✅
        holder.approveButton.setOnClickListener(v -> {
            if (approveClickListener != null) {
                approveClickListener.onApproveClick(vacancy, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return vacancyList.size();
    }

    static class VacancyViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, descriptionText, locationTextView;
        Button approveButton;

        public VacancyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.admin_item_title);
            descriptionText = itemView.findViewById(R.id.admin_item_description);
            approveButton = itemView.findViewById(R.id.btnApprove);
            locationTextView = itemView.findViewById(R.id.admin_item_location);
        }
    }
}
