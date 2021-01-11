package com.example.whatsappclone.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.Model.ChatsModel;
import com.example.whatsappclone.R;
import com.example.whatsappclone.adaptors.ChatsAdaptor;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.example.whatsappclone.MainActivity.currentState;

public class FindFriendsActivity extends AppCompatActivity {
    public static final String profile_key = "key";
    private static final String TAG = "FindFriendsActivity";
    private DatabaseReference userRef;
    private ImageView btnBack;
    private RecyclerView friendsRecyView;
    private MaterialToolbar toolbar;
    private String currUser;
    private ArrayList<ChatsModel> chats;
    private ArrayList<String> names;
    private EditText searchBox;
    private ChatsAdaptor adaptor;

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
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                showSuggestion(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showSuggestion(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void showSuggestion(String s) {
        ArrayList<ChatsModel> chatsuggestion = new ArrayList<>();
        Log.d(TAG, "showSuggestion: all su " + names);
        for (String i : names) {
            try {
                if (s.equalsIgnoreCase(i.substring(0, s.length()))) {
                    ChatsModel chat = new ChatsModel();
                    chat.setFrom(chats.get(names.indexOf(i)).getFrom());
                    chatsuggestion.add(chat);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "showSuggestion: all suggestion" + chatsuggestion);
        adaptor.setChats(chatsuggestion);

    }

    private void sendBackToMainActivity() {
        Intent intent = new Intent(FindFriendsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        friendsRecyView = findViewById(R.id.friendsRecyView);
        friendsRecyView.setLayoutManager(new LinearLayoutManager(FindFriendsActivity.this));
        adaptor = new ChatsAdaptor(FindFriendsActivity.this, "friend");
        friendsRecyView.setAdapter(adaptor);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Find Friends");
        searchBox = findViewById(R.id.search);
        names = new ArrayList<>();
        userRef = FirebaseDatabase.getInstance().getReference().child("User");
        currUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        chats = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentState("online");
        userRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    chats.add(new ChatsModel(snapshot.child("uid").getValue().toString()));
                    names.add(snapshot.child("name").getValue().toString());
                    adaptor.setChats(chats);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (currUser != null) {
            currentState("offline");
            chats.clear();
            names.clear();
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

}