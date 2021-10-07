package com.tsproject.enchat.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tsproject.enchat.Adapter.ChatAdapter;
import com.tsproject.enchat.Adapter.ExtraAdapter;
import com.tsproject.enchat.Model.Message;
import com.tsproject.enchat.Model.User;
import com.tsproject.enchat.databinding.ActivityChatBinding;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChatActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_CODE = 101;
    ActivityChatBinding binding;
    ArrayList<Message> messageList;
    ArrayList<String> urlList;
    ChatAdapter adapter;
    ExtraAdapter extraAdapter;
    FirebaseDatabase database;
    String uID, chatID, fID;
    int chatType;
    FirebaseStorage storage;
    ProgressDialog dialog;
    FirebaseUser currentUser;
    RecordView recordView;
    MediaRecorder mediaRecorder;
    String audio_path;
    Uri uriAudio;
    long blockStatus = 0;
    long blockedStatus = 0;

    private static final String TENOR_KEY = "RONF4J9X08K8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Creates the dialog that will be shown while uploading Image
        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading image...");
        dialog.setCancelable(false);

        uID = FirebaseAuth.getInstance().getUid();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        fID = getIntent().getExtras().getString("friendID");
        chatType = getIntent().getExtras().getInt("type");
        chatID = getIntent().getExtras().getString("chatID");
        String friendName = getIntent().getExtras().getString("friendName");
        binding.tvFriendName.setText(friendName);


        if (chatType == 0) {
            FirebaseDatabase.getInstance().getReference().child("user").child(fID).child("imageURL").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String url = snapshot.getValue().toString();
                        if (!url.equals("No Image")) {
                            Glide.with(getApplicationContext())
                                    .load(url)
                                    .into(binding.civFriendImage);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        if (chatType == 1) {
            FirebaseDatabase.getInstance().getReference().child("chat").child(chatID).child("imageURL").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String url = snapshot.getValue().toString();
                        if (!url.equals("No Image")) {
                            Glide.with(getApplicationContext())
                                    .load(url)
                                    .into(binding.civFriendImage);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        if (chatType == 2) {
            binding.civFriendImage.setVisibility(View.INVISIBLE);
        }

        if (chatType == 2) {
            binding.ivBlockUser.setVisibility(View.GONE);
        }

        if (chatType == 0) {
            FirebaseDatabase.getInstance().getReference().child("block_list").child(uID).child(fID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        blockStatus = (long) snapshot.getValue();
                        if (blockStatus == 1) {
                            binding.tvBlockText.setVisibility(View.VISIBLE);
                            binding.llChatContent.setVisibility(View.GONE);
                            binding.tvBlockText.setText("You have blocked this person");
                        } else {
                            binding.tvBlockText.setVisibility(View.GONE);
                            binding.llChatContent.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            FirebaseDatabase.getInstance().getReference().child("block_list").child(fID).child(uID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        blockedStatus = (long) snapshot.getValue();
                        if (snapshot.exists()) {
                            blockedStatus = (long) snapshot.getValue();
                            if (blockedStatus == 1) {
                                binding.tvBlockText.setVisibility(View.VISIBLE);
                                binding.llChatContent.setVisibility(View.GONE);
                                binding.ivBlockUser.setVisibility(View.GONE);
                                binding.tvBlockText.setText("This person is unavailable");
                            } else {
                                binding.tvBlockText.setVisibility(View.GONE);
                                binding.llChatContent.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        binding.ivBlockUser.setOnClickListener(view -> BlockOnClick());

        if (chatType == 1 || chatType == 2) {
            binding.tvStatus.setVisibility(View.GONE);
        } else {
            binding.tvStatus.setVisibility(View.VISIBLE);
        }
        //Initializes the messageList ArrayList, Sets the adapter to show messages
        messageList = new ArrayList<>();
        adapter = new ChatAdapter(getApplicationContext(), messageList, chatID, chatType);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);


        //Initializes the GIF URL arrayList, Sets the adapter for GIF keyboard
        urlList = new ArrayList<>();
        extraAdapter = new ExtraAdapter(this, urlList, chatID);
        binding.rvExtra.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvExtra.setAdapter(extraAdapter);

        //see user's active status
        if (chatType == 0) {
            /*FirebaseDatabase.getInstance().getReference().child("user").child(fID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("typing").equals(currentUser.getUid())) {
                        binding.tvStatus.setText("typing...");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });*/
            FirebaseDatabase.getInstance().getReference().child("user").child(fID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    binding.tvStatus.setText(snapshot.child("activeStatus").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            /*if (binding.tvStatus.getText().length() == 2) {

            }*/
        }

        //Sends the message written in etMessage
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = binding.etMessage.getText().toString();
                if (text.replaceAll(" ", "").length() < 1) {
                    return;
                }
                Date date = new Date();
                String messageID = database.getReference().child("chat").child(chatID).push().getKey();
                Message message = new Message(uID, text, messageID, date.getTime());
                database.getReference().child("chat").child(chatID).child("lastMsg").setValue(text);
                database.getReference().child("chat").child(chatID).child("lastTime").setValue(date.getTime());
                database.getReference().child("chat").child(chatID).child(messageID).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        binding.etMessage.setText("");
                    }
                });
            }
        });

        //Opens Gallery to select Image
        binding.btnMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/");
                startActivityForResult(intent, 25);
            }
        });

        //Reads all the message from database to ChatAdapter, Scrolls to the bottom of the RecyclerView (139,140)

        database.getReference().child("chat").child(chatID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int prevCount = messageList.size();
                messageList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("senderId").exists()) {
                        Message message = dataSnapshot.getValue(Message.class);
                        messageList.add(message);
                    }
                }
                adapter.notifyDataSetChanged();
                binding.recyclerView.smoothScrollToPosition(binding.recyclerView.getAdapter().getItemCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Functionality for btnExtra and btnCloseExtra
        binding.btnExtra.setOnClickListener(view -> btnExtraClicked());
        binding.btnCloseExtra.setOnClickListener(view -> btnCloseExtraClicked());

        //Functionality for GIF Search
        binding.etExtraSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateGIFUrl(charSequence.toString());
                updateTypingStatus(fID);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        /*binding.etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() == 0) {
                    updateTypingStatus("No");
                } else {
                    updateTypingStatus(fID);//the one who is receiving the text will see typing
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/
        if(chatType == 0) {
            binding.friendStatusLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ChatActivity.this, ProfileFriendActivity.class);
                    intent.putExtra("friendID", fID);
                    intent.putExtra("friendName", friendName);
                    startActivity(intent);
                }
            });
        }

        //initialize record button
        binding.btnRecord.setRecordView(binding.recordView);
        binding.btnRecord.setListenForRecord(false);
        binding.btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!checkPermissionForAudio()) {
                    binding.btnRecord.setListenForRecord(true);
                } else {
                    requestPermissionForAudio();
                }
            }
        });
        binding.recordView.setOnRecordListener(new OnRecordListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onStart() {
                //Start recording if has permission,otherwise request permission
                binding.etMessage.setVisibility(View.INVISIBLE);
                binding.btnMedia.setVisibility(View.INVISIBLE);
                binding.btnSend.setVisibility(View.INVISIBLE);
                binding.btnExtra.setVisibility(View.INVISIBLE);
                startRecording();
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null) {
                    vibrator.vibrate(1000);
                }
            }

            @Override
            public void onCancel() {
                //Cancel recording
                try {
                    mediaRecorder.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                //Stop Recording
                binding.etMessage.setVisibility(View.VISIBLE);
                binding.btnMedia.setVisibility(View.VISIBLE);
                binding.btnSend.setVisibility(View.VISIBLE);
                binding.btnExtra.setVisibility(View.VISIBLE);
                try {
                    String time = getHumanTimeText(recordTime);
                    stopRecord();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLessThanSecond() {
                binding.etMessage.setVisibility(View.VISIBLE);
                binding.btnMedia.setVisibility(View.VISIBLE);
                binding.btnSend.setVisibility(View.VISIBLE);
                binding.btnExtra.setVisibility(View.VISIBLE);
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void BlockOnClick() {
        if (chatType == 0) {
            if (blockStatus == 0) {
                FirebaseDatabase.getInstance().getReference().child("block_list").child(uID).child(fID).setValue(1);
            } else {
                FirebaseDatabase.getInstance().getReference().child("block_list").child(uID).child(fID).setValue(0);
            }
        }
        if (chatType == 1) {
            FirebaseDatabase.getInstance().getReference().child("chat").child(chatID).removeValue();
            getApplicationContext().startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }


    private void btnCloseExtraClicked() {
        binding.cardView.setVisibility(View.VISIBLE);
        binding.layoutExtra.setVisibility(View.GONE);
    }

    private void btnExtraClicked() {
        binding.cardView.setVisibility(View.GONE);
        binding.layoutExtra.setVisibility(View.VISIBLE);
        binding.etExtraSearch.setText("");
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
                                        message.setMessageType("PIC");
                                        message.setMediaUrl(filepath);
                                        database.getReference().child("chat").child(chatID).child(messageID).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                binding.etMessage.setText("");
                                            }
                                        });
                                        database.getReference().child("chat").child(chatID).child("lastMsg").setValue(MainActivity.Name + " sent an attachment.");
                                        database.getReference().child("chat").child(chatID).child("lastTime").setValue(date.getTime());
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
    }

    private void updateGIFUrl(String searchTerm) {

        new Thread() {
            @Override
            public void run() {
                urlList.clear();
                int limit = 32;
                //If search box is empty, Show trending gif. Otherwise show with respect to search term
                JSONObject searchResult = searchTerm.equals("") ? getTrendingGifs(limit) : getSearchResults(searchTerm, limit);
                // load the results for the user
                JSONArray results = new JSONArray();
                try {
                    results = searchResult.getJSONArray("results");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Loop to parse all the GIF url from JSON
                for (int i = 0; i < results.length(); ++i) {
                    String url = "";
                    try {
                        JSONArray jsonArray = results.getJSONObject(i).getJSONArray("media");
                        JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                        JSONObject gifObject = (JSONObject) jsonObject.get("gif");
                        urlList.add(gifObject.getString("url"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //Uses runOnUiThread to Notify the adapter as View elements can't be accessed from another thread
                ChatActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Collections.shuffle(urlList);
                        extraAdapter.notifyDataSetChanged();
                    }
                });
            }
        }.start();

    }

    //Creates the connection URL for TENOR
    public static JSONObject getSearchResults(String searchTerm, int limit) {

        // make search request - using default locale of EN_US
        final String url = String.format("https://g.tenor.com/v1/search?q=%1$s&key=%2$s&limit=%3$s",
                searchTerm, TENOR_KEY, limit);
        try {
            return get(url);
        } catch (IOException | JSONException ignored) {
        }
        return null;
    }

    public static JSONObject getTrendingGifs(int limit) {

        // get the trending GIFS - using the default locale of en_US
        final String url = String.format("https://g.tenor.com/v1/trending?key=%1$s&limit=%2$s",
                TENOR_KEY, limit);
        try {
            return get(url);
        } catch (IOException | JSONException ignored) {
        }
        return null;
    }


    //Constructs and run a get request
    private static JSONObject get(String url) throws IOException, JSONException {
        HttpURLConnection connection = null;
        try {
            // Get request
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            // Handle failure
            int statusCode = connection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK && statusCode != HttpURLConnection.HTTP_CREATED) {
                String error = String.format("HTTP Code: '%1$s' from '%2$s'", statusCode, url);
                throw new ConnectException(error);
            }

            // Parse response
            return parser(connection);
        } catch (Exception ignored) {
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return new JSONObject("");
    }

    //Parse the responses into a JSONObject
    private static JSONObject parser(HttpURLConnection connection) throws JSONException {
        char[] buffer = new char[1024 * 4];
        int n;
        InputStream stream = null;
        try {
            stream = new BufferedInputStream(connection.getInputStream());
            InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
            StringWriter writer = new StringWriter();
            while (-1 != (n = reader.read(buffer))) {
                writer.write(buffer, 0, n);
            }
            return new JSONObject(writer.toString());
        } catch (IOException ignored) {
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ignored) {
                }
            }
        }
        return new JSONObject("");
    }

    //check if user is online,else show last seen
    public static void checkOnlineStatus(String status) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("user").child(currentUser.getUid());
        Map<String, Object> curr_status = new HashMap<>();
        if (status.equals("online")) {
            curr_status.put("activeStatus", status);
        } else {
            curr_status.put("activeStatus", convertTime(status));
        }
        dbRef.updateChildren(curr_status);
    }

    //check if user is typing
    private void updateTypingStatus(String type_status) {
        // FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("user").child(currentUser.getUid());
        Map<String, Object> type = new HashMap<>();
        type.put("typing", type_status);
        dbRef.updateChildren(type);
    }

    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //set offline and last seen
        //gettime Stamp
        String timeStamp = String.valueOf(System.currentTimeMillis());
        //convertTime(timeStamp);
        checkOnlineStatus(timeStamp);
        updateTypingStatus("No");

    }

    @Override
    protected void onStart() {
        //set online
        checkOnlineStatus("online");
        super.onStart();

    }

    public String getTime() {

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

    //convert timestamp
    public static String convertTime(String timeStamp) {
        String show;
        double mSecPerMinute = 60 * 1000;//milli
        double mSecPerHour = mSecPerMinute * 60;
        double mSecPerDay = mSecPerHour * 24;
        double mSecPerMonth = mSecPerDay * 30;
        double mSecPerYear = mSecPerDay * 365;
        Date currentDate = new Date();
        Date stampDate = new Date(Long.parseLong(timeStamp));
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
        String timeString = timeFormat.format(new Date(Long.parseLong(timeStamp)));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = dateFormat.format(new Date(Long.parseLong(timeStamp)));
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE");
        String dayString = dayFormat.format(new Date(Long.parseLong(timeStamp)));
        double diff = currentDate.getTime() - stampDate.getTime();
        if (diff < mSecPerMonth)//day
        {
            if ((diff / mSecPerDay) < 1.00) {
                Log.d("Date", "convertTime: " + diff / mSecPerHour);
                show = "Last seen today at " + timeString;
            } else if ((diff / mSecPerDay) < 2.00) {
                show = "Last seen yesterday at " + timeString;
            } else if ((diff / mSecPerDay) < 8.00) {
                show = "Last seen " + dayString + " at " + timeString;
            } else {
                show = "Last seen at " + dateString;
            }
        } else if (diff < mSecPerYear)//month
        {
            if ((diff / mSecPerMonth) < 1.00) {
                show = "Last seen" + (long) Math.floor(diff / mSecPerMonth) + " month ago";
            } else {
                show = "Last seen" + (long) Math.floor(diff / mSecPerMonth) + " months ago";
            }

        } else { //year

            show = "Last seen a long time ago";

        }
        return show;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    //check permission for recording,if either of the permission is denied,recording will not take place

    private boolean checkPermissionForAudio() {
        int write_external_storage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage == PackageManager.PERMISSION_DENIED | record_audio == PackageManager.PERMISSION_DENIED;
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage == PackageManager.PERMISSION_DENIED | record_audio == PackageManager.PERMISSION_DENIED;
    }

    //else request permission
    private void requestPermissionForAudio() {

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_RECORD_CODE);
    }

    private String getHumanTimeText(long recordTime) {
        String cal = String.format("%02d",
                TimeUnit.MILLISECONDS.toSeconds(recordTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(recordTime)));
        Log.d("Cal", "getHumanTimeText: " + cal);
        return cal;
    }

    private void startRecording() {
        setUpRecording();
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Recording Error Occured", Toast.LENGTH_SHORT).show();
        }

    }

    private void stopRecord() {
        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
                //Send the recording
                uriAudio = Uri.fromFile(new File(audio_path));
                StorageReference sRef = FirebaseStorage.getInstance().getReference().child("enhat/" + fID + "/Recording" + System.currentTimeMillis());
                sRef.putFile(uriAudio).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        Log.d("Media", "onSuccess: ?" + urlTask);
                    }
                });

            } else {
                Toast.makeText(this, "Please try again!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "There were issues with the recording", Toast.LENGTH_SHORT).show();
        }

    }

    private void setUpRecording() {
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "audio_record.m4a";
        audio_path = file_path;
        mediaRecorder = new MediaRecorder();
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(file_path);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}