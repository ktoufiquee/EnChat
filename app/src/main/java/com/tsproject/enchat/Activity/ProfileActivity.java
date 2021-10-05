package com.tsproject.enchat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

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

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    TextView  tvNumber;
    CardView cvUserName, cvAbout, cvNumber;
    ImageButton ibEditName, ibEditAbout;
    EmojiTextView  tvAbout, tvUserName;
    FirebaseDatabase db;
    FirebaseStorage storage;
    StorageReference sRef;
    DatabaseReference userRef;
    FirebaseUser currentUser;
    CircleImageView civProfile;
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

        db = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        userRef = db.getReference().child("user").child(currentUser.getUid());
        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String name = "";
                    String number = "";
                    String about = "";
                    for(DataSnapshot childSnapshot: snapshot.getChildren())
                    {
                        if(childSnapshot.child("imageUrl").getValue() != null)
                        {
                            Log.d("ProfileCheck", "onDataChange: Not empty");
                            Glide.with(ProfileActivity.this)
                                        .load(childSnapshot.child("imageUrl").getValue().toString())
                                        .into(civProfile);

                        }
                        if(childSnapshot.child("userName").getValue() != null)
                        {
                            name = childSnapshot.child("userName").getValue().toString();
                            tvUserName.setText(name);
                        }
                        if(childSnapshot.child("about").getValue() != null)
                        {
                            about = childSnapshot.child("about").getValue().toString();
                            tvAbout.setText(about);
                        }
                        if(childSnapshot.child("phnNum").getValue() != null)
                        {
                            number = childSnapshot.child("phnNum").getValue().toString();
                            tvNumber.setText(number);
                        }

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
            default:
                break;
        }
    }

    private void showDialogEditName() {
        AlertDialog.Builder alert = new AlertDialog.Builder(ProfileActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_name, null);
        Button btnOk = (Button) view.findViewById(R.id.btnOk);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        ImageView ivNameEmoji = (ImageView) view.findViewById(R.id.ivNameEmoji);
        EmojiEditText  etEditedName = (EmojiEditText) view.findViewById(R.id.etEditedName);
        String currentName = tvUserName.getText().toString().trim();
        etEditedName.setText(currentName);
        etEditedName.setSelection(etEditedName.getText().length());
        alert.setView(view);
        alert.setCancelable(true);
        AlertDialog alertDialog = alert.create();
        EmojiPopup popup = EmojiPopup.Builder.fromRootView(view.findViewById(R.id.mainLayout)).build(etEditedName);
        ivNameEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.toggle();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editedName = etEditedName.getText().toString().trim();
                if (!editedName.equals("")) {
                    tvUserName.setText(editedName);
                    alertDialog.dismiss();
                    if (!editedName.equals(currentName)) {
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


}