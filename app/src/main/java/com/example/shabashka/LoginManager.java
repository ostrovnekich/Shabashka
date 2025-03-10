package com.example.shabashka;

import android.app.Activity;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

public class LoginManager {
    private final FirebaseAuth firebaseAuth;
    private final Activity activity;

    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String exception);
    }

    public LoginManager(Activity activity) {
        this.activity = activity;
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public void loginUser(String email, String password, AuthCallback callback) {
        if (email.isEmpty() || password.isEmpty()) {
            callback.onFailure(activity.getString(R.string.enter_email_and_password));
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(firebaseAuth.getCurrentUser());
                    } else {
                        callback.onFailure(getErrorMessage(task.getException()));
                    }
                });
    }

    private String getErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            return activity.getString(R.string.incorrect_email_and_password);
        } else if (exception instanceof FirebaseNetworkException) {
            return activity.getString(R.string.internet_problems);
        } else {
            return exception.getMessage();
        }
    }
}
