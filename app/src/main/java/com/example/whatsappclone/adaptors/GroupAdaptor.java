package com.example.whatsappclone.adaptors;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.FragmentsActivity.MessageActivity;
import com.example.whatsappclone.R;
import com.example.whatsappclone.FragmentsActivity.MessageActivity;
import com.example.whatsappclone.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class GroupAdaptor extends RecyclerView.Adapter<GroupAdaptor.ViewHolder> {
    private Context context;
    private ArrayList<String> grpNames=new ArrayList<>();

    public GroupAdaptor(Context context) {
        this.context = context;
    }

    public void setGrpNames(ArrayList<String> grpNames) {
        this.grpNames = grpNames;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.grp_model,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.grpName.setText(grpNames.get(position));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MessageActivity.class);
                intent.putExtra("name",grpNames.get(position));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return grpNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView grpIcon;
        private TextView grpName;
        private MaterialCardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            grpIcon=itemView.findViewById(R.id.groupicon);
            grpName=itemView.findViewById(R.id.groupName);
            cardView=itemView.findViewById(R.id.cardview);
        }
    }
}
