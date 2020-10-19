package com.example.whatsappclone.loginandsignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private EditText email,password,rePassword;
    private TextView alreadyHaveAccount,notMatching,invalidemail;
    private Button btnSignup;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootReference;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initViews();
        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignupActivity.this, LoginActivity.class);
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
                if(!checkEmail(email.getText().toString())){
                    invalidemail.setVisibility(View.VISIBLE);
                }else{
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
                if(!password.getText().toString().equals(rePassword.getText().toString())){
                    notMatching.setVisibility(View.VISIBLE);
                }else{
                    notMatching.setVisibility(View.GONE);
                }
            }
        });
    }

    private boolean checkEmail(String email) {
        if(email.contains("@") && email.contains(".")){
            if(email.indexOf('@')< email.indexOf('.')){
                return true;
            }
        }
        return false;
    }

    private void newsignup() {
        String txtemail=email.getText().toString();
        String txtpass=password.getText().toString();
        String txtrepass=rePassword.getText().toString();
        if(txtpass.length()<8) {
            Toast.makeText(SignupActivity.this,"password is too short",Toast.LENGTH_SHORT).show();
        }else if(txtpass.length()>25){
            Toast.makeText(SignupActivity.this,"password is too long.",Toast.LENGTH_SHORT).show();
        } else if(txtemail.equals("")|| txtrepass.equals("") || !txtpass.equals(txtrepass) || !checkEmail(txtemail) ){
            Toast.makeText(SignupActivity.this,"Invalid details...",Toast.LENGTH_SHORT).show();
        }else{
            loadingBar.setTitle("Sign Up");
            loadingBar.setMessage("Creating account please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.create();
            firebaseAuth.createUserWithEmailAndPassword(txtemail,txtpass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                loadingBar.dismiss();
                                String currentUserid=firebaseAuth.getCurrentUser().getUid();
                                rootReference.child("User").child(currentUserid).setValue("");
                                Toast.makeText(SignupActivity.this,"Registered sucessfully...",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(SignupActivity.this, MainActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(SignupActivity.this,task.getException().toString(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }

    }

    private void initViews() {
        email=findViewById(R.id.signupEmail);
        password=findViewById(R.id.signupPassword);
        rePassword=findViewById(R.id.signupRePassword);
        alreadyHaveAccount=findViewById(R.id.alreadyHaveAccount);
        btnSignup=findViewById(R.id.btnSignup);
        notMatching=findViewById(R.id.notMatching);
        invalidemail=findViewById(R.id.invalidemail);
        firebaseAuth=FirebaseAuth.getInstance();
        rootReference = FirebaseDatabase.getInstance().getReference();
        loadingBar=new ProgressDialog(this);
    }
}