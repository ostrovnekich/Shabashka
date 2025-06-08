package com.example.shabashka;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {

    private static final String PREFS_NAME = "settings_prefs";
    private static final String KEY_THEME = "theme";

    private String[] themeLabels;
    private final String[] themeValues = {"system", "light", "dark"};

    public SettingsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        themeLabels = new String[]{
                getString(R.string.system),
                getString(R.string.light),
                getString(R.string.dark)
        };

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner themeSpinner = view.findViewById(R.id.theme_spinner);
        Button logoutButton = view.findViewById(R.id.logout_button);

        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String currentTheme = prefs.getString(KEY_THEME, "system");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, themeLabels
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        themeSpinner.setAdapter(adapter);

        int selectedIndex = 0;
        for (int i = 0; i < themeValues.length; i++) {
            if (themeValues[i].equals(currentTheme)) {
                selectedIndex = i;
                break;
            }
        }
        themeSpinner.setSelection(selectedIndex);

        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean first = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (first) {
                    first = false;
                    return;
                }

                String selected = themeValues[position];
                prefs.edit().putString(KEY_THEME, selected).apply();

                AppCompatDelegate.setDefaultNightMode(
                        selected.equals("light") ? AppCompatDelegate.MODE_NIGHT_NO :
                                selected.equals("dark") ? AppCompatDelegate.MODE_NIGHT_YES :
                                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
