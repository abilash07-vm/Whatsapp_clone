package com.example.whatsappclone.settings;

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
import com.example.whatsappclone.Activity.ProfileActivity;
import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.Model.Contact;
import com.example.whatsappclone.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.whatsappclone.MainActivity.currentState;

public class FindFriendsActivity extends AppCompatActivity {
    public static final String profile_key = "key";
    private static final String TAG = "FindFriendsActivity";
    private DatabaseReference userRef;
    private ImageView btnBack;
    private RecyclerView friendsRecyView;
    private MaterialToolbar toolbar;
    private String currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        initViews();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackToMainActivity();
            }
        });


    }

    private void sendBackToMainActivity() {
        Intent intent = new Intent(FindFriendsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        friendsRecyView = findViewById(R.id.friendsRecyView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Find Friends");
        userRef = FirebaseDatabase.getInstance().getReference().child("User");
        currUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentState("online");
        FirebaseRecyclerOptions<Contact> options = new FirebaseRecyclerOptions.Builder<Contact>()
                .setQuery(userRef, Contact.class)
                .build();
        Log.d(TAG, "onStart: " + options);
        FirebaseRecyclerAdapter<Contact, FindFriendsViewHolder> adapter = new FirebaseRecyclerAdapter<Contact, FindFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FindFriendsViewHolder holder, final int position, @NonNull Contact model) {
                Log.d(TAG, "onBindViewHolder: " + model.getName() + " " + model.getImage());
                holder.name.setText(model.getName());
                holder.status.setText(model.getStatus());
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .placeholder(R.drawable.profile_image)
                        .load(model.getImage())
                        .into(holder.image);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FindFriendsActivity.this, ProfileActivity.class);
                        intent.putExtra(profile_key, getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_model, parent, false);
                return new FindFriendsViewHolder(view);
            }
        };
        friendsRecyView.setAdapter(adapter);
        friendsRecyView.setLayoutManager(new LinearLayoutManager(this));
        adapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (currUser != null) {
            currentState("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currUser != null) {
            currentState("offline");
        }
    }


    @Override
    public void onBackPressed() {
        sendBackToMainActivity();
    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder {
        public TextView name, status;
        public ImageView image, online;

        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.Name);
            status = itemView.findViewById(R.id.Status);
            image = itemView.findViewById(R.id.Image);
//            online=itemView.findViewById(R.id.online);
        }
    }
}