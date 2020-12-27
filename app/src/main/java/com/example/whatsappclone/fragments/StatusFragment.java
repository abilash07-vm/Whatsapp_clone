package com.example.whatsappclone.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.AlertDialog.ProgressBar;
import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.Model.Contact;
import com.example.whatsappclone.Model.StatusModel;
import com.example.whatsappclone.R;
import com.example.whatsappclone.adaptors.ChatsAdaptor;
import com.example.whatsappclone.adaptors.CommentAdaptor;
import com.example.whatsappclone.adaptors.StatusAdaptor;
import com.example.whatsappclone.settings.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.example.whatsappclone.MainActivity.count;
import static com.example.whatsappclone.MainActivity.parent;
import static com.example.whatsappclone.fragments.PostFragment.loadFromUri;
import static com.example.whatsappclone.settings.SettingsActivity.SETTINGS_REQUEST_CODE;

public class StatusFragment extends Fragment {
    private static final String TAG = "StatusFragment";
    public static TextView txtNewStatus, txtSeenStatus;
    public static Map<String, ArrayList<StatusModel>> statusMap;
    private static String currentUserId;
    private static DatabaseReference statusRef;
    private static ArrayList<StatusModel> newStatusList;
    private static ArrayList<StatusModel> seenStatusList;
    private static StatusAdaptor newStatusAdaptor;
    private static StatusAdaptor seenStatusAdaptor;
    private RecyclerView newstatusRecyView, seenStatusRecyView;
    private FloatingActionButton addStatus;
    private StorageReference fileImgRef;
    private String txtcaption;
    private ProgressBar progressBar;
    private Uri uri;
    private View view;

