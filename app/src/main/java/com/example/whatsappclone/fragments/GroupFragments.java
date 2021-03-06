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

import com.example.whatsappclone.Model.GroupChatModel;
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
import java.util.Collections;
import java.util.Comparator;

import static com.example.whatsappclone.MainActivity.currentState;
import static com.example.whatsappclone.MainActivity.grpcount;

public class GroupFragments extends Fragment {
    private static final String TAG = "GroupFragments";
    private RecyclerView grpRecyView;
    private DatabaseReference grpByUserRef, grpRef;
    private FirebaseUser currUsers;
    private TextView txtShowNotAvailable;
    private ArrayList<GroupChatModel> groups;
    private GroupAdaptor adaptor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.group_fragment, container, false);

        initViews(view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (currUsers != null) {
            getAllGroupNames();
            currentState("online");
        }
        if (groups.size() == 0) {
            txtShowNotAvailable.setText("No Group Avialable Add Group \n by Menu->New Group");
            txtShowNotAvailable.setVisibility(View.VISIBLE);
        } else {
            txtShowNotAvailable.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (currUsers != null) {
            currentState("offline");
        }
    }

    private void initViews(View view) {
        grpRecyView = view.findViewById(R.id.recyView);
        txtShowNotAvailable = view.findViewById(R.id.txtischatAvailable);
        groups = new ArrayList<>();
        currUsers = FirebaseAuth.getInstance().getCurrentUser();
        if (currUsers != null)
            grpByUserRef = FirebaseDatabase.getInstance().getReference().child("UserGroup").child(currUsers.getUid());
        grpRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        adaptor = new GroupAdaptor(getContext());
        grpRecyView.setLayoutManager(new LinearLayoutManager(getContext()));
        grpRecyView.setAdapter(adaptor);
    }

    private int getCount() {
        int c = 0;
        for (GroupChatModel i : groups) {
            if (i.getMsgcount() > 0) {
                c++;
            }
        }
        return c;
    }

    private void getAllGroupNames() {
        grpByUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groups.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    GroupChatModel group = snap.getValue(GroupChatModel.class);
                    Log.d(TAG, "onDataChange: " + group.toString());
                    groups.add(group);
                    Collections.sort(groups, new Comparator<GroupChatModel>() {
                        @Override
                        public int compare(GroupChatModel o1, GroupChatModel o2) {
                            return o1.getTimestamp() < o2.getTimestamp() ? 1 : -1;
                        }
                    });
                    txtShowNotAvailable.setVisibility(View.GONE);
                    adaptor.setGroups(groups);
                    int count = getCount();
                    if (count == 0) {
                        grpcount.setVisibility(View.GONE);
                    } else {
                        grpcount.setVisibility(View.VISIBLE);
                        grpcount.setText(String.valueOf(count));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
