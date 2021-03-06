package com.example.whatsappclone.adaptors;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.Activity.CommentActivity;
import com.example.whatsappclone.Activity.LikesActivity;
import com.example.whatsappclone.Activity.ProfileActivity;
import com.example.whatsappclone.Model.CommentModel;
import com.example.whatsappclone.Model.CompletePostModel;
import com.example.whatsappclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
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
    private DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child("Comment");

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
                holder.likes.setImageResource(R.drawable.ic_like);
            } else {
                holder.likes.setImageResource(R.drawable.ic_unlike);
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
            } else {
                holder.profileImg.setImageResource(R.drawable.profile_image);
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
                holder.toolbar.getMenu().findItem(R.id.edit).setVisible(true);
            } else {
                holder.toolbar.getMenu().findItem(R.id.edit).setVisible(false);
            }
            holder.caption.setVisibility(View.VISIBLE);
            if (post.getCaption() != null) {
                holder.caption.setText(post.getCaption());
            } else {
                if (post.getUserid().equals(currentUserId)) {
                    holder.caption.setVisibility(View.VISIBLE);
                    holder.caption.setText("No Caption");
                } else {
                    holder.caption.setVisibility(View.GONE);
                }
            }

            holder.commentBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.toString().length() == 0 || s.toString().equals(" ")) {
                        holder.btnAddComment.setVisibility(View.GONE);
                    } else {
                        holder.btnAddComment.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().length() == 0 || s.toString().equals(" ")) {
                        holder.btnAddComment.setVisibility(View.GONE);
                    } else {
                        holder.btnAddComment.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            holder.btnAddComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String txtComment = holder.commentBox.getText().toString();
                    String key = commentRef.child(post.getPostid()).push().getKey();
                    CommentModel comment = new CommentModel(currentUserId, txtComment, key, new Timestamp(System.currentTimeMillis()).getTime());
                    commentRef.child(post.getPostid()).child(key).setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                holder.commentBox.setText("");
                            }
                        }
                    });
                }
            });
            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("postid", post.getPostid());
                    context.startActivity(intent);
                }
            });

            holder.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.edit:
                            View view = LayoutInflater.from(context).inflate(R.layout.dialog_post, null);
                            ImageView postImg = view.findViewById(R.id.postImg);
                            final EditText caption = view.findViewById(R.id.txtcaption);
                            if (ChatsAdaptor.isValidContextForGlide(context)) {
                                Glide.with(context)
                                        .asBitmap()
                                        .load(post.getPostlink())
                                        .into(postImg);
                            }
                            caption.setText(post.getCaption());
                            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                    .setView(view)
                                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            post.setCaption(caption.getText().toString());
                                            FirebaseDatabase.getInstance().getReference().child("Post").child(post.getUserid()).child(post.getPostid()).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(context, "Updated...", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                            builder.create().show();
                            break;
                        case R.id.report:
                            final View view1 = LayoutInflater.from(context).inflate(R.layout.report_dialog, null);
                            final RadioGroup radioGroup = view1.findViewById(R.id.radioGrp);
                            final EditText othersBox = view1.findViewById(R.id.othersBox);

                            ((RadioButton) view1.findViewById(R.id.others)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        othersBox.setVisibility(View.VISIBLE);
                                    } else {
                                        othersBox.setVisibility(View.GONE);
                                    }
                                }
                            });


                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context)
                                    .setView(view1)
                                    .setTitle("Report")
                                    .setPositiveButton("Report", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String content = null;
                                            try {
                                                content = ((RadioButton) view1.findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            if (content != null) {
                                                if (content.equals("Others")) {
                                                    if (othersBox.getText() != null) {
                                                        FirebaseDatabase.getInstance().getReference().child("Report").child(post.getUserid()).child(post.getPostid()).child("statement").setValue(othersBox.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(context, "Reported to admin", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        Toast.makeText(context, "Since you checked others statement box cannot be empty", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    FirebaseDatabase.getInstance().getReference().child("Report").child(currentUserId).child(post.getUserid()).child(post.getPostid()).child("statement").setValue(content).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(context, "Reported to admin", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }

                                            } else {
                                                Toast.makeText(context, "Statement is empty ", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                            builder1.create().show();

                            break;
                        default:
                            break;
                    }
                    return true;
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
        private EditText commentBox;
        private ExtendedFloatingActionButton btnAddComment;
        private LinearLayout profile;
        private MaterialToolbar toolbar;

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
            btnAddComment = itemView.findViewById(R.id.btnAddCommemt);
            toolbar = itemView.findViewById(R.id.toolbar);
        }
    }
}

