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

import com.bumptech.glide.Glide;
import com.example.whatsappclone.Activity.GroupInfoActivity;
import com.example.whatsappclone.Activity.GroupMessageActivity;
import com.example.whatsappclone.Model.Dummy;
import com.example.whatsappclone.R;

import java.util.ArrayList;

import static com.example.whatsappclone.Activity.GroupInfoActivity.grpName_key;

public class GroupAdaptor extends RecyclerView.Adapter<GroupAdaptor.ViewHolder> {
    private Context context;
    private ArrayList<Dummy> groups =new ArrayList<>();

    public GroupAdaptor(Context context) {
        this.context = context;
    }

    public void setGroups(ArrayList<Dummy> groups) {
        this.groups = groups;
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
        holder.grpName.setText(groups.get(position).getName());
        if(groups.get(position).getImglink()!=null) {
            Glide.with(context)
                    .asBitmap()
                    .load(groups.get(position).getImglink())
                    .into(holder.grpIcon);
        }

        holder.grpIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, GroupInfoActivity.class);
                intent.putExtra(grpName_key,groups.get(position).getName());
                context.startActivity(intent);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, GroupMessageActivity.class);
                intent.putExtra("name",groups.get(position).getName());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView grpIcon;
        private TextView grpName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            grpIcon=itemView.findViewById(R.id.groupicon);
            grpName=itemView.findViewById(R.id.groupName);
        }
    }
}
