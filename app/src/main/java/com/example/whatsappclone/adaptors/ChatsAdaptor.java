package com.example.whatsappclone.adaptors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.Activity.PrivateMesaageActivity;
import com.example.whatsappclone.Activity.ProfileActivity;
import com.example.whatsappclone.Model.ChatsModel;
import com.example.whatsappclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.whatsappclone.Activity.PrivateMesaageActivity.message_key;
import static com.example.whatsappclone.settings.FindFriendsActivity.profile_key;

public class ChatsAdaptor extends RecyclerView.Adapter<ChatsAdaptor.ViewHolder> {
    private static final String TAG = "ChatsAdaptor";
    private Context context;
    private ArrayList<ChatsModel> chats = new ArrayList<>();

    public ChatsAdaptor(Context context) {
        this.context = context;
    }

    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }

        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }

    public void setChats(ArrayList<ChatsModel> chats) {
        this.chats = chats;
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
        if (chats.get(position).getMsgcount() > 0) {
            holder.msgCount.setVisibility(View.VISIBLE);
            holder.msgCount.setText(String.valueOf(chats.get(position).getMsgcount()));
            holder.itemView.setBackgroundColor(Color.YELLOW);
        } else {
            holder.msgCount.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.WHITE);
        }
        Log.d(TAG, "onBindViewHolder: " + chats.get(position).toString());
        FirebaseDatabase.getInstance().getReference().child("User").child(chats.get(position).getFrom()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.name.setText(snapshot.child("name").getValue().toString());
                holder.status.setText(snapshot.child("status").getValue().toString());
                if (snapshot.child("image").exists() && isValidContextForGlide(context)) {
                    Glide.with(context)
                            .asBitmap()
                            .load(snapshot.child("image").getValue())
                            .into(holder.image);
                }
                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent1=new Intent(context, ProfileActivity.class);
                        intent1.putExtra(profile_key,chats.get(position).getFrom());
                        context.startActivity(intent1);
                    }
                });
                if (snapshot.child("state").getValue().toString().equals("online"))
                    holder.online.setVisibility(View.VISIBLE);
                else
                    holder.online.setVisibility(View.GONE);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PrivateMesaageActivity.class);
                        intent.putExtra(message_key, chats.get(position).getFrom());
                        Log.d(TAG, "onClick: " + chats.toString());
                        context.startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, status, msgCount;
        public ImageView image, online;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.Name);
            status = itemView.findViewById(R.id.Status);
            image = itemView.findViewById(R.id.Image);
            online = itemView.findViewById(R.id.online);
            msgCount = itemView.findViewById(R.id.msgCount);
        }

    }
}
