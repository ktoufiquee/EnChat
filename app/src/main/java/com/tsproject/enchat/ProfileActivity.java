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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dRef = db.getReference().child("user");
                    String rec_number = getIntent().getStringExtra("number");
                    User user = new User(rec_name, rec_number);
                    dRef.push().setValue(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("Profile Activity", "onSuccess: ");
                                Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Profile Activity", "onFailure: " + e.toString());
                                Toast.makeText(ProfileActivity.this, "Check your internet and try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            }
        });
    }
}