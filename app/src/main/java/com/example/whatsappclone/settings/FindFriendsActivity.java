package com.example.whatsappclone.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.Activity.GroupMessageActivity;
import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.Model.Contact;
import com.example.whatsappclone.Activity.ProfileActivity;
import com.example.whatsappclone.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FindFriendsActivity extends AppCompatActivity {
    public static final String profile_key="key";
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
        Intent intent=new Intent(FindFriendsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void initViews() {
        btnBack=findViewById(R.id.btnBack);
        friendsRecyView=findViewById(R.id.friendsRecyView);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Find Friends");
        userRef= FirebaseDatabase.getInstance().getReference().child("User");
        currUser= FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentState("online");
        FirebaseRecyclerOptions<Contact> options=new FirebaseRecyclerOptions.Builder<Contact>()
                .setQuery(userRef, Contact.class)
                .build();
        Log.d(TAG, "onStart: "+options);
        FirebaseRecyclerAdapter<Contact,FindFriendsViewHolder> adapter= new FirebaseRecyclerAdapter<Contact, FindFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FindFriendsViewHolder holder, final int position, @NonNull Contact model) {
                Log.d(TAG, "onBindViewHolder: "+model.getName()+" "+model.getImage());
                holder.name.setText(model.getName());
                holder.status.setText(model.getStatus());
                Glide.with(FindFriendsActivity.this)
                        .asBitmap()
                        .placeholder(R.drawable.profile_image)
                        .load(model.getImage())
                        .into(holder.image);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(FindFriendsActivity.this, ProfileActivity.class);
                        intent.putExtra(profile_key,getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_model,parent,false);
                return new FindFriendsViewHolder(view);
            }
        };
        friendsRecyView.setAdapter(adapter);
        friendsRecyView.setLayoutManager(new LinearLayoutManager(this));
        adapter.startListening();
    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder {
        public TextView name,status;
        public ImageView image,online;
        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.Name);
            status=itemView.findViewById(R.id.Status);
            image=itemView.findViewById(R.id.Image);
//            online=itemView.findViewById(R.id.online);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(currUser!=null){
            currentState("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(currUser!=null) {
            currentState("offline");
        }
    }
    public void currentState(String state){
        Calendar calendar=Calendar.getInstance();
        String currentDate,currentTime;
        SimpleDateFormat sdfDate=new SimpleDateFormat("MMM dd yyyy");
        SimpleDateFormat sdfTime=new SimpleDateFormat("hh:mm a");
        currentDate=sdfDate.format(calendar.getTime());
        currentTime=sdfTime.format(calendar.getTime());
        Map<String,Object> stateMap=new HashMap<>();
        stateMap.put("date",currentDate);
        stateMap.put("time",currentTime);
        stateMap.put("state",state);
        userRef.child(currUser).updateChildren(stateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete()){
                    Toast.makeText(FindFriendsActivity.this,"welcome back ",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        sendBackToMainActivity();
    }
}