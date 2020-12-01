package com.example.whatsappclone.adaptors;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.Activity.LikesActivity;
import com.example.whatsappclone.Activity.ProfileActivity;
import com.example.whatsappclone.Model.CompletePostModel;
import com.example.whatsappclone.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.whatsappclone.adaptors.ChatsAdaptor.isValidContextForGlide;
import static com.example.whatsappclone.settings.FindFriendsActivity.profile_key;

public class PostAdaptor extends RecyclerView.Adapter<PostAdaptor.ViewHolder> {
    private static final String TAG = "PostAdaptor";
    public ArrayList<CompletePostModel> posts = new ArrayList<>();
    private Context context;
    private String currentUserId = FirebaseAuth.getInstance().getUid();
    private DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Post");

    public PostAdaptor(Context context) {
        this.context = context;
    }

    public void setPosts(ArrayList<CompletePostModel> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post_model, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: " + posts.get(position).toString());
        try {
            final CompletePostModel post = posts.get(position);
            final Set<String> likedids = new HashSet<>();
            if (posts.get(position).getLikedby() != null) {
                likedids.addAll(Arrays.asList(post.getLikedby().split(",")));
            }
            if (likedids.contains(currentUserId)) {
                holder.likes.setImageResource(R.drawable.like);
            } else {
                holder.likes.setImageResource(R.drawable.unlike);
            }
            holder.profilename.setText(post.getName());
            holder.likesCount.setText(String.valueOf(post.getLikesCount()) + " likes");

            holder.profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra(profile_key, post.getUserid());
                    context.startActivity(intent);
                }
            });

            holder.likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (likedids.contains(currentUserId)) {
                        Log.d(TAG, "onClick: before unlike " + likedids);
                        holder.likes.setImageResource(R.drawable.unlike);
                        String likedbyStr = "";
                        int count = 0;
                        for (String i : likedids) {
                            if (!i.equals(currentUserId)) {
                                likedbyStr += i + ",";
                                count++;
                            }
                        }
                        likedids.remove(currentUserId);
                        if (!likedbyStr.equals(""))
                            likedbyStr = likedbyStr.substring(0, likedbyStr.length() - 1);
                        posts.get(position).setLikesCount(posts.get(position).getLikesCount() - 1);
                        holder.likesCount.setText(String.valueOf(posts.get(position).getLikesCount()) + " likes");
                        Log.d(TAG, "onClick: sfter unlike " + likedids);
                        Map updateMap = new HashMap();
                        updateMap.put("likesCount", posts.get(position).getLikesCount());
                        updateMap.put("likedby", likedbyStr);
                        postRef.child(posts.get(position).getUserid()).child(posts.get(position).getPostid()).updateChildren(updateMap);
                    } else {
                        Log.d(TAG, "onClick: before like " + likedids);
                        holder.likes.setImageResource(R.drawable.like);
                        likedids.add(currentUserId);
                        Log.d(TAG, "onClick: adding " + likedids);
                        String likedbyStr = "";
                        int count = 0;
                        for (String i : likedids) {
                            if (!i.equals(" ")) {
                                likedbyStr += i + ",";
                                count++;
                            }
                        }
                        if (!likedbyStr.equals(""))
                            likedbyStr = likedbyStr.substring(0, likedbyStr.length() - 1);
                        posts.get(position).setLikesCount(posts.get(position).getLikesCount() + 1);
                        holder.likesCount.setText(String.valueOf(posts.get(position).getLikesCount()) + " likes");
                        if (likedbyStr.indexOf(',') == 0) {
                            likedbyStr = likedbyStr.substring(1);
                        }
                        Log.d(TAG, "onClick: after like " + likedids);
                        Map updateMap = new HashMap();
                        updateMap.put("likesCount", posts.get(position).getLikesCount());
                        updateMap.put("likedby", likedbyStr);
                        postRef.child(posts.get(position).getUserid()).child(posts.get(position).getPostid()).updateChildren(updateMap);
                    }
                    holder.likesCount.setText(String.valueOf(post.getLikesCount()) + " likes");

                }
            });
            if (isValidContextForGlide(context) && post.getPostlink() != null) {
                Glide.with(context)
                        .asBitmap()
                        .load(post.getPostlink())
                        .into(holder.post);
            }
            if (isValidContextForGlide(context) && post.getImage() != null) {
                Glide.with(context)
                        .asBitmap()
                        .load(post.getImage())
                        .into(holder.profileImg);
            }
            holder.likesCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, LikesActivity.class);
                    intent.putExtra("ids", post.getLikedby());
                    context.startActivity(intent);
                }
            });
            if (post.getUserid().equals(currentUserId)) {
                holder.btnEditCaption.setVisibility(View.VISIBLE);
            } else {
                holder.btnEditCaption.setVisibility(View.GONE);
            }
            holder.caption.setVisibility(View.VISIBLE);
            if (post.getCaption() != null) {
                holder.caption.setText(post.getCaption());
            } else {
                if (post.getUserid().equals(currentUserId)) {
                    holder.caption.setText("No Caption");
                } else {
                    holder.caption.setVisibility(View.GONE);
                }
            }
            holder.btnEditCaption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.btnEditCaption.getText().equals("EDIT")) {
                        holder.captionBox.setVisibility(View.VISIBLE);
                        holder.caption.setVisibility(View.GONE);
                        holder.captionBox.setText(post.getCaption());
                        holder.btnEditCaption.setText("Update");
                    } else {
                        holder.captionBox.setVisibility(View.GONE);
                        holder.caption.setVisibility(View.VISIBLE);
                        holder.caption.setText(post.getCaption());
                        holder.btnEditCaption.setText("Update");
                        postRef.child(post.getUserid()).child(post.getPostid()).child("caption").setValue(holder.captionBox.getText().toString());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profileImg;
        private ImageView post, likes, comment, share;
        private TextView profilename, likesCount, caption;
        private EditText commentBox, captionBox;
        private ExtendedFloatingActionButton btnAddComment, btnEditCaption;
        private LinearLayout profile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.postprofileImage);
            profilename = itemView.findViewById(R.id.postProfileName);
            post = itemView.findViewById(R.id.postImg);
            likes = itemView.findViewById(R.id.like);
            profile = itemView.findViewById(R.id.navigate);
            comment = itemView.findViewById(R.id.comment);
            share = itemView.findViewById(R.id.share);
            likesCount = itemView.findViewById(R.id.likeCount);
            commentBox = itemView.findViewById(R.id.commentbox);
            caption = itemView.findViewById(R.id.caption);
            captionBox = itemView.findViewById(R.id.addcaption);
            btnAddComment = itemView.findViewById(R.id.btnAddCommemt);
            btnEditCaption = itemView.findViewById(R.id.btnEditCaption);
        }
    }
}

