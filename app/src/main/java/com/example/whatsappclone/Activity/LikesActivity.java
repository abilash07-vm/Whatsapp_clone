package com.example.whatsappclone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.Model.ChatsModel;
import com.example.whatsappclone.R;
import com.example.whatsappclone.adaptors.ChatsAdaptor;
import com.example.whatsappclone.fragments.PostFragment;

import java.util.ArrayList;

public class LikesActivity extends AppCompatActivity {
    private static final String TAG = "LikesActivity";
    private ImageView btnBack, btnSearch;
    private EditText searchBox;
    private RecyclerView likeRecyView;
    private ArrayList<ChatsModel> ids;
    private String strids;
    private ChatsAdaptor adaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);

        initViews();
        Intent intent = getIntent();
        if (intent != null) {
            strids = intent.getStringExtra("ids");
            Log.d(TAG, "onCreate: " + strids);
            if (strids != null) {
                for (String i : strids.split(",")) {
                    if (!i.equals("")) {
                        ChatsModel chat = new ChatsModel();
                        chat.setFrom(i);
                        ids.add(chat);
                    }
                }
                adaptor.setChats(ids);
            }
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
                    showSuggestion(s.toString());
                }
            });
        }


    }

    private void showSuggestion(String s) {
        ArrayList<ChatsModel> chats = new ArrayList<>();
        for (String i : strids.split(",")) {
            String name = PostFragment.contactDetails.get(i).getName();
            Log.d(TAG, "showSuggestion: " + name + "  " + s);
            if (s.equals(name.substring(0, s.length()))) {
                ChatsModel chat = new ChatsModel();
                chat.setFrom(i);
                chats.add(chat);
            }
        }
        adaptor.setChats(chats);

    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        searchBox = findViewById(R.id.searchBox);
        likeRecyView = findViewById(R.id.likerecyView);
        adaptor = new ChatsAdaptor(LikesActivity.this, "likes");
        likeRecyView.setLayoutManager(new LinearLayoutManager(this));
        likeRecyView.setAdapter(adaptor);
        btnSearch = findViewById(R.id.btnsearch);
        ids = new ArrayList<>();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}