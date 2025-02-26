package com.example.shabashka;

import android.app.Activity;
import android.util.Patterns;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class RegisterManager {
    private final FirebaseAuth firebaseAuth;
    private final Activity activity;

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String exception);
    }

    public RegisterManager(Activity activity) {
        this.activity = activity;
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public String validateInput(String email, String password, String confirmPassword) {
        if (email.isEmpty()) {
            return activity.getString(R.string.email);
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return activity.getString(R.string.incorrect_email);
        }
        if (password.isEmpty()) {
            return activity.getString(R.string.password);
        }
        if (password.length() < 6) {
            return activity.getString(R.string.short_password);
        }
        if (!password.equals(confirmPassword)) {
            return activity.getString(R.string.different_passwords);
        }
        return null;
    }

    public void registerUser(String email, String password, String confirmPassword, AuthCallback callback) {
        String validationError = validateInput(email, password, confirmPassword);
        if (validationError != null) {
            callback.onFailure(validationError);
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(firebaseAuth.getCurrentUser());
                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseAuthUserCollisionException) {
                            callback.onFailure(activity.getString(R.string.email_already_used));
                        } else if (exception instanceof FirebaseNetworkException) {
                            callback.onFailure(activity.getString(R.string.internet_problems));
                        } else if (exception != null) {
                            callback.onFailure(exception.toString());
                        }
                    }
                });
    }
}
