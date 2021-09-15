package com.tsproject.enchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.tsproject.enchat.databinding.ActivityChatBinding;


import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    ArrayList<Message> messageList;
    ChatAdapter adapter;
    FirebaseDatabase database;
    String uID, chatID;
    FirebaseStorage storage;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading image...");
        dialog.setCancelable(false);

        uID = FirebaseAuth.getInstance().getUid();
        chatID = getIntent().getExtras().getString("chatID");

        messageList = new ArrayList<>();
        adapter = new ChatAdapter(this, messageList, chatID);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = binding.etMessage.getText().toString();

                Date date = new Date();
                String messageID = database.getReference().child("chat").child(chatID).push().getKey();
                Message message = new Message(uID, text, messageID, date.getTime());

                database.getReference().child("chat").child(chatID).child(messageID).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        binding.etMessage.setText("");
                    }
                });
            }
        });

        binding.btnMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/");
                startActivityForResult(intent, 25);
            }
        });

        database.getReference().child("chat").child(chatID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    messageList.add(message);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 25) {
            if (data != null) {
                if (data.getData() != null) {
                    Uri selectedImage = data.getData();
                    String randKey = database.getReference().child("chat").push().getKey();
                    StorageReference ref = storage.getReference().child("chat").child(randKey);
                    dialog.show();
                    ref.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            if (task.isSuccessful()) {
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        String filepath = uri.toString();
                                        Toast.makeText(ChatActivity.this, filepath, Toast.LENGTH_LONG).show();

                                        String text = binding.etMessage.getText().toString();

                                        Date date = new Date();
                                        String messageID = database.getReference().child("chat").child(chatID).push().getKey();
                                        Message message = new Message(uID, text, messageID, date.getTime());
                                        message.setMediaUrl(filepath);
                                        database.getReference().child("chat").child(chatID).child(messageID).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                binding.etMessage.setText("");
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
    }
}