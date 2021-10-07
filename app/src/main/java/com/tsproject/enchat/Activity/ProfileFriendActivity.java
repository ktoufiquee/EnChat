package com.tsproject.enchat.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.palette.graphics.Palette;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tsproject.enchat.R;

import java.util.Objects;

public class ProfileFriendActivity extends AppCompatActivity {

    private LinearLayout llMedia, llBlock;
    private CollapsingToolbarLayout collapseLayout;
    private TextView tvInfoNumber, tvInfoAbout, tvInfoNickname, tvSaveMedia;
    private Switch switchMute;
    private ImageView ivBlock, ivMsg, ivShowFriendImage;
    private Toolbar tbProfile;
    String fID, friendName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_friend);

        llMedia = findViewById(R.id.llMedia);


        collapseLayout = findViewById(R.id.collapseLayout);

        tvInfoNumber = findViewById(R.id.tvInfoNumber);
        tvInfoAbout = findViewById(R.id.tvInfoAbout);


        switchMute = findViewById(R.id.switchMute);

        ivMsg = findViewById(R.id.ivMsgSend);
        ivShowFriendImage = findViewById(R.id.ivShowFriendImage);

        tbProfile = findViewById(R.id.tbProfile);
        tbProfile.setTitle(friendName);

        fID = getIntent().getExtras().getString("friendID");
        friendName = getIntent().getExtras().getString("friendName");
        tbProfile.setTitle(friendName);
        tbProfile.setTitleTextColor(getResources().getColor(R.color.white));
        FirebaseDatabase.getInstance().getReference().child("user")
                .child(fID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("imageURL").getValue().toString().trim().equals("No Image")) {
                        ivShowFriendImage.setImageResource(R.drawable.placeholder);
                    } else {
                        Glide.with(getApplicationContext()).load(snapshot.child("imageURL").getValue().toString())
                                .placeholder(R.drawable.placeholder)
                                .into(ivShowFriendImage);
                    }
                    if(snapshot.child("about").getValue() != null)
                    {
                        tvInfoAbout.setText(snapshot.child("about").getValue().toString().trim());
                    }
                    if(snapshot.child("phnNum").getValue() != null)
                    {
                        tvInfoNumber.setText(snapshot.child("phnNum").getValue().toString().trim());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        setSupportActionBar(tbProfile);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        collapseLayout.setContentScrimColor(getResources().getColor(R.color.toolbarColour));


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


}