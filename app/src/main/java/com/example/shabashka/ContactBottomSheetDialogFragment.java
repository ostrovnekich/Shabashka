package com.example.shabashka;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ContactBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private final String phone;

    public ContactBottomSheetDialogFragment(String phone) {
        this.phone = phone;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.bottom_sheet_contacts, null);
        dialog.setContentView(view);

        TextView tvPhone = view.findViewById(R.id.tvPhone);
        Button btnCall = view.findViewById(R.id.btnCall);

        tvPhone.setText(getString(R.string.phone) + " " + phone);

        btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);
        });

        return dialog;
    }
}
