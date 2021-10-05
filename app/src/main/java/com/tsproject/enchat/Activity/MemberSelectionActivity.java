package com.tsproject.enchat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tsproject.enchat.Adapter.MemberSelectionAdapter;
import com.tsproject.enchat.Model.User;
import com.tsproject.enchat.R;
import com.tsproject.enchat.databinding.ActivityMemberSelectionBinding;

import java.util.ArrayList;

public class MemberSelectionActivity extends AppCompatActivity {

    ActivityMemberSelectionBinding binding;
    MemberSelectionAdapter adapter;
    DataSnapshot userList;
    ArrayList<User> friendList;
    String uID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMemberSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        friendList = new ArrayList<>();
        uID = FirebaseAuth.getInstance().getUid();


        adapter = new MemberSelectionAdapter(this, friendList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseDatabase.getInstance()
                .getReference()
                .child("user")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = new User();
                        for (DataSnapshot snap : snapshot.child(uID).child("connectedUser").getChildren()) {
                            user.setuID(snap.getKey());
                            friendList.add(user);
                        }
                        for (User u : friendList) {
                            u.setUserName(snapshot.child(u.getuID()).child("userName").getValue().toString());
                            u.setPhnNum(snapshot.child(u.getuID()).child("phnNum").getValue().toString());
                            u.setImageURL(snapshot.child(u.getuID()).child("imageURL").getValue().toString());
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        Log.d("CHECK_MID2", "" + friendList.size());
    }
}