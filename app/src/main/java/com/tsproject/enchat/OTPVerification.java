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
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OTPVerification extends AppCompatActivity implements  View.OnClickListener{

    private TextView tvShowNumber;
    private String verification;
    private Button btnVerify;
    private EditText etOTP1,etOTP2,etOTP3,etOTP4,etOTP5,etOTP6;
    private FirebaseAuth fbAuth;
    private ProgressBar pbOTP;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp);
        editTextInput();
        tvShowNumber = findViewById(R.id.tvPhnNum);
        btnVerify = findViewById(R.id.btnVerify);
        etOTP1 = findViewById(R.id.etOTP1);
        etOTP2 = findViewById(R.id.etOTP2);
        etOTP3 = findViewById(R.id.etOTP3);
        etOTP4 = findViewById(R.id.etOTP4);
        etOTP5 = findViewById(R.id.etOTP5);
        etOTP6 = findViewById(R.id.etOTP6);
        pbOTP = findViewById(R.id.pbOTP);
        fbAuth = FirebaseAuth.getInstance();
        tvShowNumber.setText(String.format(
                "+88-%s", getIntent().getStringExtra("number")));
        verification = getIntent().getStringExtra("verification");
        btnVerify.setOnClickListener(this);
    }

    private void editTextInput() {
            etOTP1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                            etOTP2.requestFocus();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            }); etOTP2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                            etOTP3.requestFocus();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        etOTP3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etOTP4.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etOTP4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etOTP5.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etOTP5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etOTP6.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
           case  R.id.btnVerify:
               verifyOTP();
               break;
        }
    }

    private void verifyOTP() {
        pbOTP.setVisibility(View.VISIBLE);
        btnVerify.setVisibility(View.GONE);
        String rec_etOTP1 = etOTP1.getText().toString().trim();
        String rec_etOTP2 = etOTP2.getText().toString().trim();
        String rec_etOTP3 = etOTP3.getText().toString().trim();
        String rec_etOTP4 = etOTP4.getText().toString().trim();
        String rec_etOTP5 = etOTP5.getText().toString().trim();
        String rec_etOTP6 = etOTP6.getText().toString().trim();
        if(rec_etOTP1.isEmpty() || rec_etOTP2.isEmpty() || rec_etOTP3.isEmpty() || rec_etOTP4.isEmpty()
                                ||rec_etOTP5.isEmpty() || rec_etOTP6.isEmpty())
        {
            Toast.makeText(OTPVerification.this, "OTP is not valid", Toast.LENGTH_SHORT).show();
            pbOTP.setVisibility(View.GONE);
            btnVerify.setVisibility(View.VISIBLE);
        }
        else
        {
             if(verification != null)
             {
                 String code = rec_etOTP1 + rec_etOTP2 + rec_etOTP3 + rec_etOTP4 + rec_etOTP5 + rec_etOTP6;

                 PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verification,code);
                 fbAuth.signInWithCredential(credential)
                       .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               if(task.isSuccessful())
                               {
                                   Toast.makeText(OTPVerification.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                   Intent intent = new Intent(OTPVerification.this, MainActivity.class);
                                   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                   startActivity(intent);
                               }
                               else
                               {
                                   pbOTP.setVisibility(View.GONE);
                                   btnVerify.setVisibility(View.VISIBLE);
                                   Toast.makeText(OTPVerification.this, "OTP is not valid", Toast.LENGTH_SHORT).show();
                               }
                           }
                       });
             }
        }
    }
}
