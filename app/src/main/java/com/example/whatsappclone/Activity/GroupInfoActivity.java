package com.example.whatsappclone.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.Model.Contact;
import com.example.whatsappclone.Model.GroupChatModel;
import com.example.whatsappclone.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
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
import static com.example.whatsappclone.adaptors.ChatsAdaptor.isValidContextForGlide;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.whatsappclone.MainActivity.currentState;
import static com.example.whatsappclone.settings.SettingsActivity.GALLERY_REQUEST_CODE;
import static com.example.whatsappclone.settings.SettingsActivity.SETTINGS_REQUEST_CODE;
import static com.example.whatsappclone.settings.SettingsActivity.STORAGE_PERMISSION_CODE;

public class GroupInfoActivity extends AppCompatActivity {
    public static final String grpName_key = "key";
    private static final String TAG = "GroupInfoActivity";
    private CircleImageView grpIcon;
    private TextView grpName;
    private Button btnAddMember;
    private RecyclerView memberRecyView, contactRecyView;
    private DatabaseReference grpRef, grpmemberRef, userGrpRef, contactRef, userRef;
    private StorageReference fileImgref;
    private String key, userid;
    private MaterialCardView AddMemberCardView;
    private RelativeLayout parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);


        Intent intent = getIntent();
        if (intent != null) {
            key = intent.getStringExtra(grpName_key);
            initViews();
            grpIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGallery();
                }
            });
            grpmemberRef.child(userid).child("type").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.getValue().toString().equals("admin")) {
                            btnAddMember.setVisibility(View.VISIBLE);
                        } else {
                            btnAddMember.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            btnAddMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getAllContact();
                }
            });
        }

    }

    public void openGallery() {
        if (ActivityCompat.checkSelfPermission(GroupInfoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GroupInfoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(GroupInfoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(GroupInfoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Snackbar.make(parent, "Storage Permission is Required for this feature", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Grant", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivityForResult(intent, SETTINGS_REQUEST_CODE);
                            }
                        });
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GALLERY_REQUEST_CODE:
                if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
                    Uri imgUri = data.getData();
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(this);

                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        final StorageReference filePath = fileImgref.child(grpName + ".jpg");
                        filePath.putFile(result.getUri()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            grpRef.child("image").setValue(task.getResult().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(GroupInfoActivity.this, "Uploaded Sucessfully", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(GroupInfoActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    });

                                } else {
                                    Toast.makeText(GroupInfoActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
                break;
            case SETTINGS_REQUEST_CODE:
                openGallery();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_PERMISSION_CODE:
                openGallery();
                break;
            default:
                break;

        }
    }

    private void getAllContact() {
        FirebaseRecyclerOptions<Contact> options = new FirebaseRecyclerOptions.Builder<Contact>()
                .setQuery(contactRef, Contact.class)
                .build();
        FirebaseRecyclerAdapter<Contact, ContactActivity.contactViewHolder> adapter = new FirebaseRecyclerAdapter<Contact, ContactActivity.contactViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactActivity.contactViewHolder holder, int position, @NonNull final Contact model) {
                final String key = getRef(position).getKey();
                Log.d(TAG, "onBindViewHolder: " + key);
                try {
                    userRef.child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                if (snapshot.hasChild("name"))
                                    holder.name.setText(snapshot.child("name").getValue().toString());
                                if (snapshot.hasChild("status"))
                                    holder.status.setText(snapshot.child("status").getValue().toString());
                                if (snapshot.hasChild("image") && isValidContextForGlide(GroupInfoActivity.this)) {
                                    Glide.with(GroupInfoActivity.this)
                                            .asBitmap()
                                            .load(snapshot.child("image").getValue().toString())
                                            .into(holder.image);
                                }
                                grpmemberRef.child(key).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.hasChild("type")) {
                                            holder.itemView.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final Map<String, Object> stateMap = new HashMap<>();
                                        stateMap.put("type","member");
                                        stateMap.put("timestamp", new Timestamp(System.currentTimeMillis()).getTime());
                                        stateMap.put("name", grpName.getText().toString());
                                        stateMap.put("imglink", null);
                                        stateMap.put("msgcount",0);
                                        userGrpRef.child(key).child(grpName.getText().toString()).updateChildren(stateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    grpmemberRef.child(key).child("type").setValue("member").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(GroupInfoActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                                                                AddMemberCardView.setVisibility(View.GONE);
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @NonNull
            @Override
            public ContactActivity.contactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_model, parent, false);
                return new ContactActivity.contactViewHolder(view);
            }
        };

        contactRecyView.setLayoutManager(new LinearLayoutManager(GroupInfoActivity.this));
        contactRecyView.setAdapter(adapter);
        adapter.startListening();
        AddMemberCardView.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (key != null) {
            currentState("online");
            grpName.setText(key);
            grpRef.child("image").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Glide.with(getApplicationContext())
                                .asBitmap()
                                .load(snapshot.getValue())
                                .into(grpIcon);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            FirebaseRecyclerOptions<GroupChatModel> options = new FirebaseRecyclerOptions.Builder<GroupChatModel>()
                    .setQuery(grpmemberRef, GroupChatModel.class)
                    .build();
            FirebaseRecyclerAdapter<GroupChatModel, MemberViewHolder> adapter = new FirebaseRecyclerAdapter<GroupChatModel, MemberViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final MemberViewHolder holder, int position, @NonNull final GroupChatModel model) {
                    Log.d(TAG, "onBindViewHolder: " + getRef(position).getKey());
                    try {
                        userRef.child(getRef(position).getKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try {
                                    if (snapshot.exists()) {
                                        if (snapshot.hasChild("name"))
                                            holder.userName.setText(snapshot.child("name").getValue().toString());
                                        if (snapshot.hasChild("status"))
                                            holder.userStatus.setText(snapshot.child("status").getValue().toString());
                                        if (snapshot.child("image").getValue() != null) {
                                            Glide.with(getApplicationContext())
                                                    .asBitmap()
                                                    .load(snapshot.child("image").getValue().toString())
                                                    .into(holder.userImg);
                                        }
                                        if (model.getType().equals("admin")) {
                                            holder.isadmin.setVisibility(View.VISIBLE);
                                        } else {
                                            holder.isadmin.setVisibility(View.GONE);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @NonNull
                @Override
                public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(GroupInfoActivity.this)
                            .inflate(R.layout.friends_model, parent, false);
                    return new MemberViewHolder(view);
                }
            };
            memberRecyView.setLayoutManager(new LinearLayoutManager(GroupInfoActivity.this));
            memberRecyView.setAdapter(adapter);
            adapter.startListening();
        }
    }

    private void initViews() {
        parent = findViewById(R.id.parent);
        grpIcon = findViewById(R.id.grpImg);
        grpName = findViewById(R.id.grpName);
        btnAddMember = findViewById(R.id.btnAddMember);
        memberRecyView = findViewById(R.id.memberRecyView);
        grpRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(key);
        userRef = FirebaseDatabase.getInstance().getReference().child("User");
        userGrpRef = FirebaseDatabase.getInstance().getReference().child("UserGroup");
        contactRef = FirebaseDatabase.getInstance().getReference().child("Contact");
        contactRecyView = findViewById(R.id.addContactRecyView);
        grpmemberRef = grpRef.child("Members");
        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        AddMemberCardView = findViewById(R.id.secondCardView);
        fileImgref = FirebaseStorage.getInstance().getReference().child("Profile Image");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (userid != null) {
            currentState("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.with(getApplicationContext()).pauseRequests();
        if (userid != null) {
            currentState("offline");
        }
    }


    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView userImg;
        private TextView userName, userStatus, isadmin;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            userImg = itemView.findViewById(R.id.Image);
            userName = itemView.findViewById(R.id.Name);
            userStatus = itemView.findViewById(R.id.Status);
            isadmin = itemView.findViewById(R.id.Admin);
        }
    }
}