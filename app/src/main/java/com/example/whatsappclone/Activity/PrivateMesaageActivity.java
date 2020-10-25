package com.example.whatsappclone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.Model.ChatsModel;
import com.example.whatsappclone.Model.PrivateMessageModel;
import com.example.whatsappclone.R;
import com.example.whatsappclone.adaptors.ChatsAdaptor;
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

import static com.example.whatsappclone.MainActivity.currentState;
import static com.example.whatsappclone.settings.FindFriendsActivity.profile_key;

public class PrivateMesaageActivity extends AppCompatActivity {
    public static final String message_key = "private";
    private static final String TAG = "PrivateMesaageActivity";
    private MaterialToolbar toolbar;
    private ImageView btnBack, btnSendMessage;
    private CircleImageView profileImg;
    private TextView profileName, lastSeen;
    private EditText messageBox;
    private RecyclerView messageRecyView;
    private String msgsenderKey, msgreceiverKey;
    private String message;
    private DatabaseReference rootRef, userRef;
    private ArrayList<PrivateMessageModel> messages;
    private PrivateMessageAdaptor adaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_mesaage);
        Intent intent = getIntent();
        if (intent != null) {
            msgreceiverKey = intent.getStringExtra(message_key);
            if (msgreceiverKey != null) {
                initViews();
                toolbar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendToProfileActivity();
                    }
                });
                userRef.child(msgreceiverKey).child("name").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            profileName.setText(snapshot.getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }


    }

    private void sendToProfileActivity() {
        Intent intent1=new Intent(PrivateMesaageActivity.this,ProfileActivity.class);
        intent1.putExtra(profile_key,msgreceiverKey);
        startActivity(intent1);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (msgreceiverKey != null) {
            String senderRef = msgsenderKey + "/" + msgreceiverKey;
            currentState("online");
            rootRef.child("MessageState").child(senderRef).child("msgcount").setValue(0);
            rootRef.child("Messages").child(msgsenderKey).child(msgreceiverKey).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    PrivateMessageModel message = dataSnapshot.getValue(PrivateMessageModel.class);
                    messages.add(message);
                    adaptor.setMessages(messages);
                    messageRecyView.smoothScrollToPosition(adaptor.getItemCount());
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    PrivateMessageModel message = dataSnapshot.getValue(PrivateMessageModel.class);
                    messages.add(message);
                    adaptor.setMessages(messages);
                    messageRecyView.smoothScrollToPosition(adaptor.getItemCount());
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
            userRef.child(msgreceiverKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild("image") && ChatsAdaptor.isValidContextForGlide(PrivateMesaageActivity.this)) {
                        Glide.with(PrivateMesaageActivity.this)
                                .asBitmap()
                                .load(snapshot.child("image").getValue())
                                .into(profileImg);
                    }
                    if (snapshot.hasChild("state")) {
                        if (snapshot.child("state").getValue().equals("online")) {
                            lastSeen.setText("online");
                        } else {
                            lastSeen.setText("lastSeen " + snapshot.child("date").getValue() + "\n" + snapshot.child("time").getValue());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    private void sendMessage() {
        String senderRef = "Messages/" + msgsenderKey + "/" + msgreceiverKey;
        final String receiverRef = "Messages/" + msgreceiverKey + "/" + msgsenderKey;
        final String messageKey = rootRef.child(msgsenderKey).child(msgreceiverKey).push().getKey();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdfDate = new SimpleDateFormat("MMM dd yyyy");
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");
        final String currDate = sdfDate.format(calendar.getTime());
        final String currTime = sdfTime.format(calendar.getTime());

        final Map messageBody = new HashMap();
        messageBody.put("from", msgsenderKey);
        messageBody.put("to", msgreceiverKey);
        messageBody.put("type", "text");
        messageBody.put("message", message);
        messageBody.put("date", currDate);
        messageBody.put("time", currTime);
        messageBody.put("timestamp", new Timestamp(System.currentTimeMillis()).getTime());
        final Map messageDetails = new HashMap();
        messageDetails.put(senderRef + "/" + messageKey, messageBody);
        messageDetails.put(receiverRef + "/" + messageKey, messageBody);

        rootRef.updateChildren(messageDetails).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    final String senderRef = msgsenderKey + "/" + msgreceiverKey;
                    final String receiverRef = msgreceiverKey + "/" + msgsenderKey;

                    Map notificationBody = new HashMap();
                    notificationBody.put("from", msgsenderKey);
                    notificationBody.put("message", message);
                    notificationBody.put("type", "message");

                    FirebaseDatabase.getInstance().getReference().child("MessageNotifications").child(msgreceiverKey).push().updateChildren(notificationBody).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: message notification has sent");
                            }
                        }
                    });

                    rootRef.child("MessageState").child(msgreceiverKey).child(msgsenderKey).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int count = 0;
                            if (snapshot.hasChild("msgcount") && snapshot.child("msgcount").getValue() != null) {
                                ChatsModel chat = snapshot.getValue(ChatsModel.class);
                                count = chat.getMsgcount();
                                Log.d(TAG, "onDataChange: " + chat.toString());
                                final Map messageBody = new HashMap();
                                messageBody.put("from", msgsenderKey);
                                messageBody.put("msgcount", count + 1);
                                messageBody.put("timestamp", new Timestamp(System.currentTimeMillis()).getTime());
                                final Map messageBody2 = new HashMap();
                                messageBody2.put("from", msgreceiverKey);
                                messageBody2.put("msgcount", 0);
                                messageBody2.put("timestamp", new Timestamp(System.currentTimeMillis()).getTime());
                                final Map messageDetails = new HashMap();
                                messageDetails.put(senderRef, messageBody2);
                                messageDetails.put(receiverRef, messageBody);
                                rootRef.child("MessageState").updateChildren(messageDetails).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        messageBox.setText("");
                                    }
                                });
                                rootRef.child("MessageState").child(msgreceiverKey).child(msgsenderKey).removeEventListener(this);
                            }
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
    protected void onPause() {
        super.onPause();
        if (msgsenderKey != null) {
            currentState("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (msgsenderKey != null) {
            currentState("offline");
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnSendMessage = findViewById(R.id.btnSend);
        profileImg = findViewById(R.id.img);
        lastSeen = findViewById(R.id.lastseen);
        toolbar=findViewById(R.id.toolbar);
        profileName = findViewById(R.id.name);
        messageBox = findViewById(R.id.msgBox);
        messageRecyView = findViewById(R.id.msgRecView);
        messages = new ArrayList<>();
        msgsenderKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = FirebaseDatabase.getInstance().getReference().child("User");
        adaptor = new PrivateMessageAdaptor(PrivateMesaageActivity.this);
        messageRecyView.setLayoutManager(new LinearLayoutManager(PrivateMesaageActivity.this));
        messageRecyView.setAdapter(adaptor);

        messageBox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    sendMessage();
                    messageBox.setText("");
                    return true;
                }
                return false;
            }
        });
        profileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrivateMesaageActivity.this, ProfileActivity.class);
                intent.putExtra(profile_key, msgreceiverKey);
                startActivity(intent);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrivateMesaageActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (messageBox.getText().toString().length() > 0) {
                    btnSendMessage.setVisibility(View.VISIBLE);
                } else {
                    btnSendMessage.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = messageBox.getText().toString();
                sendMessage();
                messageBox.setText("");
            }
        });
    }


}