package com.tsproject.enchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileInfoActivity extends AppCompatActivity {
    Button btnFinish;
    EditText etName;
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase db;
    CircleImageView civProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);
        btnFinish = findViewById(R.id.btnProfileNext);
        etName = findViewById(R.id.etName);
        civProfile = findViewById(R.id.ivProfilePhoto);

        db = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        civProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(ProfileInfoActivity.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
                Log.d("Camera", "onClick: permission given");
                Log.d("Camera", "onClick: permisison denied");
            }
        });
        btnFinish.setOnClickListener(new View.OnClickListener() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        Log.d("CAMERA", "onActivityResult: "+uri);
        civProfile.setImageURI(uri);

    }
}