package com.example.whatsappclone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.Model.Contact;
import com.example.whatsappclone.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.whatsappclone.MainActivity.currentState;
import static com.example.whatsappclone.settings.FindFriendsActivity.profile_key;

public class RequestActivity extends AppCompatActivity {
    private static final String TAG = "RequestFragment";
    private RecyclerView reqRecyView;
    private DatabaseReference chatRef, userRef, contactRef, rootRef;
    private String sender, receiver;
    private boolean isAccept = false;
    private ImageView btnBack;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        initViews();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (sender != null) {
            currentState("online");
            FirebaseRecyclerOptions<Contact> options = new FirebaseRecyclerOptions.Builder<Contact>()
                    .setQuery(chatRef.child(sender), Contact.class)
                    .build();
            FirebaseRecyclerAdapter<Contact, reqViewHolder> adapter = new FirebaseRecyclerAdapter<Contact, reqViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final reqViewHolder holder, int position, @NonNull final Contact model) {
                    receiver = getRef(position).getKey();
                    userRef.child(receiver).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                holder.name.setText(snapshot.child("name").getValue().toString());
                                holder.status.setText(snapshot.child("status").getValue().toString());
                                if (snapshot.child("image").getValue() != null)
                                    Glide.with(getApplicationContext())
                                            .asBitmap()
                                            .load(snapshot.child("image").getValue().toString())
                                            .into(holder.image);
                                chatRef.child(sender).child(receiver).child("request_status").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            if (snapshot.getValue().equals("request_sent")) {
                                                holder.btnAccept.setVisibility(View.GONE);
                                                holder.btnReject.setText("Cancel");
                                            } else {
                                                holder.btnAccept.setVisibility(View.VISIBLE);
                                                holder.btnReject.setText("Reject");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        contactRef.child(sender).child(receiver).child("contact").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    contactRef.child(receiver).child(sender).child("contact").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            isAccept = true;
                                                            cancelRequset();
                                                            final String receiverRef = receiver + "/" + sender, senderRef = sender + "/" + receiver;
                                                            final Map messageBody = new HashMap();
                                                            messageBody.put("from", receiver);
                                                            messageBody.put("msgcount", 0);
                                                            messageBody.put("timestamp", new Timestamp(System.currentTimeMillis()).getTime());
                                                            final Map messageDetails = new HashMap();
                                                            messageDetails.put(senderRef, messageBody);
                                                            rootRef.child("MessageState").updateChildren(messageDetails).addOnCompleteListener(new OnCompleteListener() {
                                                                @Override
                                                                public void onComplete(@NonNull Task task) {
                                                                    if (task.isSuccessful()) {
                                                                        messageDetails.clear();
                                                                        messageBody.put("from", sender);
                                                                        messageDetails.put(receiverRef, messageBody);
                                                                        rootRef.child("MessageState").updateChildren(messageDetails).addOnCompleteListener(new OnCompleteListener() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Toast.makeText(RequestActivity.this, "Contact saved Successfully", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });

                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                });
                                holder.btnReject.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        cancelRequset();
                                    }
                                });
                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(RequestActivity.this, ProfileActivity.class);
                                        intent.putExtra(profile_key, receiver);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @NonNull
                @Override
                public reqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_model, parent, false);
                    return new reqViewHolder(view);
                }
            };
            reqRecyView.setAdapter(adapter);
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        currentState("offline");
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentState("offline");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sender != null) {
            currentState("offline");
        }
    }

    private void cancelRequset() {
        chatRef.child(sender).child(receiver).child("request_status").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    chatRef.child(receiver).child(sender).child("request_status").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (!isAccept)
                                    Toast.makeText(RequestActivity.this, "Request Cancelled", Toast.LENGTH_SHORT).show();
                                else {
                                    Log.d(TAG, "onComplete: Request Accepted");
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void initViews() {
        reqRecyView = findViewById(R.id.requestRecyView);
        try {
            sender = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (Exception e) {
            e.printStackTrace();
        }
        rootRef = FirebaseDatabase.getInstance().getReference();
        reqRecyView.setLayoutManager(new LinearLayoutManager(this));
        chatRef = rootRef.child("ChatRequest");
        contactRef = rootRef.child("Contact");
        userRef = rootRef.child("User");

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public static class reqViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView image;
        private TextView name, status;
        private Button btnAccept, btnReject;

        public reqViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.Image);
            name = itemView.findViewById(R.id.Name);
            status = itemView.findViewById(R.id.Status);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
