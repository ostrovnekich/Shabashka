package com.example.shabashka;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ContactBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private final String phone;
    private final String email;

    public ContactBottomSheetDialogFragment(String phone, String email) {
        this.phone = phone;
        this.email = email;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.bottom_sheet_contacts, null);
        dialog.setContentView(view);

        TextView tvPhone = view.findViewById(R.id.tvPhone);
        TextView tvEmail = view.findViewById(R.id.tvEmail);
        Button btnClose = view.findViewById(R.id.btnClose);

        tvPhone.setText(String.format("%s %s", getString(R.string.phone), phone));
        tvEmail.setText(String.format("%s %s", getString(R.string.mail), email));

        btnClose.setOnClickListener(v -> dismiss());

        return dialog;
    }
}
