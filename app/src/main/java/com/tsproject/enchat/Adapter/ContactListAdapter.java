package com.tsproject.enchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tsproject.enchat.Activity.ChatActivity;
import com.tsproject.enchat.R;
import com.tsproject.enchat.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {
    private ArrayList<User> contactList = new ArrayList<>();
    private Context context;

    public ContactListAdapter() {

    }

    public ContactListAdapter(Context context) {
        this.context = context;
    }


    public void setContactList(ArrayList<User> contactList) {
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ContactListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }
    //ArrayList<Menu> m;
    @Override
    public void onBindViewHolder(@NonNull ContactListAdapter.ViewHolder holder, int position) {
        holder.tvContactName.setText(contactList.get(position).getContactName());
        holder.tvContactNumber.setText(contactList.get(position).getPhnNum());
        holder.clContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference dbUser = FirebaseDatabase.getInstance().getReference()
                        .child("user")
                        .child(FirebaseAuth.getInstance().getUid())
                        .child("connectedUser")
                        .child(contactList.get(holder.getBindingAdapterPosition()).getuID());
                dbUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String randKey = "";
                        if (snapshot.child("chatID").exists()) {
                            randKey = snapshot.child("chatID").getValue().toString();
                        } else {
                            randKey = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();
                        }
                        Map<String, String> hMap = new HashMap<>();
                        hMap.put("contactName", snapshot.child("contactName").getValue().toString());
                        hMap.put("phnNum", snapshot.child("phnNum").getValue().toString());
                        hMap.put("search", snapshot.child("search").getValue().toString());
                        hMap.put("chatID", randKey);
                        dbUser.setValue(hMap);
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("chatID", randKey);
                        intent.putExtra("friendName", contactList.get(holder.getAdapterPosition()).getContactName());
                        context.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvContactName, tvContactNumber;
        private ConstraintLayout clContact;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContactName = itemView.findViewById(R.id.tvContactName);
            tvContactNumber = itemView.findViewById(R.id.tvContactNumber);
            clContact = itemView.findViewById(R.id.clContact);
        }
    }
}
