package com.tsproject.enchat.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tsproject.enchat.R;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    TextView  tvNumber;
    CardView cvUserName, cvAbout, cvNumber;
    ImageButton ibEditName, ibEditAbout;
    EmojiTextView  tvAbout, tvUserName;
    FirebaseDatabase db;
    DatabaseReference dRef;
    FirebaseUser currentUser;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        cvUserName = findViewById(R.id.cvUserName);
        cvAbout = findViewById(R.id.cvAbout);
        cvNumber = findViewById(R.id.cvNumber);

        ibEditName = findViewById(R.id.ibEditName);
        ibEditAbout = findViewById(R.id.ibEditAbout);

        tvUserName = findViewById(R.id.tvUserName);
        tvAbout = findViewById(R.id.tvAbout);
        tvNumber = findViewById(R.id.tvNumber);

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

        cvUserName.setOnClickListener(this);
        ibEditName.setOnClickListener(this);

        cvAbout.setOnClickListener(this);
        ibEditAbout.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cvUserName:
                showDialogEditName();
                break;
            case R.id.ibEditName:
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
        EmojiPopup popup = EmojiPopup.Builder.fromRootView(findViewById(R.id.mainLayout)).build(etEditedName);
        ivNameEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.toggle();
                if(popup.isShowing())
                {
                    
                }
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