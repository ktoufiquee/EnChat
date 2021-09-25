package com.tsproject.enchat.Extra;
//
import android.os.Debug;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Utility {
//
//    private User user;
//
//    public void getUserObj(String uID) {
//        Log.d("GET_USER", "METHOD CALLED");
//        FirebaseDatabase.getInstance().getReference().child("user").child(uID).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Log.d("GET_USER", snapshot.exists() + "");
//                if(snapshot.exists()) {
//                    String userName = "";
//                    String phnNum = "";
//                    String contactPermission = "";
//                    if(snapshot.child("userName").getValue() != null) {
//                        userName = snapshot.child("userName").getValue().toString();
//                    }
//                    if(snapshot.child("phnNum").getValue() != null) {
//                        phnNum = snapshot.child("phnNum").getValue().toString();
//                    }
//                    if(snapshot.child("contactPermission").getValue() != null) {
//                        contactPermission = snapshot.child("contactPermission").getValue().toString();
//                    }
//                    setUser(new User(userName, phnNum, contactPermission, uID));
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
}
