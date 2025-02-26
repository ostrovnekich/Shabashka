package com.example.shabashka;

import android.app.Activity;
import android.content.Intent;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;

public class GoogleLoginManager {
    private final FirebaseAuth firebaseAuth;
    private final GoogleSignInClient googleSignInClient;
    private final Activity activity;

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String errorMessage);
    }

    public GoogleLoginManager(Activity activity) {
        this.activity = activity;
        firebaseAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    public Intent getSignInIntent() {
        return googleSignInClient.getSignInIntent();
    }

    public void handleSignInResult(Intent data, AuthCallback callback) {
        try {
            GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
            firebaseAuthWithGoogle(account, callback);
        } catch (ApiException e) {
            callback.onFailure(activity.getString(R.string.login_failed));
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account, AuthCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(firebaseAuth.getCurrentUser());
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            if ((e.toString().toLowerCase().contains("network error") ||
                                    e.toString().toLowerCase().contains("no connection"))) {
                                callback.onFailure(activity.getString(R.string.internet_problems));
                            } else {
                                callback.onFailure(activity.getString(R.string.auth_error));
                            }
                        }
                    }
                });
    }
}
