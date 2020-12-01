package com.example.whatsappclone.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.Model.Contact;
import com.example.whatsappclone.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.whatsappclone.adaptors.ChatsAdaptor.isValidContextForGlide;

public class CommentsAdaptor extends RecyclerView.Adapter<CommentsAdaptor.ViewHolder> {
    private ArrayList<Contact> contacts = new ArrayList<>();
    private Context context;

    public CommentsAdaptor(Context context) {
        this.context = context;
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_model, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.profileName.setText(contact.getName());
//        holder.profileStatus.setText();
        if (isValidContextForGlide(context) && contact.getImage() != null) {
            Glide.with(context)
                    .asBitmap()
                    .load(contact.getImage())
                    .into(holder.profileImage);
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profileImage;
        private TextView profileName, profileStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.Image);
            profileName = itemView.findViewById(R.id.Name);
            profileStatus = itemView.findViewById(R.id.Status);
        }
    }
}
