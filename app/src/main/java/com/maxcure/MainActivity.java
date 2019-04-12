package com.maxcure;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;


import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    String mVerificationId;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    PhoneAuthProvider.ForceResendingToken mResendToken;

    EditText etPhone, etOtp;
    Button btSendOtp, btResendOtp, btVerifyOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFields();
        mAuth = FirebaseAuth.getInstance();
        initFireBaseCallbacks();

    }

    void initFields() {
        etPhone = findViewById(R.id.et_phone);
        etOtp = findViewById(R.id.et_otp);
        btSendOtp = findViewById(R.id.bt_send_otp);
        btResendOtp = findViewById(R.id.bt_resend_otp);
        btVerifyOtp = findViewById(R.id.bt_verify_otp);
        btResendOtp.setOnClickListener(this);
        btVerifyOtp.setOnClickListener(this);
        btSendOtp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_send_otp:
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        etPhone.getText().toString(),        // Phone number to verify
                        1,                 // Timeout duration
                        TimeUnit.MINUTES,   // Unit of timeout
                        this,               // Activity (for callback binding)
                        mCallbacks);        // OnVerificationStateChangedCallbacks
                break;
            case R.id.bt_resend_otp:
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        etPhone.getText().toString(),        // Phone number to verify
                        1  ,               // Timeout duration
                        TimeUnit.MINUTES,   // Unit of timeout
                        this,               // Activity (for callback binding)
                        mCallbacks,         // OnVerificationStateChangedCallbacks
                        mResendToken);             // Force Resending Token from callbacks
                break;
            case R.id.bt_verify_otp:
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, etOtp.getText().toString());

                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Verification Success", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        Toast.makeText(MainActivity.this, "Verification Failed, Invalid credentials", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                break;
            case R.id.bt_sign_out:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "Signing out ", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    void initFireBaseCallbacks() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Toast.makeText(MainActivity.this, "Verification Complete", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(MainActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Toast.makeText(MainActivity.this, "Code Sent", Toast.LENGTH_SHORT).show();
                mVerificationId = verificationId;
                mResendToken = token; //Add this line to save the resend token
            }
        };
    }
}

