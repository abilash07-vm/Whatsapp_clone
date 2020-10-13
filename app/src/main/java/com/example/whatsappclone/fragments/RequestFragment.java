package com.example.whatsappclone.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.Activity.ProfileActivity;
import com.example.whatsappclone.Model.Contact;
import com.example.whatsappclone.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestFragment extends Fragment {
    private static final String TAG = "RequestFragment";
    private RecyclerView reqRecyView;
    private DatabaseReference chatRef,userRef,contactRef;
    private String sender,receiver;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=getActivity().getLayoutInflater().inflate(R.layout.request_fragment,container,false);
        initViews(view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contact> options=new FirebaseRecyclerOptions.Builder<Contact>()
                .setQuery(chatRef.child(sender),Contact.class)
                .build();
        FirebaseRecyclerAdapter<Contact,reqViewHolder> adapter=new FirebaseRecyclerAdapter<Contact, reqViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final reqViewHolder holder, int position, @NonNull Contact model) {
                receiver=getRef(position).getKey();
                userRef.child(receiver).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            holder.name.setText(snapshot.child("name").getValue().toString());
                            holder.status.setText(snapshot.child("status").getValue().toString());
                            if(snapshot.child("image").getValue()!=null)
                            Glide.with(getContext())
                                    .asBitmap()
                                    .load(snapshot.child("image").getValue().toString())
                                    .into(holder.image);
                            holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    contactRef.child(sender).child(receiver).child("contact").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                contactRef.child(receiver).child(sender).child("contact").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        cancelRequset();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            });
                            holder.btnReject.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cancelRequset();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public reqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.request_model,parent,false);
                return new reqViewHolder(view);
            }
        };
        reqRecyView.setAdapter(adapter);
        adapter.startListening();
    }

//    private void cancelRequest() {
//        chatRef.child(sender).child(receiver).child("request_status").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    chatRef.child(receiver).child(sender).child("request_status").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if(task.isSuccessful()){
//                                Log.d(TAG, "onComplete: Removed Request");
//                            }
//                        }
//                    });
//                }
//            }
//        });
//
//    }
    private void cancelRequset() {
        chatRef.child(sender).child(receiver).child("request_status").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    chatRef.child(receiver).child(sender).child("request_status").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getActivity(), "Request Cancelled", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public static class reqViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView image;
        private TextView name,status;
        private Button btnAccept,btnReject;
        public reqViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.Image);
            name=itemView.findViewById(R.id.Name);
            status=itemView.findViewById(R.id.Status);
            btnAccept=itemView.findViewById(R.id.btnAccept);
            btnReject=itemView.findViewById(R.id.btnReject);
        }
    }

    private void initViews(View view) {
            reqRecyView=view.findViewById(R.id.requestRecyView);
            sender= FirebaseAuth.getInstance().getUid();
            reqRecyView.setLayoutManager(new LinearLayoutManager(getActivity()));
            chatRef= FirebaseDatabase.getInstance().getReference().child("ChatRequest");
            contactRef=FirebaseDatabase.getInstance().getReference().child("Contact");
            userRef=FirebaseDatabase.getInstance().getReference().child("User");
    }
}
