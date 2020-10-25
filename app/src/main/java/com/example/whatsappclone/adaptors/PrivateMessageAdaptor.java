package com.example.whatsappclone.adaptors;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.Model.PrivateMessageModel;
import com.example.whatsappclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import static com.example.whatsappclone.adaptors.ChatsAdaptor.isValidContextForGlide;

public class PrivateMessageAdaptor extends RecyclerView.Adapter<PrivateMessageAdaptor.ViewHolder> {
    private static final String TAG = "PrivateMessageAdaptor";
    ArrayList<PrivateMessageModel> messages = new ArrayList<>();
    Context context;
    String currUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User");

    public PrivateMessageAdaptor(Context context) {
        this.context = context;
    }

    public void setMessages(ArrayList<PrivateMessageModel> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_private_model, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final PrivateMessageModel message = messages.get(position);
        Log.d(TAG, "onBindViewHolder: " + message.getFrom());
        if (!message.getFrom().equals(currUser)) {
            holder.receiverImg.setVisibility(View.VISIBLE);
            holder.receivermsg.setVisibility(View.VISIBLE);
            holder.sendermsg.setVisibility(View.GONE);
            holder.receivermsg.setText(message.getMessage());

            userRef.child(message.getFrom()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.hasChild("image") && isValidContextForGlide(context)) {
                        Glide.with(context)
                                .asBitmap()
                                .load(snapshot.child("image").getValue())
                                .into(holder.receiverImg);
                        Log.d(TAG, "onDataChange: " + snapshot.child("image"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            holder.sendermsg.setVisibility(View.VISIBLE);
            holder.receiverImg.setVisibility(View.GONE);
            holder.receivermsg.setVisibility(View.GONE);
            holder.sendermsg.setText(message.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView receivermsg, sendermsg;
        private CircleImageView receiverImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            receivermsg = itemView.findViewById(R.id.receiver_msg);
            sendermsg = itemView.findViewById(R.id.sender_msg);
            receiverImg = itemView.findViewById(R.id.receiver_img);
        }
    }
}

