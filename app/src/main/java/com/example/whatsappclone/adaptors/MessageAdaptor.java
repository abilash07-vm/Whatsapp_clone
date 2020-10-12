package com.example.whatsappclone.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.Model.MessageModel;
import com.example.whatsappclone.R;

import java.util.ArrayList;

public class MessageAdaptor extends RecyclerView.Adapter<MessageAdaptor.ViewHolder> {
    ArrayList<MessageModel> messages=new ArrayList<>();
    Context context;

    public MessageAdaptor(Context context) {
        this.context = context;
    }

    public void setMessages(ArrayList<MessageModel> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_model,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.messagerName.setText(messages.get(position).getSender());
        holder.message.setText(messages.get(position).getMessage());
        holder.time.setText(messages.get(position).getTime());
        holder.date.setText(messages.get(position).getDate());
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView messagerName,message,date,time;
        private CardView parent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent=itemView.findViewById(R.id.messageCardView);
            messagerName=itemView.findViewById(R.id.messagerName);
            message=itemView.findViewById(R.id.message);
            date=itemView.findViewById(R.id.messageDate);
            time=itemView.findViewById(R.id.messageTime);
        }
    }
}
