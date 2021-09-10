package com.tsproject.enchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindUserActivity extends AppCompatActivity {
    private RecyclerView rvContact;
    private ArrayList<User> contactList, userList;
    private ContactListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        rvContact = findViewById(R.id.rvContact);
        contactList = new ArrayList<>();
        userList = new ArrayList<>();

        initializeRecyclerView();
        getPermission();
        Log.d("tag", "onCreate: "+"Recycler view");
    }

    private void getPermission() {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    ContextCompat.checkSelfPermission(FindUserActivity.this,
                    Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED)
            {
                ActivityCompat.requestPermissions(FindUserActivity.this, new String[]{Manifest.permission.READ_CONTACTS},1);
            }
            else
            {
                getContacts();
            }
    }

   private void initializeRecyclerView(){
        adapter = new ContactListAdapter(this);
        adapter.setContactList(userList);
        rvContact.setAdapter(adapter);
        rvContact.setLayoutManager(new LinearLayoutManager(this));
    }
    private void getContacts()
    {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC";
        Cursor cursor = getContentResolver()
                                          .query(uri, null, null, null, sort);
        if(cursor.getCount() > 0)
        {
            while(cursor.moveToNext())
            {

                 String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                 String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                 Uri phnUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                 String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";
                 Cursor phnCursor = getContentResolver().
                                                    query(phnUri,null, selection,new String[]{id}, null);

                       if(phnCursor.moveToNext())
                        {
                            String number = phnCursor.getString(phnCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            number.replaceAll("[^0-9]+","");
                            if(number.charAt(0) == '0')
                            {
                                StringBuilder remove = new StringBuilder(number);
                                remove.deleteCharAt(0);
                                number = remove.toString();
                            }
                            if(number.charAt(0) != '+')
                            {
                                number = getCountryIso() + number;
                                Log.d("tag", "getContacts: " +number);
                            }
                            User phnContact = new User(name,number);
                            contactList.add(phnContact);
                            getUserDetails(phnContact);
                            Log.d("tag", "getContacts: works" );
                            phnCursor.close();

                        }

            }
        }
        cursor.close();
        Log.d("tag", "getContacts: +1st while loop ");

    }

    private void getUserDetails(User phnContact) {
        Log.d("tag", "getUserDetails: called");
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("user");
        Query query = dRef.orderByChild("phnNum").equalTo(phnContact.getPhnNum());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("tag", "on data change: called");
                Log.d("tag", "onDataChangeSnapshot: "+snapshot.exists());
                if(snapshot.exists())
                {

                    String name = "";
                    String number = "";
                    for(DataSnapshot childSnapshot : snapshot.getChildren())
                    {
                        Log.d("tag", "goes to for loop");
                        if(childSnapshot.child("userName").getValue() != null)
                        {
                            name = childSnapshot.child("userName").getValue().toString();
                        }
                        if(childSnapshot.child("phnNum").getValue() != null)
                        {
                            number = childSnapshot.child("phnNum").getValue().toString();
                        }
                        User appUser = new User(name, number);
                        userList.add(appUser);
                        adapter.notifyDataSetChanged();
                        Log.d("tag", "onDataChange: "+ name + " " + number);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private String getCountryIso()
    {
        String countryIso = "";
        String iso = "";
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().
                                            getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if(telephonyManager.getNetworkCountryIso() != null)
        {
            if(!telephonyManager.getNetworkCountryIso().equals(""))
            {
                countryIso = telephonyManager.getNetworkCountryIso();
                iso = CountryToPhonePrefix.getPhone(countryIso);
            }
        }
        return iso;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
                getContacts();
        }
        else
        {
            Log.d("tag", "onRequestPermissionsResult: "+ "can't show");
        }
    }
}