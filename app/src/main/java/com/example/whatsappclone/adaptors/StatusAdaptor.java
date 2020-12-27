package com.example.whatsappclone.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.Model.StatusModel;
import com.example.whatsappclone.R;
import com.example.whatsappclone.fragments.PostFragment;
import com.example.whatsappclone.fragments.StatusFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class StatusAdaptor extends RecyclerView.Adapter<StatusAdaptor.ViewHolder> {
    private ArrayList<StatusModel> status = new ArrayList<>();
    private Context context;
    private String type;

    public StatusAdaptor(Context context, String type) {
        this.context = context;
        this.type = type;
    }

    public void setStatus(ArrayList<StatusModel> status) {
        this.status = status;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friends_model, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final StatusModel statusModel = status.get(position);


        holder.name.setText(PostFragment.contactDetails.get(statusModel.getUserid()).getName());

        Date d1 = Calendar.getInstance().getTime();
        Date d2 = new Date(statusModel.getTimestamp());
        long diff = (d1.getTime() - d2.getTime()) / 1000;
        holder.duration.setText(CommentAdaptor.getDate(diff));
        holder.count.setVisibility(View.VISIBLE);
        holder.count.setText(String.valueOf(StatusFragment.statusMap.get(statusModel.getUserid()).size()));

        String imglink = PostFragment.contactDetails.get(statusModel.getUserid()).getImage();
        if (imglink != null && ChatsAdaptor.isValidContextForGlide(context))
            Glide.with(context)
                    .asBitmap()
                    .load(imglink)
                    .into(holder.profileImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new StatusFragment.StatusAsyncTask(context, statusModel.getUserid(), type).execute();
            }
        });

    }

    @Override
    public int getItemCount() {
        return status.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, duration, count;
        private CircleImageView profileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.Name);
            duration = itemView.findViewById(R.id.Status);
            profileImage = itemView.findViewById(R.id.Image);
            count = itemView.findViewById(R.id.msgCount);
        }
    }
}

