package com.tsproject.enchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private String userID, chatID;
    private RecyclerView rvChat;
    private RecyclerView.Adapter rvChatAdapter;
    private RecyclerView.LayoutManager rvChatLayoutManager;
    private DatabaseReference chatDB;

    ArrayList<MessageObject> messageList;
    ArrayList<String> mediaUriList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        userID = getIntent().getExtras().getString("userID");
        chatID = getIntent().getExtras().getString("chatID");
        chatDB = FirebaseDatabase.getInstance().getReference().child("chat").child(chatID);

        Button btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(v -> sendMessage());

        Button btnAttach = findViewById(R.id.btnAttach);
        btnAttach.setOnClickListener(v -> sendAttachment());

        initRecyclerView();
        getChatMessages();
    }



    private void sendAttachment() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture(s)"), 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                if (data.getClipData() == null) {
                    mediaUriList.add(data.getData().toString());
                }
                else {
                    for (int i = 0; i < data.getClipData().getItemCount(); ++i) {
                        mediaUriList.add(data.getClipData().getItemAt(i).toString());
                    }
                }
            }
        }
    }

    private void getChatMessages() {
        chatDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    String sender = "", text = "";
                    if (snapshot.child("text").getValue() != null) {
                        text = snapshot.child("text").getValue().toString();
                    }
                    if (snapshot.child("sender").getValue() != null) {
                        sender = snapshot.child("sender").getValue().toString();
                    }
                    MessageObject readMessage = new MessageObject(snapshot.getKey(), sender, text);
                    messageList.add(readMessage);
                    rvChatLayoutManager.scrollToPosition(messageList.size() - 1);
                    rvChatAdapter.notifyDataSetChanged();

                }

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

    private void sendMessage() {
        EditText mMessage = findViewById(R.id.etMessage);
        if (!mMessage.getText().toString().isEmpty()) {
            DatabaseReference newMessageDB = chatDB.push();
            Map newMessageMap = new HashMap<>();
            newMessageMap.put("text", mMessage.getText().toString());
            newMessageMap.put("sender", userID);

            newMessageDB.updateChildren(newMessageMap);
        }
        mMessage.setText(null);
    }

    private void initRecyclerView() {
        messageList = new ArrayList<>();
        rvChat = findViewById(R.id.messageList);
        rvChat.setNestedScrollingEnabled(false);
        rvChatLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        rvChat.setLayoutManager(rvChatLayoutManager);
        rvChatAdapter = new MessageAdapter(messageList);
        rvChat.setAdapter(rvChatAdapter);
    }
}