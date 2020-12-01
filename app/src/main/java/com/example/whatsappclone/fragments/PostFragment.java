package com.example.whatsappclone.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.Model.CompletePostModel;
import com.example.whatsappclone.Model.Contact;
import com.example.whatsappclone.Model.PostModel;
import com.example.whatsappclone.Model.PostStatus;
import com.example.whatsappclone.R;
import com.example.whatsappclone.adaptors.PostAdaptor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static android.app.Activity.RESULT_OK;
import static com.example.whatsappclone.MainActivity.btn;
import static com.example.whatsappclone.MainActivity.currentState;
import static com.example.whatsappclone.MainActivity.parent;
import static com.example.whatsappclone.settings.SettingsActivity.GALLERY_REQUEST_CODE;
import static com.example.whatsappclone.settings.SettingsActivity.SETTINGS_REQUEST_CODE;
import static com.example.whatsappclone.settings.SettingsActivity.STORAGE_PERMISSION_CODE;

public class PostFragment extends Fragment {
    private static final String TAG = "PostFragment";
    public static Map<String, Contact> contactDetails;
    public static String txtcaption;
    private View view;
    private DatabaseReference postRef, userRef, contactRef;
    private FirebaseAuth auth;
    private RecyclerView postRecyView;
    private FloatingActionButton btnAddPost;
    private String currentUser;
    private StorageReference fileImgRef;
    private Set<String> allContact;
    private ArrayList<CompletePostModel> nonDuplicateposts;
    private ArrayList<String> ids;
    private PostAdaptor adaptor;
    private Intent imgdata;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_post, container, false);
        initViews();
        btnAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        postRecyView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adaptor = new PostAdaptor(getActivity());
        postRecyView.setAdapter(adaptor);
        return view;
    }

    private void openGallery() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent cameraIntent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(cameraIntent, GALLERY_REQUEST_CODE);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission_group.STORAGE)) {
                Snackbar.make(parent, "This Permission is essential to Access Gallery", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Grant", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                                startActivityForResult(intent, SETTINGS_REQUEST_CODE);
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GALLERY_REQUEST_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    Log.d(TAG, "onActivityResult post: cropImage");
                    CropImage.activity()
                            .setAspectRatio(1, 1)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(getActivity(), this);
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "onActivityResult: post uploading...");
                    uploadFileAndUpdateState(imgdata);

                }
                break;
            default:
                break;
        }
    }

    private void uploadFileAndUpdateState(Intent data) {
        final CropImage.ActivityResult result = CropImage.getActivityResult(data);
        userRef.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final PostStatus contact = snapshot.getValue(PostStatus.class);
                contact.setPostcount(contact.getPostcount() + 1);
                if (snapshot.exists()) {
                    Log.d(TAG, "onDataChange: post exist" + contact.toString());
                    final StorageReference fileRef = fileImgRef.child(currentUser + "post" + contact.getPostcount() + ".jpg");
                    fileRef.putFile(result.getUri()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                fileRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "onComplete: " + task.getResult());
                                            final String postkey = postRef.child(currentUser).push().getKey();
                                            PostModel post = new PostModel(currentUser, task.getResult().toString(), null, txtcaption, postkey, false, new Timestamp(System.currentTimeMillis()).getTime(), 0);
                                            postRef.child(currentUser).child(postkey).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Map map = new HashMap();
                                                        map.put("postcount", contact.getPostcount());
                                                        userRef.child(currentUser).updateChildren(map);
                                                        Toast.makeText(getActivity(), "posted", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }

                        }
                    });
                } else {
                    Log.d(TAG, "onDataChange: postcount dont exist" + contact);
                }
                userRef.child(currentUser).removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    @Override
    public void onStart() {
        super.onStart();
        if (currentUser != null) {
            currentState("online");
            btn.setVisibility(View.GONE);
            allContact.add(currentUser);
            contactRef.child(currentUser).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot i : snapshot.getChildren()) {
                        Log.d(TAG, "onChildAdded: " + i.getRef().getKey());
                        allContact.add(i.getRef().getKey());

                    }
                    collectAllPost();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    @Override
    public void onStop() {
        super.onStop();
        nonDuplicateposts.clear();
    }

    private void collectAllPost() {
        for (String i : allContact) {
            userRef.child(i).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Contact contact = snapshot.getValue(Contact.class);
                    Log.d(TAG, "onDataChange: " + contact.toString());
                    contactDetails.put(snapshot.getRef().getKey(), contact);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            postRef.child(i).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    getAllPost(snapshot, "childAdded");
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    getAllPost(snapshot, "childChange");
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void getAllPost(DataSnapshot snapshot, String type) {
        try {
            CompletePostModel model = snapshot.getValue(CompletePostModel.class);
            model.addUserdata(contactDetails.get(model.getUserid()));
            Log.d(TAG, "getAllPost: " + type + "  " + model.toString());
            for (CompletePostModel i : nonDuplicateposts) {
                if (i.getPostid().equals(model.getPostid())) {
                    nonDuplicateposts.remove(i);
                    break;
                }
            }
            if (!nonDuplicateposts.contains(model)) {
                nonDuplicateposts.add(model);
            }
            Collections.sort(nonDuplicateposts, new Comparator<CompletePostModel>() {
                @Override
                public int compare(CompletePostModel o1, CompletePostModel o2) {
                    return (int) (o2.getTimestamp() - o1.getTimestamp());
                }
            });
            adaptor.setPosts(nonDuplicateposts);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        btn.setVisibility(View.VISIBLE);
        if (currentUser != null)
            currentState("offline");
    }

    private void initViews() {
        userRef = FirebaseDatabase.getInstance().getReference().child("User");
        postRef = FirebaseDatabase.getInstance().getReference().child("Post");
        contactRef = FirebaseDatabase.getInstance().getReference().child("Contact");
        allContact = new HashSet<>();
        nonDuplicateposts = new ArrayList<>();
        ids = new ArrayList<>();
        contactDetails = new HashMap<>();
        auth = FirebaseAuth.getInstance();
        postRecyView = view.findViewById(R.id.postRecyView);
        btnAddPost = view.findViewById(R.id.addPost);
        if (auth.getCurrentUser() != null) {
            currentUser = auth.getCurrentUser().getUid();
            fileImgRef = FirebaseStorage.getInstance().getReference().child("Post");
        }
    }
}

