package com.example.whatsappclone.settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.Activity.GroupMessageActivity;
import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    private Button btnUpdate;
    private EditText name,status;
    private CircleImageView profileImage;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private String userid;
    public static final int GALLERY_REQUEST_CODE=1;
    private StorageReference fileImgref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initViews();
        userid=firebaseAuth.getUid();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUpdate.setEnabled(false);
                updatedetails();
                btnUpdate.setEnabled(true);
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_REQUEST_CODE);
            }
        });
        reference.child("User").child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    try {
                        if(dataSnapshot.hasChild("name")){
                            name.setText(dataSnapshot.child("name").getValue().toString());
                        }
                        if(dataSnapshot.hasChild("status")){
                            status.setText(dataSnapshot.child("status").getValue().toString());
                        }
                        if(dataSnapshot.hasChild("image")){
                            Glide.with(SettingsActivity.this).asBitmap().load(dataSnapshot.child("image").getValue().toString()).into(profileImage);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST_CODE && resultCode==RESULT_OK && data!=null){
            Uri imgUri=data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                final StorageReference filePath=fileImgref.child(userid+".jpg");
                filePath.putFile(result.getUri()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    reference.child("User").child(userid).child("image").setValue(task.getResult().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(SettingsActivity.this, "Uploaded Sucessfully", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Toast.makeText(SettingsActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });

                        }else{
                            Toast.makeText(SettingsActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private void updatedetails() {

        String userName=name.getText().toString();
        String userStatus=status.getText().toString();
        if(TextUtils.isEmpty(userName)){
            Toast.makeText(SettingsActivity.this,"please enter the user name",Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(userStatus)){
            Toast.makeText(SettingsActivity.this,"please enter your status",Toast.LENGTH_LONG).show();
        }else{
            Hashtable<String,Object> profileMap=new Hashtable<>();
            profileMap.put("uid",userid);
            profileMap.put("name",userName);
            profileMap.put("status",userStatus);
            reference.child("User").child(userid).updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SettingsActivity.this,"Suceesfully updated...",Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(SettingsActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else{
                        Toast.makeText(SettingsActivity.this,task.getException().toString(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        reference.child("User").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("uid")){
                    currentState("online");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(userid!=null){
            currentState("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(userid!=null) {
            currentState("offline");
        }
    }
    public void currentState(String state){
        Calendar calendar=Calendar.getInstance();
        String currentDate,currentTime;
        SimpleDateFormat sdfDate=new SimpleDateFormat("MMM dd yyyy");
        SimpleDateFormat sdfTime=new SimpleDateFormat("hh:mm a");
        currentDate=sdfDate.format(calendar.getTime());
        currentTime=sdfTime.format(calendar.getTime());
        Map<String,Object> stateMap=new HashMap<>();
        stateMap.put("date",currentDate);
        stateMap.put("time",currentTime);
        stateMap.put("state",state);
        reference.child("User").child(userid).updateChildren(stateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete()){
                    Log.d(TAG, "onComplete: welcom back "+userid);
                }
            }
        });
    }

    private void initViews() {
        btnUpdate =findViewById(R.id.btnupdate);
        name=findViewById(R.id.username);
        status=findViewById(R.id.status);
        profileImage=findViewById(R.id.profile_image);
        firebaseAuth=FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference();
        fileImgref= FirebaseStorage.getInstance().getReference().child("Profile Image");
    }
}