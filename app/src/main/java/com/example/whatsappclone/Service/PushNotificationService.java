package com.example.whatsappclone.Service;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.whatsappclone.Activity.GroupMessageActivity;
import com.example.whatsappclone.Activity.PrivateMesaageActivity;
import com.example.whatsappclone.Activity.RequestActivity;
import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.Model.Contact;
import com.example.whatsappclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.net.URL;

import static com.example.whatsappclone.Activity.PrivateMesaageActivity.message_key;

public class PushNotificationService extends FirebaseMessagingService {
    private static final String TAG = "PushNotificationService";
//    NotificationModel notification;
//
//    public PushNotificationService(NotificationModel notification) {
//        this.notification = notification;
//    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived: " + remoteMessage.getData());
        String type = remoteMessage.getData().get("type");
        String senderid = remoteMessage.getData().get("senderid");
        final Contact contact = new Contact();
        Intent intent;
        Log.d(TAG, "onMessageReceived: senderid" + senderid);
        if (type.equals("chat")) {
            intent = new Intent(this, PrivateMesaageActivity.class);
            if (senderid != null) {
                FirebaseDatabase.getInstance().getReference().child("User").child(senderid).child("image").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            contact.setImage(snapshot.getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            intent.putExtra(message_key, senderid);
        } else if (type.equals("grpchat")) {
            String grpid = remoteMessage.getData().get("grpid");
            if (grpid != null) {
                FirebaseDatabase.getInstance().getReference().child("Groups").child(grpid).child("image").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            contact.setImage(snapshot.getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            intent = new Intent(this, GroupMessageActivity.class);
            intent.putExtra("name", grpid);
        } else if (type.equals("request")) {
            intent = new Intent(this, RequestActivity.class);
            if (senderid != null) {
                FirebaseDatabase.getInstance().getReference().child("User").child(senderid).child("image").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            contact.setImage(snapshot.getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        } else {
            intent = new Intent(this, MainActivity.class);

        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, 0);
        URL url;
        Bitmap bitmap;
        try {
            if (contact.getImage() != null) {
                url = new URL(contact.getImage());
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } else if (type.equals("grpchat")) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_grp);
            } else {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile_image);
            }
            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "channel1")
                    .setContentTitle(remoteMessage.getData().get("title"))
                    .setContentText(remoteMessage.getData().get("body"))
                    .setLargeIcon(bitmap)
                    .addAction(R.drawable.icon, "Message", pendingIntent)
                    .setSmallIcon(R.drawable.icon);
            NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
            manager.notify(123, notification.build());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
