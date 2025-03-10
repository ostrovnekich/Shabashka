package com.example.shabashka;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class JobDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_job_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvLocation = findViewById(R.id.tvLocation);
        TextView tvSalary = findViewById(R.id.tvSalary);
        TextView tvDescription = findViewById(R.id.tvDescription);

        String title = getIntent().getStringExtra(getString(R.string.title));
        String location = getIntent().getStringExtra(getString(R.string.location));
        String salary = getIntent().getStringExtra(getString(R.string.salary));
        boolean hourly = getIntent().getBooleanExtra(getString(R.string.hourly), false);
        String description = getIntent().getStringExtra(getString(R.string.description));

        tvTitle.setText(title);
        tvLocation.setText(location);
        tvSalary.setText(hourly ? String.format(getString(R.string.space), salary, getString(R.string.salary_per_hour)) : String.format(getString(R.string.space), salary, getString(R.string.salary_fixed)));
        tvDescription.setText(description);
    }
}