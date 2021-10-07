package com.tsproject.enchat.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.tsproject.enchat.R;
import com.tsproject.enchat.databinding.ActivityGroupDetailsBinding;

public class GroupDetails extends AppCompatActivity {

    String chatID;
    String groupAdmin;
    String uID;
    ActivityGroupDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        chatID = getIntent().getExtras().getString("chatID");
        groupAdmin = getIntent().getExtras().getString("groupAdmin");
        uID = FirebaseAuth.getInstance().getUid();

        if(uID.equals(groupAdmin)) {
            binding.cvDeleteGroup.setVisibility(View.VISIBLE);
        } else {
            binding.cvDeleteGroup.setVisibility(View.GONE);
        }

    }
}