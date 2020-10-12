package com.example.whatsappclone.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adaptors.GroupAdaptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class GroupFragments extends Fragment {
    private RecyclerView grpRecyView;
    private GroupAdaptor adaptor;
    private ArrayList<String> groupNames;
    private DatabaseReference reference;
    private FirebaseUser currUsers;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=getActivity().getLayoutInflater().inflate(R.layout.group_fragment,container,false);

        initViews(view);

        getAllGroupNames();
        grpRecyView.setLayoutManager(new LinearLayoutManager(getActivity()));
        grpRecyView.setAdapter(adaptor);
        adaptor.setGrpNames(groupNames);

        return view;
    }

    private void initViews(View view) {
        grpRecyView=view.findViewById(R.id.recyView);
        groupNames=new ArrayList<>();
        adaptor=new GroupAdaptor(getActivity());
        currUsers=FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference().child("Groups");
    }

    private void getAllGroupNames() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Set<String> grpNamesSet =new TreeSet<>();
                Iterator iterator= dataSnapshot.getChildren().iterator();
                while(iterator.hasNext()){
                    String name=((DataSnapshot) iterator.next()).getKey();
                    if(!groupNames.contains(name))
                    groupNames.add(name);
                }
//                groupNames.addAll(grpNamesSet);
                adaptor.setGrpNames(groupNames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
