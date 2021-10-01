package com.tsproject.enchat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.tsproject.enchat.R;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "Login";
    ProgressBar pbLoadLogin;
    private CountryCodePicker ccp;
    private EditText etCountryCode, etPhnNum;
    private FloatingActionButton btnNext;
    private FirebaseAuth fbAuth;
    private String number, countryCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verCallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);
        userAlreadyLoggedIn();
        fbAuth = FirebaseAuth.getInstance();
        fbAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);
        ccp = findViewById(R.id.countryCodePicker);
        etCountryCode = findViewById(R.id.etCountryCode);
        etPhnNum = findViewById(R.id.etPhnNum);
        btnNext = findViewById(R.id.fabBtnNext);
        pbLoadLogin = findViewById(R.id.pbLogin);
        btnNext.setOnClickListener(this);
        etCountryCode.setText("+"+ccp.getDefaultCountryCode());
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                etCountryCode.setText("+"+ccp.getSelectedCountryCode());
            }
        });
        verCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                Log.d(TAG, "onVerificationCompleted: "+phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                pbLoadLogin.setVisibility(View.GONE);
                Log.d(TAG, "onVerificationFailed: " + e.toString());
                Log.d(TAG, "onVerificationFailed: " +e.getLocalizedMessage());
             //   Toast.makeText(LoginActivity.this, "Invalid number", Toast.LENGTH_SHORT).show();
                Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationID, forceResendingToken);
                pbLoadLogin.setVisibility(View.GONE);
                Intent intent = new Intent(LoginActivity.this, OTPActivity.class);
                intent.putExtra("number", number);
                intent.putExtra("verification",verificationID);
                intent.putExtra("token",forceResendingToken);
                startActivity(intent);
                finish();
            }
        };
    }


    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.fabBtnNext:
                    checkNumber();
                break;
            default:
                break;
        }
    }

    private void checkNumber(){
        number = etPhnNum.getText().toString().trim();
        countryCode = etCountryCode.getText().toString().trim();
        number = countryCode + number;
        PhnNumVerification();
    }


    private void PhnNumVerification() {

        pbLoadLogin.setVisibility(View.VISIBLE);
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(fbAuth)
               // .setPhoneNumber("+88"+ " " + rec_phnNum)//
                .setPhoneNumber(number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(LoginActivity.this)
                .setCallbacks(verCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        Log.d(TAG, "PhnNumVerification: "+number);

    }
    private void userAlreadyLoggedIn() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null)
        {
            Log.d(TAG, "userLogin: "+currentUser.getPhoneNumber());
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

}



