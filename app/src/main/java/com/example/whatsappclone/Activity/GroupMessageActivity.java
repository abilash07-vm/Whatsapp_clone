package com.example.whatsappclone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import static com.example.whatsappclone.adaptors.ChatsAdaptor.isValidContextForGlide;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.Model.GroupChatModel;
import com.example.whatsappclone.Model.PrivateMessageModel;
import com.example.whatsappclone.R;
import com.example.whatsappclone.adaptors.PrivateMessageAdaptor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.whatsappclone.Activity.GroupInfoActivity.grpName_key;
import static com.example.whatsappclone.MainActivity.currentState;

public class GroupMessageActivity extends AppCompatActivity {
    private static final String TAG = "GroupMessageActivity";
    private MaterialToolbar toolbar;
    private ArrayList<PrivateMessageModel> messages = new ArrayList<>();
    private ImageView btnBack, btnSend;
    private EditText txtMessage;
    private TextView grpName;
    private DatabaseReference grpRef, userRef, grpMessageKeyRef;
    private FirebaseAuth firebaseAuth;
    private String currUserName, currUserid;
    private String txtgrpName, message, currTime, currDate, grpMessageKey;
    private RecyclerView messageRecyview;
    private PrivateMessageAdaptor adaptor;
    private CircleImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message);


        Intent intent = getIntent();
        if (intent != null) {
            txtgrpName = intent.getStringExtra("name");
            if (txtgrpName != null) {
                initViews();
                grpName.setText(txtgrpName);
                grpName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent1 = new Intent(GroupMessageActivity.this, GroupInfoActivity.class);
                        intent1.putExtra(grpName_key, grpName.getText());
                        startActivity(intent1);
                    }
                });
                txtMessage.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            sendMessage();
                            txtMessage.setText("");
                            return true;
                        }
                        return false;
                    }
                });
                txtMessage.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (txtMessage.getText().toString().length() > 0) {
                            btnSend.setVisibility(View.VISIBLE);
                        } else {
                            btnSend.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                btnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendMessage();
                        txtMessage.setText("");
                    }
                });
            }
        }

    }

    private void sendMessage() {
        grpMessageKey = grpRef.push().getKey();
        message = txtMessage.getText().toString();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdfDate = new SimpleDateFormat("MMM dd yyyy");
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");
        currDate = sdfDate.format(calendar.getTime());
        currTime = sdfTime.format(calendar.getTime());

        grpMessageKeyRef = grpRef.child("Messages").child(grpMessageKey);
        HashMap<String, Object> messageKey = new HashMap<>();
        grpRef.updateChildren(messageKey);

        HashMap<String, Object> messageHashMap = new HashMap<>();

        messageHashMap.put("from", currUserid);
        messageHashMap.put("message", message);
        messageHashMap.put("to", currUserid);
        messageHashMap.put("type", "text");
        messageHashMap.put("date", currDate);
        messageHashMap.put("time", currTime);


        grpMessageKeyRef.updateChildren(messageHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(GroupMessageActivity.this, "Something Went Wrong...", Toast.LENGTH_SHORT).show();
                } else {
                    final Map<String, Object> stateMap = new HashMap<>();
                    stateMap.put("timestamp", new Timestamp(System.currentTimeMillis()).getTime());
                    stateMap.put("name", txtgrpName);
                    grpRef.child("image").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                stateMap.put("imglink", snapshot.getValue());
                            } else {
                                stateMap.put("imglink", null);
                            }
                            Log.d(TAG, "onDataChange: " + snapshot.getValue());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    grpRef.child("Members").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (final DataSnapshot snap : snapshot.getChildren()) {
                                if (!snap.getKey().equals(currUserid)) {
                                    Map notificationMap = new HashMap();
                                    notificationMap.put("from", currUserid);
                                    notificationMap.put("message", message);
                                    notificationMap.put("type", "groupmessage");
                                    notificationMap.put("grpName", txtgrpName);
                                    FirebaseDatabase.getInstance().getReference().child("GroupNotifications").child(snap.getKey()).push().updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            Log.d(TAG, "onComplete: notification set for " + snap.getKey());
                                        }
                                    });
                                }
                                Log.d(TAG, "onDataChange: members " + snap.getKey() + " " + txtgrpName);
                                FirebaseDatabase.getInstance().getReference().child("UserGroup").child(snap.getKey()).child(txtgrpName).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.hasChild("msgcount")) {
                                            GroupChatModel chat = snapshot.getValue(GroupChatModel.class);
                                            Log.d(TAG, "onDataChange: " + chat.toString());

                                            if (!snap.getKey().equals(currUserid)) {
                                                stateMap.put("msgcount", chat.getMsgcount() + 1);
                                            } else {

                                                stateMap.put("msgcount", 0);
                                            }
                                            FirebaseDatabase.getInstance().getReference().child("UserGroup").child(snap.getKey()).child(txtgrpName).updateChildren(stateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                    }
                                                }
                                            });
                                            FirebaseDatabase.getInstance().getReference().child("UserGroup").child(snap.getKey()).child(txtgrpName).removeEventListener(this);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }

                                });

                            }
                            grpRef.child("Members").removeEventListener(this);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (txtgrpName != null) {
            currentState("online");
            FirebaseDatabase.getInstance().getReference().child("UserGroup").child(currUserid).child(txtgrpName).child("msgcount").setValue(0);
            grpRef.child("Messages").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    DisplayMessages(dataSnapshot);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    DisplayMessages(dataSnapshot);
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            grpRef.child("image").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && isValidContextForGlide(GroupMessageActivity.this)) {
                        Glide.with(GroupMessageActivity.this)
                                .asBitmap()
                                .load(snapshot.getValue())
                                .into(img);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void DisplayMessages(DataSnapshot dataSnapshot) {
        PrivateMessageModel message = dataSnapshot.getValue(PrivateMessageModel.class);
        messages.add(message);
        adaptor.setMessages(messages);
        messageRecyview.smoothScrollToPosition(messageRecyview.getAdapter().getItemCount() + 1);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        messageRecyview = findViewById(R.id.messageRecyView);
        messageRecyview.setLayoutManager(new LinearLayoutManager(GroupMessageActivity.this));
        firebaseAuth = FirebaseAuth.getInstance();
        currUserid = firebaseAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("User");
        grpName = findViewById(R.id.grpName);
        btnBack = findViewById(R.id.btnBack);
        btnSend = findViewById(R.id.btnSend);
        txtMessage = findViewById(R.id.txtMessage);
        img = findViewById(R.id.Image);

        if (txtgrpName != null)
            grpRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(txtgrpName);
        adaptor = new PrivateMessageAdaptor(GroupMessageActivity.this);
        messageRecyview.setAdapter(adaptor);
        userRef.child(currUserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currUserName = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (currUserid != null) {
            currentState("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currUserid != null) {
            currentState("offline");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu,menu);
        return true;
    }
}