    public static void GetAllStatus() {
        for (String i : PostFragment.contactDetails.keySet()) {
            if (i != null) {
                Log.d(TAG, "GetAllStatus: " + i);
                statusRef.child(i).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        getAllStatus(snapshot);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
    }

    private static void getAllStatus(DataSnapshot snapshot) {
        if (snapshot.exists()) {
            StatusModel status = snapshot.getValue(StatusModel.class);
            if (status != null) {
                Date d1 = Calendar.getInstance().getTime();
                Date d2 = new Date(status.getTimestamp());
                long diff = (d1.getTime() - d2.getTime()) / 1000;
                if (diff >= 24 * 60 * 60) {
                    statusRef.child(status.getUserid()).child(status.getStatusid()).removeValue();
                    GetAllStatus();
                    return;
                }
            }
            ArrayList<StatusModel> statuses = statusMap.get(status.getUserid());

            if (statuses == null) {
                statuses = new ArrayList<>();
            }
            if (statuses.contains(status)) {
                return;
            }
            removeRepetedByStatusId(statuses, status.getStatusid());
            statuses.add(status);

            if (isSeen(status.getSeenBy(), currentUserId)) {
                removeRepetedByUserId(seenStatusList, status.getUserid());
                removeRepetedByUserId(newStatusList, status.getUserid());
                seenStatusList.add(status);
            } else {
                removeRepetedByUserId(seenStatusList, status.getUserid());
                removeRepetedByUserId(newStatusList, status.getUserid());
                newStatusList.add(status);
            }
            statusMap.put(status.getUserid(), statuses);


            Collections.sort(newStatusList, new Comparator<StatusModel>() {
                @Override
                public int compare(StatusModel o1, StatusModel o2) {
                    return (int) (o2.getTimestamp() - o1.getTimestamp());
                }
            });
            Collections.sort(seenStatusList, new Comparator<StatusModel>() {
                @Override
                public int compare(StatusModel o1, StatusModel o2) {
                    return (int) (o2.getTimestamp() - o1.getTimestamp());
                }
            });
            Collections.sort(statusMap.get(status.getUserid()), new Comparator<StatusModel>() {
                @Override
                public int compare(StatusModel o1, StatusModel o2) {
                    return (int) (o1.getTimestamp() - o2.getTimestamp());
                }
            });


            Log.d(TAG, "getAllStatus: new " + newStatusList);
            Log.d(TAG, "getAllStatus: seen " + seenStatusList);
            if (newStatusList.size() > 0) {
                txtNewStatus.setVisibility(View.VISIBLE);
            } else {
                txtNewStatus.setVisibility(View.GONE);
            }
            if (seenStatusList.size() > 0) {
                txtSeenStatus.setVisibility(View.VISIBLE);
            } else {
                txtSeenStatus.setVisibility(View.GONE);
            }
            newStatusAdaptor.setStatus(newStatusList);
            seenStatusAdaptor.setStatus(seenStatusList);
        }
    }

    public static boolean isSeen(String seenBy, String id) {
        if (seenBy == null)
            return false;
        for (String i : seenBy.split(",")) {
            if (i.equals(id)) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<StatusModel> removeRepetedByUserId(ArrayList<StatusModel> statuses, String id) {
        try {
            for (StatusModel i : statuses) {
                if (i.getUserid().equals(id)) {
                    statuses.remove(i);
                    Log.d(TAG, "removeRepetedByUserId: removed " + i.getCaption());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return statuses;
    }

    public static ArrayList<StatusModel> removeRepetedByStatusId(ArrayList<StatusModel> statuses, String id) {
        try {
            for (StatusModel i : statuses) {
                if (i.getStatusid().equals(id)) {
                    statuses.remove(i);
                    Log.d(TAG, "removeRepetedByStatusId: removed " + i.getCaption());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statuses;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_status, container, false);
        initViews();
        return view;
    }

    private void initViews() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentUserId = user.getUid();
            txtNewStatus = view.findViewById(R.id.txtNewStatus);
            txtSeenStatus = view.findViewById(R.id.txtSeenStatus);
            newstatusRecyView = view.findViewById(R.id.newStatusRecyView);
            seenStatusRecyView = view.findViewById(R.id.seenStatusRecyView);
            addStatus = view.findViewById(R.id.addStatus);
            fileImgRef = FirebaseStorage.getInstance().getReference().child("Status");
            statusRef = FirebaseDatabase.getInstance().getReference().child("Status");
            progressBar = new ProgressBar();
            statusMap = new HashMap<>();
            newStatusAdaptor = new StatusAdaptor(getActivity(), "new");
            newstatusRecyView.setAdapter(newStatusAdaptor);
            newstatusRecyView.setLayoutManager(new LinearLayoutManager(getActivity()));
            seenStatusAdaptor = new StatusAdaptor(getActivity(), "seen");
            seenStatusRecyView.setAdapter(seenStatusAdaptor);
            seenStatusRecyView.setLayoutManager(new LinearLayoutManager(getActivity()));
            newStatusList = new ArrayList<>();
            seenStatusList = new ArrayList<>();
            addStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGalery();
                }
            });
        }
    }

    private void openGalery() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, SettingsActivity.GALLERY_REQUEST_CODE);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showSnackBar();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, SettingsActivity.STORAGE_PERMISSION_CODE);
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case SettingsActivity.GALLERY_REQUEST_CODE:
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(getActivity(), this);
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_post, null);
                    ImageView postImg = view.findViewById(R.id.postImg);
                    final EditText caption = view.findViewById(R.id.txtcaption);
                    final CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    uri = result.getUri();
                    postImg.setImageBitmap(loadFromUri(uri, getActivity()));
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                            .setView(view)
                            .setPositiveButton("Post", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    txtcaption = caption.getText().toString();
                                    uploadFileAndUpdateState();
                                }
                            });
                    builder.create().show();
                }
                break;
            default:
                break;
        }
    }

    private void uploadFileAndUpdateState() {
        progressBar.show(getActivity().getSupportFragmentManager(), "status");
        final String postkey = statusRef.child(currentUserId).push().getKey();
        final StorageReference fileRef = fileImgRef.child(currentUserId + "status" + postkey + ".jpg");
        fileRef.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    fileRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: " + task.getResult());
                                StatusModel post = new StatusModel(currentUserId, task.getResult().toString(), null, txtcaption, postkey, new Timestamp(System.currentTimeMillis()).getTime(), 0);
                                statusRef.child(currentUserId).child(postkey).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity(), "uploded", Toast.LENGTH_SHORT).show();
                                            progressBar.dismiss();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private void showSnackBar() {
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
    }

    @Override
    public void onStart() {
        super.onStart();
        if (currentUserId != null) {
            if (count == 0) {
                count = 1;
                MainActivity.tabs.getTabAt(0).select();
            } else {
                GetAllStatus();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (currentUserId != null) {
            newStatusList.clear();
            statusMap.clear();
            seenStatusList.clear();
        }
    }

    public static class StatusAsyncTask extends AsyncTask<Void, StatusModel, Boolean> {
        private Context context;
        private CircleImageView profileImg;
        private ImageView statusImg;
        private TextView name, date, statusCount, caption;
        private android.widget.ProgressBar progressBar;
        private View view;
        private String userid, type;
        private AlertDialog builder;

        public StatusAsyncTask(Context context, String userid, String type) {
            this.context = context;
            this.userid = userid;
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute: status view started");
            view = LayoutInflater.from(context).inflate(R.layout.status_model, null);
            profileImg = view.findViewById(R.id.profile_Image);
            statusImg = view.findViewById(R.id.statusimg);
            progressBar = view.findViewById(R.id.progressStatus);
            name = view.findViewById(R.id.name);
            date = view.findViewById(R.id.date);
            statusCount = view.findViewById(R.id.statusCount);
            caption = view.findViewById(R.id.caption);
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
                    .setView(view).create();
            builder.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            ArrayList<StatusModel> allStatusOfUser = statusMap.get(userid);
            Log.d(TAG, "doInBackground: " + allStatusOfUser);
            for (int status = 0; status < allStatusOfUser.size(); status++) {
                if ((type.equals("new") && isSeen(allStatusOfUser.get(status).getSeenBy(), currentUserId)) || (type.equals("seen") && !isSeen(allStatusOfUser.get(status).getSeenBy(), currentUserId))) {
                    continue;
                }
                for (int i = 0; i <= 100; i += 20) {
                    Log.d(TAG, "onPreExecute: status viewing");
                    publishProgress(allStatusOfUser.get(status), new StatusModel(null, null, null, null, null, 0, allStatusOfUser.size() - status - 1), new StatusModel(null, null, null, null, null, 0, i));
                    SystemClock.sleep(1000);
                }

            }
            return true;
        }

        @Override
        protected void onProgressUpdate(StatusModel... values) {
            super.onProgressUpdate(values);

            StatusModel status = values[0];
            int count = values[1].getViewCount();
            int progressValue = values[2].getViewCount();

            String s = "";
            Set<String> seenSet = new HashSet<>();
            try {
                seenSet.addAll(Arrays.asList(status.getSeenBy().split(",")));
                int z = 0;
                for (String i : seenSet) {
                    s += i;
                    if (z != seenSet.size() - 1) {
                        s += ",";
                    }
                    z++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!seenSet.contains(currentUserId)) {
                status.setViewCount(status.getViewCount() + 1);
                s += currentUserId;
            }
            status.setSeenBy(s);

            statusRef.child(status.getUserid()).child(status.getStatusid()).setValue(status);

            Contact contact = PostFragment.contactDetails.get(status.getUserid());
            name.setText(contact.getName());
            statusCount.setText(" " + count + " ");
            progressBar.setProgress(progressValue);
            if (ChatsAdaptor.isValidContextForGlide(context)) {
                if (status.getStatuslink() != null) {
                    Glide.with(context)
                            .asBitmap()
                            .load(status.getStatuslink())
                            .into(statusImg);
                }
                if (contact.getImage() != null) {
                    Glide.with(context)
                            .asBitmap()
                            .load(contact.getImage())
                            .into(profileImg);
                }
            }
            Date d1 = Calendar.getInstance().getTime();
            Date d2 = new Date(status.getTimestamp());
            long diff = (d1.getTime() - d2.getTime()) / 1000;
            date.setText(CommentAdaptor.getDate(diff));
            if (status.getCaption() != null) {
                caption.setText(status.getCaption());
                caption.setVisibility(View.VISIBLE);
            } else {
                caption.setVisibility(View.GONE);
            }

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            this.onCancelled();
            builder.dismiss();
            StatusFragment.statusMap.clear();
            StatusFragment.GetAllStatus();
        }
    }
}
