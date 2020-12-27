package com.example.whatsappclone.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.Activity.ProfileActivity;
import com.example.whatsappclone.Model.CompletePostModel;
import com.example.whatsappclone.R;

import java.util.ArrayList;

public class PhotoAdaptor extends RecyclerView.Adapter<PhotoAdaptor.ViewHolder> {
    private ArrayList<CompletePostModel> post = new ArrayList<>();
    private Context context;

    public PhotoAdaptor(Context context) {
        this.context = context;
    }

    public void setPost(ArrayList<CompletePostModel> post) {
        this.post = post;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.photos_model, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (ChatsAdaptor.isValidContextForGlide(context)) {
            Glide.with(context)
                    .asBitmap()
                    .load(post.get(position).getPostlink())
                    .into(holder.img);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, ProfileActivity.class);
//                intent.putExtra(POST_ID, post.get(position).getPostid());
//                intent.putExtra(profile_key, post.get(position).getUserid());
//                context.startActivity(intent);
                ProfileActivity.incommingPost = post.get(position);
                ProfileActivity.refresh(context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return post.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.pic);
        }
    }
}
