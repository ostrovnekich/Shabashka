package com.example.shabashka;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {

    public static void saveUserToFirestore(FirebaseUser user) {
        if (user == null) return;

        String uid = user.getUid();
        String email = user.getEmail();
        String name = user.getDisplayName();

        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("name", name != null ? name : email);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String phone = documentSnapshot.getString("phone");
                        if (phone != null) {
                            userData.put("phone", phone);
                        }
                    }

                    db.collection("users")
                            .document(uid)
                            .set(userData);
                });
    }
}
