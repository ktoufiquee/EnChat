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
import com.tsproject.enchat.Activity.ChatActivity;
import com.tsproject.enchat.R;
import com.tsproject.enchat.Model.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder> {

    ArrayList<User> recentList;
    Context context;

    public RecentAdapter() {
    }

    public void setRecentList(ArrayList<User> recentList) {
        this.recentList = recentList;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_heads, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //holder.ivProfilePic.setImageResource(recentList.get(position).);
        holder.tvName.setText(recentList.get(position).getUserName());
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("userID", FirebaseAuth.getInstance().getUid());
                intent.putExtra("chatID", recentList.get(holder.getBindingAdapterPosition()).getChatID());
                String contactName;
                if(recentList.get(holder.getBindingAdapterPosition()).getContactName() != null) {
                    contactName = recentList.get(holder.getBindingAdapterPosition()).getContactName();
                } else {
                    contactName = recentList.get(holder.getBindingAdapterPosition()).getUserName();
                }
                intent.putExtra("type", recentList.get(holder.getBindingAdapterPosition()).getType());
                intent.putExtra("friendName", contactName);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView ivProfilePic;
        private TextView tvName;
        private ConstraintLayout parent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            tvName = itemView.findViewById(R.id.tvName);
            parent = itemView.findViewById(R.id.clRecent);
        }
    }
}
