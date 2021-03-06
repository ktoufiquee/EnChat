package com.tsproject.enchat.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tsproject.enchat.Adapter.ContactListAdapter;
import com.tsproject.enchat.Extra.CountryToPhonePrefix;
import com.tsproject.enchat.R;
import com.tsproject.enchat.Model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FindUserActivity extends AppCompatActivity {
    public static final String tag = "TAG";
    private RecyclerView rvContact;
    private FirebaseDatabase db;
    private DatabaseReference dRef, userRef;
    private FirebaseUser currentUser;
    private ArrayList<User> contactList, userList;
    private ContactListAdapter adapter;
    private EditText etSearchUser;

    ProgressDialog dialog;
    String filepath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading image...");
        dialog.setCancelable(false);

        rvContact = findViewById(R.id.rvContact);
        etSearchUser = findViewById(R.id.etSearchFilter);
        contactList = new ArrayList<>();
        userList = new ArrayList<>();
        db = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        dRef = db.getReference().child("user").child(currentUser.getUid());
        initializeRecyclerView();
        dRef.child("contactPermission").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("SEARCH", "SNAPSHOT EXISTS? " + snapshot.exists());
                if (snapshot.exists()) {
                    Log.d("SEARCH", "SNAPSHOT EXISTS");
                    if (snapshot.getValue().equals("true")) {
                        Log.d("SEARCH", "READ USER");
                        readUser();
                        //   getContacts();
                    } else {
                        Log.d("SEARCH", "SHOW ALERT");
                        showAlert();
                    }

                } else {
                    Log.d("SEARCH", "PERMISSION FIRST TIME");
                    getPermission();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Log.d("tag", "onCreate: " + "Recycler view");

        etSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etSearchUser.getText().toString().equals("")) {
                    readUser();
                } else {
                    searchUser(s.toString().toLowerCase());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ImageView btnBack = findViewById(R.id.btnBackCtoM);
        btnBack.setOnClickListener(view -> btnBackOnClick());
    }

    public void btnBackOnClick() {
        finish();
        startActivity(new Intent(FindUserActivity.this, MainActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 25) {
            if (data != null) {
                if (data.getData() != null) {
                    Uri selectedImage = data.getData();
                    StorageReference ref = FirebaseStorage.getInstance().getReference().child("profilePic").child(FirebaseAuth.getInstance().getUid());
                    dialog.show();
                    ref.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            if (task.isSuccessful()) {
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        filepath = uri.toString();
                                        FirebaseDatabase.getInstance()
                                                .getReference()
                                                .child("user")
                                                .child(FirebaseAuth.getInstance().getUid())
                                                .child("imageURL")
                                                .setValue(filepath);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
    }

    public void showAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("EnChat needs access to your contacts to connect with your friends");
        alert.setCancelable(false);
        alert.setPositiveButton("Continue", (dialog, which) -> {
            dialog.cancel();
            getPermission();
        });
        alert.setNegativeButton("Not now", (dialog, which) -> {
            dialog.cancel();
            Intent intent = new Intent(FindUserActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
        });
        alert.create().show();
    }

    private void initializeRecyclerView() {
        adapter = new ContactListAdapter(this);
        adapter.setContactList(userList);
        rvContact.setAdapter(adapter);
        rvContact.setLayoutManager(new LinearLayoutManager(this));
    }

    private void searchUser(String s) {
        Log.d("SEARCH", "searchUser: works");
        userRef = db.getReference().child("user").child(currentUser.getUid()).child("connectedUser");

        Query query = userRef.orderByChild("search")
                .startAt(s)
                .endAt(s + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                Log.d("SEARCH", "SEARCH SNAPSHOT" + snapshot.exists());

                String name = "";
                String number = "";
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    User user = new User();
                    if (childSnapshot.child("contactName").getValue() != null) {

                        name = childSnapshot.child("contactName").getValue().toString();

                    }
//                    if (childSnapshot.child("phnNum").getValue() != null) {
//                        number = childSnapshot.child("phnName").getValue().toString();
//                    }
                    Log.d("SEARCH", "onDataChange: SEARCH USER" + "name = " + name + "number = " + number);
                    user.setContactName(name);
                    //user.setPhnNum(number);
                    userList.add(user);

                }
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUser() {
        //This function will be called after pressing on Contact button
        //Read all the contacts from users profile in Firebase
        //Store them in an ArrayList
        //Notify the adapter
        Log.d("SEARCH", "readUser: called");
        userRef = db.getReference().child("user").child(currentUser.getUid()).child("connectedUser");
        userRef.addValueEventListener(new ValueEventListener() {
            String contactName = "";
            String number = "";

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.d("SEARCH", "readUser: works");
                userList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapShot : snapshot.getChildren()) {
                        ;
                        if (childSnapShot.child("contactName").getValue() != null) {
                            contactName = childSnapShot.child("contactName").getValue().toString();
                        }
                        if (childSnapShot.child("phnNum").getValue() != null) {
                            number = childSnapShot.child("phnNum").getValue().toString();
                        }
                        User appUser = new User();
                        Log.d("SEARCH", "onDataChange: " + contactName + "number =  " + number);
                        appUser.setContactName(contactName);
                        appUser.setPhnNum(number);
                        appUser.setuID(childSnapShot.getKey());
                        Log.d("CHECK_ID", childSnapShot.getKey());
                        userList.add(appUser);
                        adapter.notifyDataSetChanged();
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Scenario:
    //First Time Run = Permission -> getContact -> readUser
    //Not First Time Run = Permission -> false -> Ask? -> getContact -> readUser
    //Not First Time Run = Permission -> True -> readUser
    //SearchBar TextLength() > 0 -> remove contacts from ArrayList that doesn't match with criteria -> Notify adapter
    //SearchBar TextLength() = 0 -> readUser


    private void getContacts() {
        Log.d("SEARCH", "getContacts: called");
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
        Cursor cursor = getContentResolver()
                .query(uri, null, null, null, sort);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                @SuppressLint("Range") String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Uri phnUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";
                Cursor phnCursor = getContentResolver().
                        query(phnUri, null, selection, new String[]{id}, null);

                if (phnCursor.moveToNext()) {
                    @SuppressLint("Range") String number = phnCursor.getString(phnCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String rec_number = formatNumber(number);
                    User phnContact = new User();
                    phnContact.setContactName(contactName);
                    phnContact.setPhnNum(rec_number);
                    contactList.add(phnContact);
                    getUserDetails(phnContact);
                    phnCursor.close();

                }
            }
        }
        cursor.close();
    }

    private String formatNumber(String number) {

        //String iso = getCountryIso();
        String iso = "+880";
        String fNum = "";
        number = number.replaceAll("[^0-9]+", "");
        if (number.charAt(0) == '0') {
            number = number.substring(1);
        } else if (number.substring(0, iso.length() - 1).equals(iso.substring(1))) {
            number = number.substring(iso.length() - 1);
        }
        fNum = iso + number;
        Log.d("format", "formatNumber: " + iso + " " + fNum);

        return fNum;
    }


    private void getUserDetails(User phnContact) {
        dRef = db.getReference().child("user").child(currentUser.getUid());
//        Log.d("SEARCH", "getUserDetails: " + currentUser.getUid());
//        Log.d("SEARCH", "getUserDetails: USERCONTACT");
        Log.d("EMU_CHECK", phnContact.getPhnNum());
        userRef = db.getReference().child("user");
        Log.d("TESTING", phnContact.getPhnNum());
        Query query = userRef.orderByChild("phnNum").equalTo(phnContact.getPhnNum());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        Map<String, String> save = new HashMap<>();
                        save.put("contactName", phnContact.getContactName());
                        save.put("search", phnContact.getContactName().toLowerCase());
                        save.put("phnNum", phnContact.getPhnNum());
                        dRef.child("connectedUser").child(childSnapshot.getKey()).setValue(save);
                    }

                }
                else
                {
                    Map<String, String> s = new HashMap<>();
                    s.put("contactName", phnContact.getContactName());
                    s.put("search", phnContact.getContactName().toLowerCase());
                    s.put("phnNum", phnContact.getPhnNum());
                    String take = dRef.child("inviteUser").push().getKey();
                    dRef.child("inviteUser").child(take).setValue(s);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private String getCountryIso() {
        String countryIso = "";
        String iso = "";
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().
                getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if (telephonyManager.getNetworkCountryIso() != null) {
            if (!telephonyManager.getNetworkCountryIso().equals("")) {
                countryIso = telephonyManager.getNetworkCountryIso();
                iso = CountryToPhonePrefix.getPhone(countryIso);
            }
        }
        return iso;
    }

    private void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(FindUserActivity.this,
                        Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(FindUserActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
            Log.d("PERMISSION", "getPermission: request permission ");
        } else {
//            dRef.child("contactPermission").setValue("true");
            Log.d("PERMISSION", "getPermission: request permission accepted");
            // getContacts();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        dRef = db.getReference().child("user").child(currentUser.getUid());
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("PERMISSION", "onRequestPermissionsResult: get Permission Called");
            dRef.child("contactPermission").setValue("true");
            getContacts();
        } else {
            Log.d("PERMISSION", "onRequestPermissionsResult: " + "can't show");
            dRef.child("contactPermission").setValue("false");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
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
