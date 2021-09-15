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
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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


        userAlreadyLoggedIn();
        fbAuth = FirebaseAuth.getInstance();
        fbAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);
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
                finish();
            }
        };
    }
    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.btnLoginContinue:
                    checkNumber();
                break;
            default:
                break;
        }
    }

    private void checkNumber(){
        number = etPhnNum.getText().toString().trim();
        number = "+88" + number;
        PhnNumVerification();
    }


    private void PhnNumVerification() {

        pbLoadLogin.setVisibility(View.VISIBLE);
        btnLoginContinue.setVisibility(View.GONE);
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(fbAuth)
               // .setPhoneNumber("+88"+ " " + rec_phnNum)//
                .setPhoneNumber(number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(LoginActivity.this)
                .setCallbacks(verCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
    private void userAlreadyLoggedIn() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null)
        {
            Log.d(TAG, "userLogin: "+currentUser.getPhoneNumber());
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

}



