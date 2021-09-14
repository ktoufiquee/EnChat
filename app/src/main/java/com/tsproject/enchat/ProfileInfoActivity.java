package com.tsproject.enchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileInfoActivity extends AppCompatActivity {
    Button btnNext;
    EditText etName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);
        btnNext = findViewById(R.id.btnProfileNext);
        etName = findViewById(R.id.etName);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rec_name = etName.getText().toString().trim();
                if (rec_name.equals("")) {
                    etName.setError("Enter your name");
                } else {
                    String rec_number = getIntent().getStringExtra("number");
                    User user = new User(rec_name, rec_number);
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dRef = db.getReference().child("user").child(currentUser.getUid());
                    dRef.setValue(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ProfileInfoActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ProfileInfoActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                          .addOnFailureListener(new OnFailureListener() {
                              @Override
                              public void onFailure(@NonNull Exception e) {
                                  Toast.makeText(ProfileInfoActivity.this, "Check your internet!", Toast.LENGTH_SHORT).show();
                              }
                          });

                }
            }
        });
    }
}