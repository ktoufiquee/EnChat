package com.tsproject.enchat.Activity;

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

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.tsproject.enchat.R;

import java.util.Objects;

public class ProfileFriendActivity extends AppCompatActivity {

    private LinearLayout llMedia, llBlock;
    private CollapsingToolbarLayout collapseLayout;
    private TextView tvInfoNumber, tvInfoAbout, tvInfoNickname, tvSaveMedia;
    private Switch switchMute;
    private ImageView ivBlock, ivMsg, ivShowFriendImage;
    private Toolbar tbProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_friend);

        llMedia = findViewById(R.id.llMedia);
        llBlock = findViewById(R.id.llBlock);

        collapseLayout = findViewById(R.id.collapseLayout);

        tvInfoNumber = findViewById(R.id.tvInfoNumber);
        tvInfoAbout = findViewById(R.id.tvInfoAbout);
        tvInfoNickname = findViewById(R.id.tvInfoNickname);
        tvSaveMedia = findViewById(R.id.tvSaveMedia);

        switchMute = findViewById(R.id.switchMute);

        ivBlock = findViewById(R.id.ivBlock);
        ivMsg = findViewById(R.id.iv_image);
        ivShowFriendImage = findViewById(R.id.ivShowFriendImage);

        tbProfile = findViewById(R.id.tbProfile);

        setSupportActionBar(tbProfile);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //   Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.avatar);
        Bitmap bitmap = ((BitmapDrawable) ivShowFriendImage.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@Nullable Palette palette) {
                if (palette != null) {
                    int primary = getResources().getColor(R.color.toolbarColour);
                    collapseLayout.setContentScrimColor(palette.getMutedColor(primary));
                }
            }

        });

    }


}