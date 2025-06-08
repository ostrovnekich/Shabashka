package com.example.shabashka;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminVacancyAdapter adapter;
    private final ArrayList<Vacancy> vacancyList = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference vacanciesRef = db.collection("jobs");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        recyclerView = findViewById(R.id.admin_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminVacancyAdapter(vacancyList, this::approveVacancy);
        recyclerView.setAdapter(adapter);

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        loadUnapprovedVacancies();
    }

    private void loadUnapprovedVacancies() {
        vacanciesRef.whereEqualTo("approved", false)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    vacancyList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Vacancy vacancy = doc.toObject(Vacancy.class);
                        vacancy.setId(doc.getId());
                        vacancyList.add(vacancy);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Ошибка загрузки вакансий", Toast.LENGTH_SHORT).show()
                );
    }

    private void approveVacancy(Vacancy vacancy, int position) {
        vacanciesRef.document(vacancy.getId())
                .update("approved", true)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Вакансия одобрена", Toast.LENGTH_SHORT).show();
                    vacancyList.remove(position);
                    adapter.notifyItemRemoved(position);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Ошибка при одобрении", Toast.LENGTH_SHORT).show()
                );
    }
}
