package com.tsproject.enchat.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tsproject.enchat.R;

public class GetStartedActivity extends AppCompatActivity {
    LottieAnimationView lottie;
    Button btnStart;
    TextView title, description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        lottie = findViewById(R.id.animationView);
        btnStart = findViewById(R.id.btnStart);
        title = findViewById(R.id.tvTitle);
        description = findViewById(R.id.tvDescription);
        userAlreadyLoggedIn();
        btnStart.setOnClickListener(view -> {
            lottie.animate().translationX(2000).setDuration(900).setStartDelay(0);
            title.animate().translationY(-500).setDuration(800).setStartDelay(0);
            description.animate().translationY(-500).setDuration(800).setStartDelay(0);
            btnStart.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(GetStartedActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            },950);
        });
    }
    private void userAlreadyLoggedIn() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null)
        {
            Log.d("Get Started", "userLogin: "+currentUser.getPhoneNumber());
            Intent intent = new Intent(GetStartedActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }
}