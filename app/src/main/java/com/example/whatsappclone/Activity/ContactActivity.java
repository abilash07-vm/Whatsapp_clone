package com.example.whatsappclone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.whatsappclone.settings.FindFriendsActivity.profile_key;

public class ContactActivity extends AppCompatActivity {
    private static final String TAG = "ContactActivity";
    private MaterialToolbar toolbar;
    private RecyclerView contactRecView;
    private ImageView btnBack;
    private DatabaseReference contactRef, userRef;
    private FirebaseAuth auth;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);


        initViews();
        setSupportActionBar(toolbar);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        currentState("online");
        FirebaseRecyclerOptions<Contact> options = new FirebaseRecyclerOptions.Builder<Contact>()
                .setQuery(contactRef, Contact.class)
                .build();
        FirebaseRecyclerAdapter<Contact, contactViewHolder> adapter = new FirebaseRecyclerAdapter<Contact, contactViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final contactViewHolder holder, int position, @NonNull final Contact model) {
                final String key = getRef(position).getKey();
                Log.d(TAG, "onBindViewHolder: " + key);
                userRef.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.name.setText(snapshot.child("name").getValue().toString());
                            holder.status.setText(snapshot.child("status").getValue().toString());
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ContactActivity.this, ProfileActivity.class);
                                    intent.putExtra(profile_key, key);
                                    startActivity(intent);
                                }
                            });
                            if (snapshot.child("state").exists() && snapshot.child("state").getValue().toString().equals("online")) {
                                holder.online.setVisibility(View.VISIBLE);
                            } else {
                                holder.online.setVisibility(View.GONE);
                            }
                            if (snapshot.hasChild("image")) {
                                Glide.with(getApplicationContext())
                                        .asBitmap()
                                        .placeholder(R.drawable.profile_image)
                                        .load(snapshot.child("image").getValue().toString())
                                        .into(holder.image);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public contactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_model, parent, false);
                return new contactViewHolder(view);
            }
        };
        contactRecView.setAdapter(adapter);
        contactRecView.setLayoutManager(new LinearLayoutManager(ContactActivity.this));
        adapter.startListening();

    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        contactRecView = findViewById(R.id.contactRecyView);
        btnBack = findViewById(R.id.btnBack);

        userRef = FirebaseDatabase.getInstance().getReference().child("User");
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser().getUid();
        contactRef = FirebaseDatabase.getInstance().getReference().child("Contact").child(currentUser);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (currentUser != null) {
            currentState("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentUser != null) {
            currentState("offline");
        }
    }

    public void currentState(String state) {
        Calendar calendar = Calendar.getInstance();
        String currentDate, currentTime;
        SimpleDateFormat sdfDate = new SimpleDateFormat("MMM dd yyyy");
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");
        currentDate = sdfDate.format(calendar.getTime());
        currentTime = sdfTime.format(calendar.getTime());
        Map<String, Object> stateMap = new HashMap<>();
        stateMap.put("date", currentDate);
        stateMap.put("time", currentTime);
        stateMap.put("state", state);
        userRef.child(currentUser).updateChildren(stateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    Log.d(TAG, "onComplete: welcome back");
                }
            }
        });
    }

    public static class contactViewHolder extends RecyclerView.ViewHolder {
        public TextView name, status;
        public ImageView image, online;

        public contactViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.Name);
            status = itemView.findViewById(R.id.Status);
            image = itemView.findViewById(R.id.Image);
            online = itemView.findViewById(R.id.online);
        }
    }

}