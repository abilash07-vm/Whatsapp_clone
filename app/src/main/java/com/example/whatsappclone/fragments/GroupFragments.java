package com.example.whatsappclone.fragments;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.whatsappclone.Activity.GroupInfoActivity;
import com.example.whatsappclone.Activity.MessageActivity;
import com.example.whatsappclone.Model.Dummy;
import com.example.whatsappclone.R;
import com.example.whatsappclone.adaptors.GroupAdaptor;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.whatsappclone.Activity.GroupInfoActivity.grpName_key;

public class GroupFragments extends Fragment {
    private RecyclerView grpRecyView;
    private GroupAdaptor adaptor;
    private ArrayList<String> grpNames;
    private DatabaseReference grpByUserRef,grpRef;
    private FirebaseUser currUsers;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=getActivity().getLayoutInflater().inflate(R.layout.group_fragment,container,false);

        initViews(view);



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(currUsers!=null)
        getAllGroupNames();
    }

    private void initViews(View view) {
        grpRecyView=view.findViewById(R.id.recyView);
        grpNames=new ArrayList<>();
        adaptor=new GroupAdaptor(getActivity());
        currUsers=FirebaseAuth.getInstance().getCurrentUser();
        grpByUserRef = FirebaseDatabase.getInstance().getReference().child("User").child("userGrp").child(currUsers.getUid());
        grpRef=FirebaseDatabase.getInstance().getReference().child("Groups");
    }

    private void getAllGroupNames() {
        try{
            FirebaseRecyclerOptions<Dummy> options=new FirebaseRecyclerOptions.Builder<Dummy>()
                    .setQuery(grpByUserRef,Dummy.class)
                    .build();
            FirebaseRecyclerAdapter<Dummy,grpViewHolder> adapter=new FirebaseRecyclerAdapter<Dummy, grpViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final grpViewHolder holder, final int position, @NonNull Dummy model) {
                    final String grpName=getRef(position).getKey();
                    grpRef.child(grpName).child("image").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Glide.with(getActivity())
                                        .asBitmap()
                                        .load(snapshot.getValue())
                                        .into(holder.grpIcon);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    holder.grpIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(getActivity(), GroupInfoActivity.class);
                            intent.putExtra(grpName_key,grpName);
                            startActivity(intent);
                        }
                    });
                    holder.grpName.setText(grpName);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(getActivity(),MessageActivity.class);
                            intent.putExtra("name",grpName);
                            startActivity(intent);
                        }
                    });

                }

                @NonNull
                @Override
                public grpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.grp_model,parent,false);
                    return new grpViewHolder(view);
                }
            };
            grpRecyView.setLayoutManager(new LinearLayoutManager(getActivity()));
            grpRecyView.setAdapter(adapter);
            adapter.startListening();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static class grpViewHolder extends RecyclerView.ViewHolder {
        private ImageView grpIcon;
        private TextView grpName;
        private MaterialCardView cardView;
        public grpViewHolder(@NonNull View itemView) {
            super(itemView);
            grpIcon=itemView.findViewById(R.id.groupicon);
            grpName=itemView.findViewById(R.id.groupName);
            cardView=itemView.findViewById(R.id.cardview);
        }
    }
}
