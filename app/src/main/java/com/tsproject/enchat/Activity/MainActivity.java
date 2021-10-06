package com.tsproject.enchat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tsproject.enchat.Adapter.RecentAdapter;
import com.tsproject.enchat.R;
import com.tsproject.enchat.Model.User;

import java.util.ArrayList;
import java.util.List;

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

    public static boolean showArchive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Fresco.initialize(this);


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
                    case R.id.btnNewGroup:
                        CreateGroupOnClick();
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

        FirebaseDatabase.getInstance().getReference().child("user").child(uID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (dpUrl.equals("")) {
                    Name = snapshot.child("userName").getValue().toString();
                    dpUrl = snapshot.child("imageURL").getValue().toString();
                    phoneNumber = snapshot.child("phnNum").getValue().toString();
                }

                @NonNull TextView tvUsername = findViewById(R.id.tvUsername);
                @NonNull TextView tvUserNumber = findViewById(R.id.tvUserNumber);
                @NonNull ImageView profilePicture = findViewById(R.id.ivUserImage);
                tvUsername.setText(Name);
                tvUserNumber.setText(phoneNumber);
                Glide.with(getApplicationContext())
                        .load(dpUrl)
                        .into(profilePicture);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        CardView cvGetArchive = findViewById(R.id.cvGetArchive);
        cvGetArchive.setOnClickListener(view -> SeeArchivedOnClick());

    }

    private void SeeArchivedOnClick() {
        recentList.clear();
        TextView tvArchiveText = findViewById(R.id.tvArchiveText);
        if (showArchive == false) {
            tvArchiveText.setText("Hide Archived Chats");
            adapterLoader(1);
        } else {
            tvArchiveText.setText("Show Archived Chats");
            adapterLoader(0);
        }
        showArchive = !showArchive;
    }

    private void CreateGroupOnClick() {
        startActivity(new Intent(MainActivity.this, MemberSelectionActivity.class));
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
        adapterLoader(0);

    }

    private void adapterLoader(int setting) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("chat")
                .orderByChild(uID)
                .equalTo(setting)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        recentList.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                User user = new User();
                                user.setChatID(snap.getKey());

                                long type = (long) snap.child("type").getValue();
                                List<String> members = (List<String>) snap.child("members").getValue();
                                if (type == 0) {
                                    user.setType(0);
                                    String targetUID = members.get(0).equals(uID) ? members.get(1) : members.get(0);
                                    user.setuID(targetUID);
                                    FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("user")
                                            .child(targetUID)
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        user.setUserName(snapshot.child("userName").getValue().toString());
                                                        if (!recentList.contains(user)) {
                                                            recentList.add(user);
                                                        }
                                                    } else {
                                                        user.setUserName("Deleted User");
                                                    }
                                                    adapter.notifyDataSetChanged();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }
                                if (type == 1) {
                                    user.setType(1);
                                    user.setUserName(snap.child("GroupName").getValue().toString());
                                    if (!recentList.contains(user)) {
                                        recentList.add(user);
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    protected void onResume() {
        ChatActivity.checkOnlineStatus("online");
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //set offline and last seen
        //gettime Stamp
        String timeStamp = String.valueOf(System.currentTimeMillis());
        ChatActivity.checkOnlineStatus(timeStamp);

    }

    @Override
    protected void onStart() {
        //set online
        ChatActivity.checkOnlineStatus("online");
        super.onStart();

    }
}
