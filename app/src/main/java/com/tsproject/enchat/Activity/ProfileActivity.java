package com.tsproject.enchat.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tsproject.enchat.R;

public class ProfileActivity extends AppCompatActivity {
    EditText etShowAbout, etShowName, etShowNumber;
    public static final String promptText = "Hey there!I am using EnChat";
    FirebaseDatabase db;
    DatabaseReference dRef;
    FirebaseUser currentUser;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etShowName = findViewById(R.id.etShowName);
        etShowAbout = findViewById(R.id.eTShowAbout);
        etShowNumber = findViewById(R.id.etShowNumber);


       auth = FirebaseAuth.getInstance();
       currentUser = auth.getCurrentUser();
       db = FirebaseDatabase.getInstance();
       dRef = db.getReference("user").child("profile");

    /*    etShowName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etShowName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        });*/
    }
}