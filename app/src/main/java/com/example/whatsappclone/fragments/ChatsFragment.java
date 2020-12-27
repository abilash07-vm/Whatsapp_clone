package com.example.whatsappclone.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.Model.ChatsModel;
import com.example.whatsappclone.R;
import com.example.whatsappclone.adaptors.ChatsAdaptor;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.example.whatsappclone.MainActivity.chatcount;
import static com.example.whatsappclone.MainActivity.currentState;

public class ChatsFragment extends Fragment {
    private static final String TAG = "ChatsFragment";
    private MaterialToolbar toolbar;
    private View view;
    private RecyclerView chatsRecyView;
    private DatabaseReference contactRef, userRef, userStateRef;
    private ArrayList<ChatsModel> chats;
    private TextView txtShowNotAvailable;
    private FirebaseAuth auth;
    private ChatsAdaptor adaptor;
    private String currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.group_fragment, container, false);
        initViews();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (currentUser != null) {
            currentState("online");
            userStateRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    chats.clear();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        ChatsModel chat = snap.getValue(ChatsModel.class);
                        Log.d(TAG, "onDataChange: " + chat.toString());
                        chats.add(chat);
                        Collections.sort(chats, new Comparator<ChatsModel>() {
                            @Override
                            public int compare(ChatsModel o1, ChatsModel o2) {
                                return o1.getTimestamp() < o2.getTimestamp() ? 1 : -1;
                            }
                        });
                        txtShowNotAvailable.setVisibility(View.GONE);
                        adaptor.setChats(chats);
                        int count = getCount();
                        if (count == 0) {
                            chatcount.setVisibility(View.GONE);
                        } else {
                            chatcount.setVisibility(View.VISIBLE);
                            chatcount.setText(String.valueOf(count));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            if (chats.size() == 0) {
                txtShowNotAvailable.setText("No Chat Avialable Add friend \n by Menu->Find Friends");
                txtShowNotAvailable.setVisibility(View.VISIBLE);
            } else {
                txtShowNotAvailable.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (currentUser != null)
            currentState("offline");
    }

    private int getCount() {
        int c = 0;
        for (ChatsModel i : chats) {
            if (i.getMsgcount() > 0) {
                c++;
            }
        }
        return c;
    }

    private void initViews() {
        chatsRecyView = view.findViewById(R.id.recyView);
        chatsRecyView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adaptor = new ChatsAdaptor(getActivity());
        chatsRecyView.setAdapter(adaptor);
        userRef = FirebaseDatabase.getInstance().getReference().child("User");
        auth = FirebaseAuth.getInstance();
        chats = new ArrayList<>();
        txtShowNotAvailable = view.findViewById(R.id.txtischatAvailable);
        if (auth.getCurrentUser() != null) {
            currentUser = auth.getCurrentUser().getUid();
            contactRef = FirebaseDatabase.getInstance().getReference().child("Contact").child(currentUser);
            userStateRef = FirebaseDatabase.getInstance().getReference().child("MessageState").child(currentUser);
        }
    }
}
