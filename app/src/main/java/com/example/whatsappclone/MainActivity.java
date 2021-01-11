package com.example.whatsappclone;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.whatsappclone.Activity.ContactActivity;
import com.example.whatsappclone.Activity.RequestActivity;
import com.example.whatsappclone.Model.GroupChatModel;
import com.example.whatsappclone.loginandsignup.LoginActivity;
import com.example.whatsappclone.settings.BrowserActivity;
import com.example.whatsappclone.settings.FindFriendsActivity;
import com.example.whatsappclone.settings.SettingsActivity;
import com.example.whatsappclone.ui.main.Adapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.whatsappclone.settings.SettingsActivity.STORAGE_PERMISSION_CODE;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseUser currentUser;
    private Adapter adapter;
    private ViewPager viewPager;
    public static TabLayout tabs;
    public static int count = 0;
    private static String currentUserid;
    private FirebaseAuth firebaseAuth;
    private MaterialToolbar toolbar;
    private DatabaseReference rootRef;
    public static CoordinatorLayout parent;
    public static TextView postcount, chatcount, statuscount, grpcount, title1, title2, title3, title4;
    private TabItem postTab, chatTab, statusTab, groupTab;

    public static void currentState(String state) {
        Calendar calendar = Calendar.getInstance();
        String currentDate, currentTime;
        SimpleDateFormat sdfDate = new SimpleDateFormat("MMM dd yyyy");
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");
        currentDate = sdfDate.format(calendar.getTime());
        currentTime = sdfTime.format(calendar.getTime());
        Map<String, Object> stateMap = new HashMap<>();
        stateMap.put("date", currentDate);
        stateMap.put("time", currentTime);
        stateMap.put("state", state);
        try {
            FirebaseDatabase.getInstance().getReference().child("User").child(currentUserid).updateChildren(stateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isComplete()) {
                        Log.d(TAG, "onComplete: status updated");
                    }
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAdd = InetAddress.getByName("google.com");
            return !ipAdd.equals("");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);

        setValuesToTab();

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.settings:
                        Intent intent1 = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.signout:
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Sign Out")
                                .setMessage("Are you sure to sign out?")
                                .setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        currentState("offline");
                                        firebaseAuth.signOut();
                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    }
                                })
                                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        builder.create().show();

                        break;
                    case R.id.newGroup:
                        newGroup();
                        break;
                    case R.id.findFriends:
                        final Intent intent = new Intent(MainActivity.this, FindFriendsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.aboutUs:
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("About Us")
                                .setMessage("For more Details visit my website")
                                .setPositiveButton("Visit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent2 = new Intent(MainActivity.this, BrowserActivity.class);
                                        intent2.putExtra("url", "https://abilash-2k20.web.app/");
                                        startActivity(intent2);
                                    }
                                });
                        builder1.create().show();
                        break;
                    case R.id.feedback:
                        final EditText feedback = new EditText(MainActivity.this);
                        feedback.setGravity(Gravity.CENTER);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Add a new task")
                                .setMessage("What do you want to do next?")
                                .setView(feedback)
                                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final String txtfeedback = String.valueOf(feedback.getText());
                                        rootRef.child("Feedbacks").child(currentUser.getUid()).child("feedback").setValue(txtfeedback).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Map<String, String> notificationBody = new HashMap<>();
                                                    notificationBody.put("from", currentUser.getUid());
                                                    notificationBody.put("message", txtfeedback);
                                                    notificationBody.put("type", "message");
                                                    String admin = "mcUF8n2NwANwPEp1DzSuyFiDTCr1";
                                                    FirebaseDatabase.getInstance().getReference().child("FeedbackNotifications").child(admin).push().setValue(notificationBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Snackbar.make(parent, "Thank You for Your Valuable FeedBack...", Snackbar.LENGTH_LONG)
                                                                        .show();
                                                            }
                                                        }
                                                    });

                                                }
                                            }
                                        });

                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        dialog.create().show();
                        break;
                    case R.id.request:
                        Intent intent2 = new Intent(MainActivity.this, RequestActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.contact:
                        Intent intent3 = new Intent(MainActivity.this, ContactActivity.class);
                        startActivity(intent3);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        if (currentUserid != null) {
            rootRef.child("ChatRequest").child(currentUserid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long count = snapshot.getChildrenCount();
                    if (count == 0) {
                        toolbar.getMenu().findItem(R.id.request).setIcon(R.drawable.ic_request);
                    } else {
                        toolbar.getMenu().findItem(R.id.request).setIcon(R.drawable.ic_req_background);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }

    private void setValuesToTab() {
        View view = LayoutInflater.from(MainActivity.this)
                .inflate(R.layout.customtabs, null, false);
        View view1 = LayoutInflater.from(MainActivity.this)
                .inflate(R.layout.customtabs, null, false);
        View view2 = LayoutInflater.from(MainActivity.this)
                .inflate(R.layout.customtabs, null, false);
        View view3 = LayoutInflater.from(MainActivity.this)
                .inflate(R.layout.customtabs, null, false);
        tabs.getTabAt(0).setCustomView(view);
        tabs.getTabAt(1).setCustomView(view1);
        tabs.getTabAt(2).setCustomView(view2).select();
        tabs.getTabAt(3).setCustomView(view3);
        postcount = tabs.getTabAt(0).getCustomView().findViewById(R.id.count);
        chatcount = tabs.getTabAt(1).getCustomView().findViewById(R.id.count);
        statuscount = tabs.getTabAt(2).getCustomView().findViewById(R.id.count);
        grpcount = tabs.getTabAt(3).getCustomView().findViewById(R.id.count);
        title1 = tabs.getTabAt(0).getCustomView().findViewById(R.id.text);
        title2 = tabs.getTabAt(1).getCustomView().findViewById(R.id.text);
        title3 = tabs.getTabAt(2).getCustomView().findViewById(R.id.text);
        title4 = tabs.getTabAt(3).getCustomView().findViewById(R.id.text);
        title1.setText("Post");
        title2.setText("Chat");
        title3.setText("Status");
        title4.setText("Group");
        statuscount.setVisibility(View.GONE);
        chatcount.setVisibility(View.GONE);
        grpcount.setVisibility(View.GONE);
        postcount.setText("New");

    }

    private void newGroup() {
        final EditText grpName = new EditText(MainActivity.this);
        grpName.setGravity(Gravity.CENTER);
        grpName.setHint("Group Name");
        grpName.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle("New Group")
                .setView(grpName)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(grpName.equals("") || grpName==null){
                            Toast.makeText(MainActivity.this, "Group Name Cannot be Null", Toast.LENGTH_SHORT).show();
                        }else {
                            final String txtGroupName = grpName.getText().toString();
                            final String key = rootRef.child("Groups").push().getKey();
                            rootRef.child("Groups").child(key).setValue("")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                rootRef.child("Groups").child(key).child("Members").child(currentUser.getUid()).child("type").setValue("admin").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            rootRef.child("Groups").child(key).child("grpname").setValue(txtGroupName);
                                                            GroupChatModel grpmodel = new GroupChatModel("admin", new Timestamp(System.currentTimeMillis()).getTime(), null, txtGroupName, "", key, 0);
                                                            rootRef.child("UserGroup").child(currentUser.getUid()).child(key).setValue(grpmodel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(MainActivity.this, "Creted Sucessfully", Toast.LENGTH_LONG).show();
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
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }

    private void initViews() {
        adapter = new Adapter(this, getSupportFragmentManager());
        viewPager = findViewById(R.id.container);
        tabs = findViewById(R.id.tabs);
        parent = findViewById(R.id.parent);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        postTab = findViewById(R.id.post);
        chatTab = findViewById(R.id.chats);
        statusTab = findViewById(R.id.status);
        groupTab = findViewById(R.id.group);

        rootRef = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (currentUser != null) {
            currentUserid = currentUser.getUid();
            currentState("offline");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (currentUser != null) {
            currentState("online");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentUser != null)
            currentState("offline");
    }


    @Override
    protected void onStart() {
        try {
            super.onStart();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (currentUser == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            currentState("online");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission_group.STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission_group.STORAGE}, STORAGE_PERMISSION_CODE);
            }
            String currUser = firebaseAuth.getUid();
            rootRef.child("User").child(currUser).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if ((dataSnapshot.child("name").exists())) {
                        String name = dataSnapshot.child("name").getValue().toString();

                    } else {
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
}