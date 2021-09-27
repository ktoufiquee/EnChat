package com.tsproject.enchat.Activity;

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
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tsproject.enchat.R;
import com.tsproject.enchat.Model.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileInfoActivity extends AppCompatActivity {
    FloatingActionButton fabBtnNext, fabBtnAddPhoto;
    EditText etName;
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase db;
    Uri selectedImage;
    FirebaseUser currentUser;
    DatabaseReference dRef;
    String imageURL;
    ProgressBar pbProfileInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);
        fabBtnNext = findViewById(R.id.fabBtnNext);
        fabBtnAddPhoto = findViewById(R.id.fabBtnAddPhoto);
        etName = findViewById(R.id.etUserName);
        pbProfileInfo = findViewById(R.id.pbProfileInfo);

        db = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        dRef = db.getReference().child("user").child(currentUser.getUid());

        fabBtnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            }
        });
        fabBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String rec_name = etName.getText().toString().trim();
                if (rec_name.equals("")) {
                    etName.setError("Enter your name");
                } else {
                    if (selectedImage != null) {
                        StorageReference reference = storage.getReference().child("Profile").child(auth.getUid());
                        reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                Log.d("Image url", "onClick: " );
                                if (task.isSuccessful()) {
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Toast.makeText(ProfileInfoActivity.this , "Image added", Toast.LENGTH_SHORT).show();
                                            imageURL = uri.toString();
                                            boolean isUploaded = dRef.child("imageURL").setValue(imageURL).isSuccessful();
                                            Log.d("Image url", "onClick inside Upload: " + !isUploaded + " " +imageURL);
                                        }
                                    });
                                }
                            }
                        });
                    }
                    String uID = auth.getUid();
                    String rec_number = getIntent().getStringExtra("number");
                    User user = new User();
                    user.setUserName(rec_name);
                    user.setPhnNum(rec_number);
                    user.setuID(uID);
                    if(imageURL == null) {
                        imageURL = "No Image";
                    }
                    user.setImageURL(imageURL);
                    Log.d("TEST IMAGE", imageURL);
                    dRef.setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    pbProfileInfo.setVisibility(View.VISIBLE);
                                    Log.d("Profile", "onSuccess: Image Uploaded Successfully");
                                    Intent intent = new Intent(ProfileInfoActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pbProfileInfo.setVisibility(View.GONE);
                                    Log.d("Profile", "onFailure: "+e.toString() + "Localized = " + e.toString());
                                }
                            });
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ImageURL", "onActivityResult: NULL" );
        if (data != null) {
            if (data.getData() != null) {
                selectedImage = data.getData();
                Log.d("ImageURL", "onActivityResult: " + selectedImage);
            }
        }
    }
}