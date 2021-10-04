package com.tsproject.enchat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tsproject.enchat.Adapter.RecentAdapter;
import com.tsproject.enchat.R;
import com.tsproject.enchat.Model.User;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navView;
    ArrayList<User> recentList;
    RecyclerView rvRecent;
    RecentAdapter adapter;
    String uID;

    public static String Name = "";
    public static String dpUrl = "";
    public static String phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Fresco.initialize(this);
        EmojiManager.install(new GoogleEmojiProvider());

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navView = findViewById(R.id.navView);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open,
                R.string.close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.btnHome:
                        loadHomeActivity();
                        break;
                    case R.id.btnContacts:
                        loadFindUserActivity();
                        break;
                    case R.id.btnProfile:
                        loadProfileActivity();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        initRecyclerView();
        findViewById(R.id.cvAddGroup).setOnClickListener(view -> CreateGroupOnClick());

        FirebaseDatabase.getInstance().getReference().child("user").child(uID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (dpUrl.equals("")) {
                    Name = snapshot.child("userName").getValue().toString();
                    dpUrl = snapshot.child("imageURL").getValue().toString();
                    phoneNumber = snapshot.child("phnNum").getValue().toString();
                }

                TextView tvUsername = findViewById(R.id.tvUsername);
                TextView tvUserNumber = findViewById(R.id.tvUserNumber);
                ImageView profilePicture = findViewById(R.id.ivUserImage);

                tvUsername.setText(Name);
                tvUserNumber.setText(phoneNumber);
                Glide.with(MainActivity.this)
                        .load(dpUrl)
                        .into(profilePicture);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        findViewById(R.id.cvAddGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MemberSelectionActivity.class));
            }
        });

    }

    private void CreateGroupOnClick() {

    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void loadProfileActivity() {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);

    }

    private void loadFindUserActivity() {
        Intent intent = new Intent(MainActivity.this, FindUserActivity.class);
        startActivity(intent);
    }

    private void loadHomeActivity() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void initRecyclerView() {
        rvRecent = findViewById(R.id.rvRecent);
        recentList = new ArrayList<>();
        adapter = new RecentAdapter();
        adapter.setContext(this);
        adapter.setRecentList(recentList);
        rvRecent.setLayoutManager((new LinearLayoutManager(this)));
        rvRecent.setAdapter(adapter);
        FirebaseDatabase.getInstance()
                .getReference()
                .child("chat")
                .orderByChild(uID)
                .equalTo(0)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                User user = new User();
                                user.setChatID(snap.getKey());

                                long type = (long) snap.child("type").getValue();
                                List<String> members = (List<String>) snap.child("members").getValue();
                                if (type == 0) {
                                    String targetUID = members.get(0).equals(uID) ? members.get(1) : members.get(0);
                                    FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("user")
                                            .child(targetUID)
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        user.setUserName(snapshot.child("userName").getValue().toString());
                                                        recentList.add(user);
                                                        adapter.notifyDataSetChanged();
                                                    } else {
                                                        user.setUserName("Deleted User");
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    /*private void adapterLoader() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("user").child(uID).child("connectedUser");
        DatabaseReference dbUser = FirebaseDatabase.getInstance().getReference().child("user");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    recentList.clear();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        User user = new User();
                        boolean add = true;
                        if (snap.child("chatID").exists()) {
                            user.setChatID(snap.child("chatID").getValue().toString());
                        } else {
                            dbUser.child(snap.getKey()).child("connectedUser").child(uID).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        if (snapshot.child("chatID").exists()) {
                                            user.setChatID(snapshot.child("chatID").getValue().toString());
                                            Log.d("CHECK_CHAT", "Inside" + " " + user.getChatID());
                                            Map<String, String> mChatID = new HashMap<>();
                                            mChatID.put("chatID", user.getChatID());
                                            mChatID.put("contactName", snap.child("contactName").getValue().toString());
                                            mChatID.put("search", snap.child("search").getValue().toString());
                                            mChatID.put("phnNum", snap.child("phnNum").getValue().toString());
                                            db.child(snap.getKey()).setValue(mChatID);
                                            if (snap.child("contactName").getValue().toString() != null) {
                                                user.setUserName(snap.child("contactName").getValue().toString());
                                            }
                                            recentList.add(user);
                                        } else {
                                            user.setChatID("");
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        if (user.getChatID() == null) {
                            add = false;
                        }
                        if (snap.child("contactName").getValue().toString() != null) {
                            user.setUserName(snap.child("contactName").getValue().toString());
                        }
                        if (add) {
                            recentList.add(user);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/
}
