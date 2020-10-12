package com.example.whatsappclone.FragmentsActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappclone.Model.MessageModel;
import com.example.whatsappclone.R;
import com.example.whatsappclone.adaptors.MessageAdaptor;
import com.example.whatsappclone.Model.MessageModel;
import com.example.whatsappclone.R;
import com.example.whatsappclone.adaptors.MessageAdaptor;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class MessageActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private ArrayList<MessageModel> messages=new ArrayList<>();
    private ImageView btnBack,btnSend;
    private EditText txtMessage;
    private TextView grpName;
    private DatabaseReference grpRef,userRef,grpMessageKeyRef;
    private FirebaseAuth firebaseAuth;
    private String currUserName,currUserid;
    private String txtgrpName, message,currTime,currDate,grpMessageKey;
    private MessageAdaptor messageAdaptor;
    private RecyclerView messageRecyview;
    private NestedScrollView nestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.activity_message);

        initViews();
        Intent intent=getIntent();
        if (intent!=null){
            txtgrpName=intent.getStringExtra("name");
            if(grpName!=null){
                grpName.setText(txtgrpName);
            }
        }
        txtMessage.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction()==KeyEvent.ACTION_DOWN) && (keyCode==KeyEvent.KEYCODE_ENTER) ){
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
                if(txtMessage.getText().toString().length()>0){
                    btnSend.setVisibility(View.VISIBLE);
                }else{
                    btnSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        grpRef=FirebaseDatabase.getInstance().getReference().child("Groups").child(txtgrpName).child("Messages");
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

    private void sendMessage() {
        grpMessageKey=grpRef.push().getKey();
        message =txtMessage.getText().toString();
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat sdfDate=new SimpleDateFormat("MMM dd yyyy");
        SimpleDateFormat sdfTime=new SimpleDateFormat("hh:mm a");
        currDate=sdfDate.format(calendar.getTime());
        currTime=sdfTime.format(calendar.getTime());

        HashMap<String,Object> messageKey=new HashMap<>();
        grpRef.updateChildren(messageKey);

        grpMessageKeyRef=grpRef.child(grpMessageKey);

        HashMap<String,Object> messageHashMap=new HashMap<>();

        messageHashMap.put("name",currUserName);
        messageHashMap.put("message", message);
        messageHashMap.put("date",currDate);
        messageHashMap.put("time",currTime);


        grpMessageKeyRef.updateChildren(messageHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(MessageActivity.this,"Something Went Wrong...",Toast.LENGTH_SHORT).show();
                }
            }
        });
        nestedScrollView.fullScroll(NestedScrollView.FOCUS_DOWN);

    }

    @Override
    protected void onStart() {
        super.onStart();
        grpRef.addChildEventListener(new ChildEventListener() {
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
    }

    private void DisplayMessages(DataSnapshot dataSnapshot) {
        Iterator iterator=dataSnapshot.getChildren().iterator();
        while(iterator.hasNext()){
            String date=(String) ((DataSnapshot)iterator.next()).getValue();
            String message=(String) ((DataSnapshot)iterator.next()).getValue();
            String sender=(String) ((DataSnapshot)iterator.next()).getValue();
            String time=(String) ((DataSnapshot)iterator.next()).getValue();

            messages.add(new MessageModel(sender,message, date,time));
        }
        messageAdaptor.setMessages(messages);
        nestedScrollView.fullScroll(NestedScrollView.FOCUS_DOWN);
    }

    private void initViews() {
        toolbar=findViewById(R.id.toolbar);
        nestedScrollView=findViewById(R.id.messageNestedScrollView);
        messageAdaptor=new MessageAdaptor(MessageActivity.this);
        messageRecyview=findViewById(R.id.messageRecyView);
        messageRecyview.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
        messageRecyview.setAdapter(messageAdaptor);
        grpName=findViewById(R.id.groupName);
        btnBack=findViewById(R.id.btnBack);
        btnSend=findViewById(R.id.btnSend);
        txtMessage=findViewById(R.id.txtMessage);
        firebaseAuth=FirebaseAuth.getInstance();
        currUserid=firebaseAuth.getCurrentUser().getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("User");
        userRef.child(currUserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currUserName=dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
//        inflater.inflate(R.menu.menu,menu);
        return true;
    }
}