package com.example.whatsappclone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.Model.CommentModel;
import com.example.whatsappclone.R;
import com.example.whatsappclone.adaptors.CommentAdaptor;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.example.whatsappclone.MainActivity.currentState;

public class CommentActivity extends AppCompatActivity {
    private static final String TAG = "CommentActivity";
    private DatabaseReference commentRef;
    private ImageView btnBack;
    private RecyclerView commentRecyView;
    private String incommingPostid;
    private ArrayList<CommentModel> comments;
    private CommentAdaptor adaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        initViews();
        Intent intent = getIntent();
        if (intent != null) {
            incommingPostid = intent.getStringExtra("postid");
        }
        Log.d(TAG, "onCreate: " + incommingPostid);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (incommingPostid != null) {
            commentRef.child(incommingPostid).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    ExtractComment(snapshot);
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
    }

    private void ExtractComment(DataSnapshot snapshot) {
        try {
            CommentModel model = snapshot.getValue(CommentModel.class);
            Log.d(TAG, "ExtractComment: " + model.toString());
            if (!comments.contains(model)) {
                comments.add(model);
            }
            Collections.sort(comments, new Comparator<CommentModel>() {
                @Override
                public int compare(CommentModel o1, CommentModel o2) {
                    return (int) (o2.getTimestamp() - o1.getTimestamp());
                }
            });
            adaptor.setComments(comments);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        comments.clear();
        currentState("offline");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentState("offline");
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentState("offline");
    }

    private void initViews() {
        commentRef = FirebaseDatabase.getInstance().getReference().child("Comment");
        btnBack = findViewById(R.id.btnBack);
        commentRecyView = findViewById(R.id.commentRecyView);
        comments = new ArrayList<>();
        commentRecyView.setLayoutManager(new LinearLayoutManager(this));
        adaptor = new CommentAdaptor(this);
        commentRecyView.setAdapter(adaptor);
    }
}