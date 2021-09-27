package com.tsproject.enchat.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.tsproject.enchat.R;


public class OTPActivity extends AppCompatActivity {
    private String verification, username, userNumber, description;
    private FirebaseAuth fbAuth;
    private ProgressBar pbOTP;
    private TextView tvShowNumber;
    private PinView otpPinview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        tvShowNumber = findViewById(R.id.tvOtpTitle);
        otpPinview = findViewById(R.id.pinView);
        pbOTP = findViewById(R.id.pbOTP);

        fbAuth = FirebaseAuth.getInstance();
        tvShowNumber.setText(getIntent().getStringExtra("number"));
        verification = getIntent().getStringExtra("verification");
        userNumber = getIntent().getStringExtra("countryCode") + getIntent().getStringExtra("number");

        otpPinview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String otp = otpPinview.getText().toString().trim();
                if (otp.length() == 6) {
                    pbOTP.setVisibility(View.VISIBLE);
                    if (verification != null) {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verification, otp);
                        fbAuth.signInWithCredential(credential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            userLogin();
                                        } else {
                                            pbOTP.setVisibility(View.GONE);
                                            Toast.makeText(OTPActivity.this, "OTP is not valid", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    private void userLogin() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(OTPActivity.this, ProfileInfoActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("number", userNumber);
            startActivity(intent);
            finish();

        }
    }

}
