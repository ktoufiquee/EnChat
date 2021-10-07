package com.tsproject.enchat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tsproject.enchat.R;
import com.tsproject.enchat.databinding.ItemChatHeadsBinding;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.ViewHolder> {

    Context context;
    ArrayList<String> memberList;
    String adminUID;
    String chatID;

    public GroupMemberAdapter(Context context, ArrayList<String> memberList, String adminUID, String chatID) {
        this.context = context;
        this.memberList = memberList;
        this.adminUID = adminUID;
        this.chatID = chatID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_heads, parent, false);
        GroupMemberAdapter.ViewHolder holder = new GroupMemberAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.tvLastText.setVisibility(View.GONE);
        String currID = memberList.get(position);
        String uID = FirebaseAuth.getInstance().getUid();
        if(adminUID == uID) {
            holder.binding.ivArchive.setVisibility(View.VISIBLE);
            holder.binding.ivArchive.setImageResource(R.drawable.ic_baseline_delete_24);
        } else {
            holder.binding.ivArchive.setVisibility(View.GONE);
        }
        FirebaseDatabase.getInstance().getReference().child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(currID).child("imageURL").exists()) {
                    String url = snapshot.child(currID).child("imageURL").getValue().toString();
                    if (url.equals("No Image")) {
                        Glide.with(context.getApplicationContext())
                                .load(url)
                                .into(holder.binding.ivImageFriend);
                    }
                }
                String name = "";
                if (snapshot.child(uID).child("connectedUser").child(currID).exists()) {
                    name = snapshot.child(uID).child("connectedUser").child(currID).getValue().toString();
                }
                if (name.equals("")) {
                    name = snapshot.child(currID).child("userName").getValue().toString();
                }
                holder.binding.tvNameFriend.setText(name);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.binding.ivArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("chat").child(chatID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<String> currUsers = (ArrayList<String>) snapshot.child("members").getValue();
                        currUsers.remove(currID);
                        snapshot.getRef().child("members").setValue(currUsers);
                        snapshot.getRef().child(currID).removeValue();
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
        return memberList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemChatHeadsBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemChatHeadsBinding.bind(itemView);
        }
    }
}
