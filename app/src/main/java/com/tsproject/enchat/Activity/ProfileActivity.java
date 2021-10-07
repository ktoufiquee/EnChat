package com.tsproject.enchat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tsproject.enchat.R;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.EmojiUtils;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import java.net.URL;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    CardView cvUserName, cvAbout, cvNumber;
    ImageButton ibEditName, ibEditAbout;
    TextView tvAbout, tvUserName, tvNumber;
    FirebaseDatabase db;
    FirebaseStorage storage;
    StorageReference sRef;
    DatabaseReference userRef;
    FirebaseUser currentUser;
    CircleImageView civProfile;
    String uID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        cvUserName = findViewById(R.id.cvUserName);
        cvAbout = findViewById(R.id.cvAbout);
        cvNumber = findViewById(R.id.cvNumber);

        civProfile = findViewById(R.id.civProfile);

        ibEditName = findViewById(R.id.ibEditName);
        ibEditAbout = findViewById(R.id.ibEditAbout);

        tvUserName = findViewById(R.id.tvUserName);
        tvAbout = findViewById(R.id.tvAbout);
        tvNumber = findViewById(R.id.tvNumber);

        cvUserName.setOnClickListener(this);
        ibEditName.setOnClickListener(this);

        cvAbout.setOnClickListener(this);
        ibEditAbout.setOnClickListener(this);

        civProfile.setOnClickListener(this);

        storage = FirebaseStorage.getInstance();
        uID = FirebaseAuth.getInstance().getUid();
        db = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase.getInstance()
                .getReference()
                .child("user")
                .child(uID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String name = "";
                            String number = "";
                            String about = "";
                            if (snapshot.child("imageURL").getValue() != null) {
                                Glide.with(getApplicationContext())
                                        .load(snapshot.child("imageURL").getValue().toString())
                                        .into(civProfile);
                            }
                            if (snapshot.child("userName").getValue() != null) {
                                name = snapshot.child("userName").getValue().toString();
                                tvUserName.setText(name);
                            }
                            if (snapshot.child("about").getValue() != null) {
                                about = snapshot.child("about").getValue().toString();
                                tvAbout.setText(about);
                            }
                            if (snapshot.child("phnNum").getValue() != null) {
                                number = snapshot.child("phnNum").getValue().toString();
                                tvNumber.setText(number);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cvUserName:
                showDialogEditName();
                break;
            case R.id.ibEditName:
                TypedValue outValue = new TypedValue();
                showDialogEditName();
                break;
            case R.id.cvAbout:
                showDialogEditAbout();
                break;
            case R.id.ibEditAbout:
                showDialogEditAbout();
                break;
            case R.id.civProfile:
                profilePicChangeOnClick();
            default:
                break;
        }
    }

    private void profilePicChangeOnClick() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/");
        startActivityForResult(intent, 25);
    }



    private void showDialogEditName() {
        AlertDialog.Builder alert = new AlertDialog.Builder(ProfileActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_name, null);
        Button btnOk = (Button) view.findViewById(R.id.btnOk);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        EditText etEditedName = (EditText) view.findViewById(R.id.etEditedName);
        String currentName = tvUserName.getText().toString().trim();
        etEditedName.setText(currentName);
        etEditedName.setSelection(etEditedName.getText().length());
        alert.setView(view);
        alert.setCancelable(true);
        AlertDialog alertDialog = alert.create();
        btnOk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String editedName = etEditedName.getText().toString().trim();
                if (!editedName.equals("")) {
                    tvUserName.setText(editedName);
                    alertDialog.dismiss();
                    if (!editedName.equals(currentName)) {
                        FirebaseDatabase.getInstance().getReference().child("user").child(uID).child("userName").setValue(editedName);
                        Toast.makeText(ProfileActivity.this, "Username updated", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Username can't be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

            }
        });
        alertDialog.show();
    }

    private void showDialogEditAbout() {
        AlertDialog.Builder alert = new AlertDialog.Builder(ProfileActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_about, null, false);
        Button btnOk = (Button) view.findViewById(R.id.btnOk);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        EditText etEditedAbout = (EditText) view.findViewById(R.id.etEditedAbout);
        String currentAbout = tvAbout.getText().toString().trim();
        etEditedAbout.setText(currentAbout);
        etEditedAbout.setSelection(etEditedAbout.getText().length());
        alert.setView(view);
        AlertDialog alertDialog = alert.create();
        alert.setCancelable(true);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editedAbout = etEditedAbout.getText().toString().trim();
                if (!editedAbout.equals("")) {
                    tvAbout.setText(editedAbout);
                    alertDialog.dismiss();
                    if (!editedAbout.equals(currentAbout)) {

                        FirebaseDatabase.getInstance().getReference().child("user").child(uID).child("about").setValue(editedAbout);
                        Toast.makeText(ProfileActivity.this, "About updated", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "About can't be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }
}