package com.tsproject.enchat;

import android.content.Intent;
import android.os.Bundle;
import android.text.NoCopySpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.tsproject.enchat.databinding.LoginBinding;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity implements View.OnClickListener {
    LoginBinding binding;
    public static final String TAG = "Login";
    ProgressBar pbLoadLogin;
    private EditText etPhnNum;
    private Button btnLoginContinue;
    private FirebaseAuth fbAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verCallbacks;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.login);
        binding = LoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseApp.initializeApp(this);
       // userLogin();
        fbAuth = FirebaseAuth.getInstance();
        etPhnNum = findViewById(R.id.etPhnNum);
        btnLoginContinue = findViewById(R.id.btnLoginContinue);
        pbLoadLogin = findViewById(R.id.pbLogin);
        btnLoginContinue.setOnClickListener(this);
        String number = etPhnNum.getText().toString().trim();
        verCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: "+phoneAuthCredential);
              //  signInWithPhnCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
               // pbLoadLogin.setVisibility(View.GONE);
               // btnLoginContinue.setVisibility(View.VISIBLE);
                Log.d(TAG, "onVerificationFailed: " + e.toString());
                Toast.makeText(Login.this, "Verification failed due to "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationID, forceResendingToken);
            //    pbLoadLogin.setVisibility(View.GONE);
             //   btnLoginContinue.setVisibility(View.VISIBLE);
                Intent intent = new Intent(Login.this,OTPVerification.class);
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
       // pbLoadLogin.setVisibility(View.VISIBLE);
      //  btnLoginContinue.setVisibility(View.GONE);
        String rec_text = etPhnNum.getText().toString().trim();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(fbAuth)
                .setPhoneNumber("+88" + rec_text)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(verCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
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

