package com.example.whatsappclone.adaptors;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.Activity.GroupInfoActivity;
import com.example.whatsappclone.Activity.GroupMessageActivity;
import com.example.whatsappclone.Model.GroupChatModel;
import com.example.whatsappclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.whatsappclone.Activity.GroupInfoActivity.grpName_key;
import static com.example.whatsappclone.adaptors.ChatsAdaptor.isValidContextForGlide;

public class GroupAdaptor extends RecyclerView.Adapter<GroupAdaptor.ViewHolder> {
    private Context context;
    private ArrayList<GroupChatModel> groups = new ArrayList<>();
    private DatabaseReference grpRef = FirebaseDatabase.getInstance().getReference().child("Groups");

    public GroupAdaptor(Context context) {
        this.context = context;
    }

    public void setGroups(ArrayList<GroupChatModel> groups) {
        this.groups = groups;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_model, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.name.setText(groups.get(position).getName());
        if (groups.get(position).getMsgcount() > 0) {
            holder.msgcount.setVisibility(View.VISIBLE);
            holder.status.setTextColor(Color.rgb(0, 0, 0));
            holder.msgcount.setText(String.valueOf(groups.get(position).getMsgcount()));
        } else {
            holder.msgcount.setVisibility(View.GONE);
            holder.status.setTextColor(Color.rgb(200, 200, 200));
        }

        grpRef.child(groups.get(position).getGrpid()).child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (isValidContextForGlide(context)) {
                        Glide.with(context)
                                .asBitmap()
                                .load(snapshot.getValue())
                                .into(holder.grpIcon);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.grpIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GroupInfoActivity.class);
                intent.putExtra(grpName_key, groups.get(position).getGrpid());
                context.startActivity(intent);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GroupMessageActivity.class);
                intent.putExtra("name", groups.get(position).getGrpid());
                context.startActivity(intent);
            }
        });
        if (groups.get(position).getLastMessage() != null) {
            holder.status.setText(groups.get(position).getLastMessage());
        }

    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, status, msgcount;
        public ImageView grpIcon, online;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.Name);
            status = itemView.findViewById(R.id.Status);
            grpIcon = itemView.findViewById(R.id.Image);
            online = itemView.findViewById(R.id.online);
            msgcount = itemView.findViewById(R.id.msgCount);
        }
    }
}
