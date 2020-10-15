package com.example.whatsappclone.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.whatsappclone.settings.FindFriendsActivity.profile_key;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private DatabaseReference userRef,chatRef,contactRef;
    private CircleImageView profileImage;
    private TextView profileName,profileStatus;
    private Button btnRequest,btnCancel;
    private String reciever,sender,currStatus="new";
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        Intent intent=getIntent();
        if(intent!=null) {
            String user =intent.getStringExtra(profile_key);
            if(user !=null) {
                userRef = userRef.child(user);
                reciever =userRef.getKey();
                retrieveAllDetails();
                btnRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(btnRequest.getText().equals("Request")) {
                            sendRequest();
                        }else if(btnRequest.getText().equals("Cancel Request")){
                            cancelRequset();
                        }else  if(btnRequest.getText().equals("Accept request")){
                            contactRef.child(sender).child(reciever).child("contact").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    contactRef.child(reciever).child(sender).child("contact").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(ProfileActivity.this, "saved Successfully", Toast.LENGTH_SHORT).show();
                                            btnRequest.setText("Friends");
                                            btnCancel.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            });
                            cancelRequset();
                        }
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelRequset();
                    }
                });
            }
        }
    }

    private void cancelRequset() {
        chatRef.child(sender).child(reciever).child("request_status").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    chatRef.child(reciever).child(sender).child("request_status").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ProfileActivity.this, "Request Cancelled", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        btnCancel.setVisibility(View.GONE);
        if(reciever.equals(sender)) {
            btnRequest.setVisibility(View.GONE);
            return;
        }
        chatRef.child(sender).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(reciever)){
                    Log.d(TAG, "onDataChange: "+snapshot.child(sender).toString());
                    String status=snapshot.child(reciever).child("request_status").getValue().toString();
                    if(status.equals("request_sent")) {
                        btnCancel.setVisibility(View.GONE);
                        btnRequest.setText("Cancel Request");
                    } else if(status.equals("request_received")){
                        currStatus="received";
                        btnRequest.setText("Accept request");
                        btnCancel.setVisibility(View.VISIBLE);
                    }
                }else{
                    contactRef.child(sender).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(reciever)){
                                btnRequest.setText("Friends");
                            }else{
                                btnRequest.setText("Request");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendRequest() {
        chatRef.child(sender).child(reciever).child("request_status").setValue("request_sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    chatRef.child(reciever).child(sender).child("request_status").setValue("request_received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ProfileActivity.this,"Request Sent",Toast.LENGTH_SHORT).show();
                                btnRequest.setText("Cancel Request");
                                currStatus="sent";
                            }
                        }
                    });
                }
            }
        });
    }

    private void retrieveAllDetails() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.hasChild("name")){
                        profileName.setText(snapshot.child("name").getValue().toString());
                    }
                    if(snapshot.hasChild("status")){
                        profileStatus.setText(snapshot.child("status").getValue().toString());
                    }
                    if(snapshot.hasChild("image")){
                        Glide.with(ProfileActivity.this)
                                .asBitmap()
                                .load(snapshot.child("image").getValue().toString())
                                .into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initViews() {
        userRef= FirebaseDatabase.getInstance().getReference().child("User");
        chatRef=FirebaseDatabase.getInstance().getReference().child("ChatRequest");
        contactRef=FirebaseDatabase.getInstance().getReference().child("Contact");
        profileImage=findViewById(R.id.profile_Image);
        profileName=findViewById(R.id.profile_Name);
        profileStatus=findViewById(R.id.profile_Status);
        btnRequest=findViewById(R.id.btnRequest);
        btnCancel=findViewById(R.id.btnCancel);
        auth=FirebaseAuth.getInstance();
        sender=auth.getCurrentUser().getUid();
    }

}