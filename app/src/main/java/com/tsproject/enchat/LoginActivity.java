package com.tsproject.enchat;

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
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "Login";
    ProgressBar pbLoadLogin;
    private EditText etPhnNum;
    private Button btnLoginContinue;
    private FirebaseAuth fbAuth;
    private String number;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verCallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);
        userLogin();
        fbAuth = FirebaseAuth.getInstance();
        etPhnNum = findViewById(R.id.etPhnNum);
        btnLoginContinue = findViewById(R.id.btnLoginContinue);
        pbLoadLogin = findViewById(R.id.pbLogin);
        btnLoginContinue.setOnClickListener(this);
        verCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: "+phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                pbLoadLogin.setVisibility(View.GONE);
                btnLoginContinue.setVisibility(View.VISIBLE);
                Log.d(TAG, "onVerificationFailed: " + e.toString());
                Toast.makeText(LoginActivity.this, "Verification failed due to "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationID, forceResendingToken);
                pbLoadLogin.setVisibility(View.GONE);
                btnLoginContinue.setVisibility(View.VISIBLE);
                Intent intent = new Intent(LoginActivity.this,OTPActivity.class);
                intent.putExtra("number", number);
                intent.putExtra("verification",verificationID);
                startActivity(intent);
            }
        };
    }
    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.btnLoginContinue:
                PhnNumVerification();
                break;
            default:
                break;
        }
    }
    private void PhnNumVerification() {
        number = etPhnNum.getText().toString().trim();
        pbLoadLogin.setVisibility(View.VISIBLE);
        btnLoginContinue.setVisibility(View.GONE);
        String rec_phnNum = etPhnNum.getText().toString().trim();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(fbAuth)
                .setPhoneNumber("+88" + rec_phnNum)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(LoginActivity.this)
                .setCallbacks(verCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
    private void userLogin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
        {
            Log.d(TAG, "userLogin: "+user.getPhoneNumber());
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }


  /*  private void signInWithPhnCredential(PhoneAuthCredential phoneAuthCredential) {
            fbAuth.signInWithCredential(phoneAuthCredential)
                  .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {
                          if(task.isSuccessful())
                          {
                              Log.d(TAG, "onComplete: signInWithPhnCredential");
                              userLogin();
                          }
                          else
                          {
                              Log.d(TAG, "signInPhnCredential failure");
                              Toast.makeText(Login.this, "Invalid verification code", Toast.LENGTH_SHORT).show();
                          }
                      }
                  });

    }


    private void userLogin() {
        FirebaseUser user = fbAuth.getInstance().getCurrentUser();
        if(user != null)
        {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }
    }



*/
}



