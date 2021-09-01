package com.tsproject.enchat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OnOtpCompletionListener;
import com.tsproject.enchat.databinding.OtpBinding;

public class OTPVerification extends AppCompatActivity{
    OtpBinding binding;
    private TextView tvShowNumber;
    private String verification;
    private FirebaseAuth fbAuth;
    private ProgressBar pbOTP;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
       // userLogin();
        tvShowNumber = findViewById(R.id.tvPhnNum);
        pbOTP = findViewById(R.id.pbOTP);
        fbAuth = FirebaseAuth.getInstance();
        tvShowNumber.setText(getIntent().getStringExtra("number"));
        verification = getIntent().getStringExtra("verification");
        binding.otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                if(verification != null)
                {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verification,otp);
                    fbAuth.signInWithCredential(credential)
                           .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                               @Override
                               public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(OTPVerification.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        userLogin();
                                    }
                                    else
                                    {
                                        pbOTP.setVisibility(View.GONE);
                                        Toast.makeText(OTPVerification.this, "OTP is not valid", Toast.LENGTH_SHORT).show();
                                    }
                               }
                           });

                }
            }
        });
    }

    private void userLogin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
        {
            Intent intent = new Intent(OTPVerification.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }
    }


}
