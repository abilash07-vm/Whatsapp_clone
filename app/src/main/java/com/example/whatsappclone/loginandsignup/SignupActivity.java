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

public class SignupActivity extends AppCompatActivity {

    private EditText email, password, rePassword;
    private TextView alreadyHaveAccount, notMatching, invalidemail, privacypolicy;
    private Button btnSignup;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootReference;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initViews();
        privacypolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, BrowserActivity.class);
                intent.putExtra("url", "https://privacy-policy-2k21-211a4.web.app");
                startActivity(intent);
            }
        });
        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsignup();

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
        rePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!password.getText().toString().equals(rePassword.getText().toString())) {
                    notMatching.setVisibility(View.VISIBLE);
                } else {
                    notMatching.setVisibility(View.GONE);
                }
            }
        });
    }

    private boolean checkEmail(String email) {
        if (email.contains("@") && email.contains(".")) {
            if (email.indexOf('@') < email.indexOf('.')) {
                return true;
            }
        }
        return false;
    }

    private void newsignup() {
        String txtemail = email.getText().toString();
        String txtpass = password.getText().toString();
        String txtrepass = rePassword.getText().toString();
        if (txtpass.length() < 8) {
            Toast.makeText(SignupActivity.this, "password is too short", Toast.LENGTH_SHORT).show();
        } else if (txtpass.length() > 25) {
            Toast.makeText(SignupActivity.this, "password is too long.", Toast.LENGTH_SHORT).show();
        } else if (txtemail.equals("") || txtrepass.equals("") || !txtpass.equals(txtrepass) || !checkEmail(txtemail)) {
            Toast.makeText(SignupActivity.this, "Invalid details...", Toast.LENGTH_SHORT).show();
        } else {
            progressBar.show(getSupportFragmentManager(), "signup");
            firebaseAuth.createUserWithEmailAndPassword(txtemail, txtpass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressBar.dismiss();
                                String currentUserid = firebaseAuth.getCurrentUser().getUid();
                                rootReference.child("User").child(currentUserid).setValue("");
                                String userId = firebaseAuth.getUid();
                                String device_token = FirebaseInstanceId.getInstance().getToken();
                                FirebaseDatabase.getInstance().getReference().child("User").child(userId).child("device_token").setValue(device_token).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SignupActivity.this, "Registered sucessfully...", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(SignupActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }

    }

    private void initViews() {
        email = findViewById(R.id.signupEmail);
        password = findViewById(R.id.signupPassword);
        rePassword = findViewById(R.id.signupRePassword);
        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);
        btnSignup = findViewById(R.id.btnSignup);
        notMatching = findViewById(R.id.notMatching);
        invalidemail = findViewById(R.id.invalidemail);
        firebaseAuth = FirebaseAuth.getInstance();
        rootReference = FirebaseDatabase.getInstance().getReference();
        progressBar = new ProgressBar();
        privacypolicy = findViewById(R.id.privacypolicy);
    }
}