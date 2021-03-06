package com.example.whatsappclone.loginandsignup;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whatsappclone.AlertDialog.ProgressBar;
import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.R;
import com.example.whatsappclone.settings.BrowserActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button btnlogin, btnphone;
    private TextView txtFrogetPass, txtSignUp, invalidemail, privacypolicy;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
        privacypolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, BrowserActivity.class);
                intent.putExtra("url", "https://privacy-policy-2k21-211a4.web.app");
                startActivity(intent);
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!checkEmail(email.getText().toString())) {
                    invalidemail.setVisibility(View.VISIBLE);
                } else {
                    invalidemail.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        btnlogin.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                btnphone.setVisibility(View.VISIBLE);
                return true;
            }
        });
        btnphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, PhoneVerficationActivity.class);
                startActivity(intent);
            }
        });
        txtFrogetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login() {
        String txtemail = email.getText().toString();
        String txtpass = password.getText().toString();
        if (txtemail.equals("") || txtpass.equals("") || !checkEmail(txtemail)) {
            Toast.makeText(LoginActivity.this, "Invalid details...", Toast.LENGTH_SHORT).show();
        } else {
            progressBar.show(getSupportFragmentManager(), "login");
            firebaseAuth.signInWithEmailAndPassword(txtemail, txtpass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.dismiss();
                            if (task.isSuccessful()) {
                                String userId = firebaseAuth.getUid();
                                String device_token = FirebaseInstanceId.getInstance().getToken();
                                userRef.child(userId).child("device_token").setValue(device_token).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(LoginActivity.this, "logged in sucessfully...", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }

    }

    private boolean checkEmail(String email) {
        if (email.contains("@") && email.contains(".")) {
            if (email.indexOf('@') < email.indexOf('.')) {
                return true;
            }
        }
        return false;
    }

    private void initViews() {
        email = findViewById(R.id.loginemail);
        password = findViewById(R.id.loginpassword);
        btnlogin = findViewById(R.id.btnLogin);
        btnphone = findViewById(R.id.btnLoginUsingPhone);
        txtFrogetPass = findViewById(R.id.forgotPassword);
        txtSignUp = findViewById(R.id.signUpNewAccount);
        invalidemail = findViewById(R.id.invalidemail);
        firebaseAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("User");
        progressBar = new ProgressBar();
        privacypolicy = findViewById(R.id.privacypolicy);
    }

}