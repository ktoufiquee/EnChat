package com.tsproject.enchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navView = findViewById(R.id.navView);

        TextView uID = findViewById(R.id.uID);
        uID.setText(FirebaseAuth.getInstance().getCurrentUser().getUid());

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

        Button startChat = findViewById(R.id.startChat);
        startChat.setOnClickListener(v -> startChatOnClick());

    }

    private void startChatOnClick() {
        EditText chatID = findViewById(R.id.chatID);
        EditText userID = findViewById(R.id.nickname);
        String userName = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(userID.getText().toString() != null) {
            userName = userID.getText().toString();
        }
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("userID", userName);
        intent.putExtra("chatID", chatID.getText().toString());
        startActivity(intent);
    }

}