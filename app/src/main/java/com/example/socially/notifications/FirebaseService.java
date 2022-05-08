package com.example.socially.notifications;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

public class FirebaseService extends FirebaseMessagingService {

    private String firebaseURL = "https://socially-14fd2-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String tokenRefresh = FirebaseInstanceId.getInstance().getToken();
//        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
//            try {
//                String tokenRefresh = instanceIdResult.getToken();
                if (user != null) {
                    updateToken(tokenRefresh);
                }
//            } catch (Exception e) {
//                System.out.println(e);
//            }
//        });

    }

    private void updateToken(String tokenRefresh) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance(firebaseURL).getReference("Tokens");
        Token token = new Token(tokenRefresh);
        ref.child(user.getUid()).setValue(token);
    }
}
