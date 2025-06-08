package com.example.shabashka;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private LoginManager loginManager;
    private GoogleLoginManager googleLoginManager;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = loginManager.getCurrentUser();
        if (currentUser != null) {
            openProperActivity(currentUser);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView tvRegister = findViewById(R.id.tv_register);
        Button btnGoogleSignIn = findViewById(R.id.btn_google_sign_in);

        loginManager = new LoginManager(this);
        googleLoginManager = new GoogleLoginManager(this);

        btnLogin.setOnClickListener(v -> loginUser());

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        btnGoogleSignIn.setOnClickListener(v ->
                startActivityForResult(googleLoginManager.getSignInIntent(), 9001)
        );
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        loginManager.loginUser(email, password, new LoginManager.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                openProperActivity(user);
            }

            @Override
            public void onFailure(String errorMessage) {
                ToastHelper.show(LoginActivity.this, errorMessage);
            }
        });
    }

    private void openProperActivity(FirebaseUser user) {
        Intent intent;
        if ("shabashkaadmin@gmail.com".equalsIgnoreCase(user.getEmail())) {
            intent = new Intent(LoginActivity.this, AdminActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, BaseActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9001) {
            googleLoginManager.handleSignInResult(data, new GoogleLoginManager.AuthCallback() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    UserRepository.saveUserToFirestore(user);
                    openProperActivity(user);
                }

                @Override
                public void onFailure(String errorMessage) {
                    ToastHelper.show(LoginActivity.this, errorMessage);
                }
            });
        }
    }
}
