package com.tsproject.enchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    Button btnNext;
    EditText etName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        btnNext = findViewById(R.id.btnProfileNext);
        etName = findViewById(R.id.etName);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rec_name = etName.getText().toString().trim();
                if(rec_name.equals(""))
                {
                    etName.setError("Enter your name");
                }
                else
                {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if(currentUser != null) {
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference dRef = db.getReference().child("user");
                        String rec_number = getIntent().getStringExtra("number");
                        User user = new User(rec_name, rec_number);
                        dRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(!snapshot.exists())
                                {
                                       dRef.push().setValue(user);
                                }
                                else
                                {
                                    Toast.makeText(ProfileActivity.this, "User already exists!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }
            }
        });
    }
}