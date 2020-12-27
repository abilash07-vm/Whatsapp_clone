package com.example.whatsappclone.adaptors;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.Activity.ProfileActivity;
import com.example.whatsappclone.Model.CommentModel;
import com.example.whatsappclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.whatsappclone.settings.FindFriendsActivity.profile_key;

public class CommentAdaptor extends RecyclerView.Adapter<CommentAdaptor.ViewHolder> {
    private static final String TAG = "CommentAdaptor";
    private Context context;
    private ArrayList<CommentModel> comments = new ArrayList<>();

    public CommentAdaptor(Context context) {
        this.context = context;
    }

    public static String getDate(long diff) {
        String date;
        int years = (int) diff / (365 * 24 * 60 * 60), months = (int) diff / (31 * 12 * 24 * 60 * 60), days = (int) diff / (24 * 60 * 60), hours = (int) diff / (60 * 60), min = (int) diff / 60, sec = (int) diff;
        if (years == 1) {
            date = "1 year ago";
        } else if (years > 1) {
            date = years + " years ago";
        } else {
            if (months == 1) {
                date = "1 month ago";
            } else if (months > 1) {
                date = months + " months ago";
            } else {
                if (days == 1) {
                    date = "1 day ago";
                } else if (days > 1) {
                    date = days + " days ago";
                } else {
                    if (hours == 1) {
                        date = "1 hour ago";
                    } else if (hours > 1) {
                        date = hours + " hours ago";
                    } else {
                        if (min >= 1) {
                            date = min + " minute ago";
                        } else {
                            date = sec + " seconds ago";
                        }
                    }
                }
            }
        }
        return date;
    }

    public void setComments(ArrayList<CommentModel> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_model, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentAdaptor.ViewHolder holder, int position) {
        final CommentModel comment = comments.get(position);
        Date d1 = Calendar.getInstance().getTime();
        Date d2 = new Date(comment.getTimestamp());
        long diff = (d1.getTime() - d2.getTime()) / 1000;
        Log.d(TAG, "onBindViewHolder: seconds" + diff);
        Log.d(TAG, "onBindViewHolder: date1" + d1);
        Log.d(TAG, "onBindViewHolder: date2" + d2);
        String date = getDate(diff);

        holder.date.setText(date);
        holder.comment.setText(comment.getComment());
        holder.profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra(profile_key, comment.getUserid());
                context.startActivity(intent);
            }
        });
        FirebaseDatabase.getInstance().getReference().child("User").child(comment.getUserid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("name").exists()) {
                    holder.name.setText(snapshot.child("name").getValue().toString());
                }
                if (ChatsAdaptor.isValidContextForGlide(context) && snapshot.child("image").exists()) {
                    Glide.with(context)
                            .asBitmap()
                            .load(snapshot.getValue())
                            .into(holder.profileImg);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profileImg;
        private TextView comment, date, name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.profile_Image);
            comment = itemView.findViewById(R.id.comment);
            date = itemView.findViewById(R.id.commentdate);
            name = itemView.findViewById(R.id.name);
        }
    }
}
