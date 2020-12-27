package com.example.whatsappclone.loginandsignup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whatsappclone.AlertDialog.ProgressBar;
import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.TimeUnit;

public class PhoneVerficationActivity extends AppCompatActivity {
    private static final String TAG = "PhoneVerficationActivity";
    private EditText txtPhoneNum, verificationCode;
    private Button btnVerify, btnSubmit;
    private String phoneNumber;
    private String verifationId;
    private PhoneAuthProvider.ForceResendingToken token;
    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verfication);

        initViews();
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = txtPhoneNum.getText().toString();
                if (phoneNumber.equals("") || phoneNumber.length() < 10) {
                    Toast.makeText(PhoneVerficationActivity.this, "Invalid PhoneNumber", Toast.LENGTH_SHORT).show();

                } else {
                    progressBar.show(getSupportFragmentManager(), "verification Code sent");
                    sendVerifationCode();
                }

            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSubmit.setEnabled(false);
                String typedcode = verificationCode.getText().toString();
                if (typedcode.equals("")) {
                    Toast.makeText(PhoneVerficationActivity.this, "Please enter the code to verify...", Toast.LENGTH_SHORT).show();

                } else {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifationId, typedcode);
                    signInWithPhoneNum(credential);
                }
                btnSubmit.setEnabled(true);

            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneNum(phoneAuthCredential);
            }


            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressBar.dismiss();
                Toast.makeText(PhoneVerficationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                progressBar.dismiss();
                verifationId = s;
                token = forceResendingToken;
                Toast.makeText(PhoneVerficationActivity.this, "Code Sent...", Toast.LENGTH_SHORT).show();
                btnSubmit.setVisibility(View.VISIBLE);
                verificationCode.setVisibility(View.VISIBLE);
            }
        };
    }


    private void sendVerifationCode() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                PhoneVerficationActivity.this,
                callbacks
        );


    }

    private void signInWithPhoneNum(PhoneAuthCredential phoneAuthCredential) {
        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String userId = firebaseAuth.getUid();
                    String device_token = FirebaseInstanceId.getInstance().getToken();
                    FirebaseDatabase.getInstance().getReference().child("User").child(userId).child("device_token").setValue(device_token).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PhoneVerficationActivity.this, "Account Created Sucessfully..", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(PhoneVerficationActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }
                    });

                } else {
                    Toast.makeText(PhoneVerficationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    progressBar.dismiss();
                }
            }
        });
    }

    private void initViews() {
        progressBar = new ProgressBar();
        txtPhoneNum = findViewById(R.id.txtPhoneNumber);
        verificationCode = findViewById(R.id.txtVerficationCode);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnVerify = findViewById(R.id.btnVerify);
        firebaseAuth = FirebaseAuth.getInstance();
        btnSubmit.setVisibility(View.GONE);
        verificationCode.setVisibility(View.GONE);
        btnVerify.setVisibility(View.VISIBLE);
        txtPhoneNum.setVisibility(View.VISIBLE);

    }
}