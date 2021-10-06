package com.tsproject.enchat.Extra;

import android.app.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token) {

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        FirebaseDatabase.getInstance()
                .getReference()
                .child("user")
                .child(FirebaseAuth.getInstance().getUid())
                .child("token")
                .setValue(token);
    }

}
