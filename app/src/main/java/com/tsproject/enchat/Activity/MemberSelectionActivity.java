package com.tsproject.enchat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberSelectionActivity extends AppCompatActivity {

    private ActivityMemberSelectionBinding binding;
    private MemberSelectionAdapter adapter;
    private ArrayList<User> friendList;
    private String uID;

    public static List<String> selectedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMemberSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        selectedList = new ArrayList<>();
        friendList = new ArrayList<>();
        uID = FirebaseAuth.getInstance().getUid();

        adapter = new MemberSelectionAdapter(this, friendList, binding.tvSelectionCount);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

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

        binding.mcvCreateGroup.setOnClickListener(view -> createGroupOnClick());

    }

    private void createGroupOnClick() {
        if (binding.etGroupName.getText().toString().replaceAll(" ", "").length() == 0) {
            Toast.makeText(this, "Group name cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (selectedList.size() == 0) {
            Toast.makeText(this, "Select at least 1 member", Toast.LENGTH_SHORT).show();
        } else {
            String groupName = binding.etGroupName.getText().toString();
            String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();

            HashMap<String, Integer> mapper = new HashMap<>();
            selectedList.add(uID);
            for (String it : selectedList) {
                mapper.put(it, 0);
            }
            mapper.put(uID, 0);
            mapper.put("type", 1);

            FirebaseDatabase.getInstance().getReference().child("chat").child(key).setValue(mapper);
            FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("GroupName").setValue(groupName);
            FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("members").setValue(selectedList);

            Toast.makeText(this, "Group created", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }
    @Override
    protected void onResume() {
        ChatActivity.checkOnlineStatus("online");
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //set offline and last seen
        //gettime Stamp
        String timeStamp = String.valueOf(System.currentTimeMillis());
        ChatActivity.checkOnlineStatus(timeStamp);

    }

    @Override
    protected void onStart() {
        //set online
        ChatActivity.checkOnlineStatus("online");
        super.onStart();

    }


}