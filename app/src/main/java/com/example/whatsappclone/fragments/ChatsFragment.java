package com.example.whatsappclone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.Activity.ContactActivity;
import com.example.whatsappclone.Activity.PrivateMesaageActivity;
import com.example.whatsappclone.Model.Contact;
import com.example.whatsappclone.Model.Dummy;
import com.example.whatsappclone.Model.PrivateMessageModel;
import com.example.whatsappclone.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import static com.example.whatsappclone.Activity.PrivateMesaageActivity.message_key;

public class ChatsFragment extends Fragment {
    private MaterialToolbar toolbar;
    private View view;
    private RecyclerView chatsRecyView;
    private ImageView btnBack;
    private DatabaseReference contactRef,userRef,userStateRef;
    private ArrayList<PrivateMessageModel>
    private FirebaseAuth auth;
    private String currentUser;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=getActivity().getLayoutInflater().inflate(R.layout.group_fragment,container,false);
        initViews();
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        if(currentUser!=null) {
            userStateRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    groups.clear();
                    for(DataSnapshot snap:snapshot.getChildren()){
                        Dummy group=snap.getValue(Dummy.class);
                        groups.add(group);
                        Collections.sort(groups, new Comparator<Dummy>() {
                            @Override
                            public int compare(Dummy o1, Dummy o2) {
                                return o1.getTimestamp()<o2.getTimestamp() ? 1 :-1;
                            }
                        });
                        adaptor.setGroups(groups);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
//            FirebaseRecyclerOptions<Contact> options = new FirebaseRecyclerOptions.Builder<Contact>()
//                    .setQuery(contactRef, Contact.class)
//                    .build();
//            FirebaseRecyclerAdapter<Contact, ContactActivity.contactViewHolder> adapter = new FirebaseRecyclerAdapter<Contact, ContactActivity.contactViewHolder>(options) {
//                @Override
//                protected void onBindViewHolder(@NonNull final ContactActivity.contactViewHolder holder, int position, @NonNull final Contact model) {
//                    String key = getRef(position).getKey();
//                    userRef.child(key).addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull final DataSnapshot snapshot) {
//                            if (snapshot.exists()) {
//                                holder.name.setText(snapshot.child("name").getValue().toString());
//                                holder.status.setText(snapshot.child("status").getValue().toString());
//                                if (snapshot.child("state").exists() && snapshot.child("state").getValue().toString().equals("online")) {
//                                    holder.online.setVisibility(View.VISIBLE);
//                                } else {
//                                    holder.online.setVisibility(View.GONE);
//                                }
//                                if (snapshot.child("image").exists()) {
//                                    Glide.with(getActivity())
//                                            .asBitmap()
//                                            .load(snapshot.child("image").getValue().toString())
//                                            .into(holder.image);
//                                }
//                                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Intent intent = new Intent(getContext(), PrivateMesaageActivity.class);
//                                        intent.putExtra(message_key, snapshot.getKey());
//                                        startActivity(intent);
//                                    }
//                                });
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//                }
//
//                @NonNull
//                @Override
//                public ContactActivity.contactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_model, parent, false);
//                    return new ContactActivity.contactViewHolder(view);
//                }
//            };
//            chatsRecyView.setAdapter(adapter);
//            chatsRecyView.setLayoutManager(new LinearLayoutManager(getActivity()));
//            adapter.startListening();
        }

    }
    private void initViews() {
        chatsRecyView=view.findViewById(R.id.recyView);
        userRef= FirebaseDatabase.getInstance().getReference().child("User");
        auth= FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null) {
            currentUser = auth.getCurrentUser().getUid();
            contactRef = FirebaseDatabase.getInstance().getReference().child("Contact").child(currentUser);
            userStateRef=FirebaseDatabase.getInstance().getReference().child("MessageState").child(currentUser);
        }
    }
}
