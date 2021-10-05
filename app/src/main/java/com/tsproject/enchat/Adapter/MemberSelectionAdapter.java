package com.tsproject.enchat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tsproject.enchat.Model.User;
import com.tsproject.enchat.R;
import com.tsproject.enchat.databinding.ItemUserBinding;

import java.util.ArrayList;

public class MemberSelectionAdapter extends RecyclerView.Adapter<MemberSelectionAdapter.ViewHolder> {

    Context context;
    ArrayList<User> friendList;

    public MemberSelectionAdapter(Context context, ArrayList<User> friendList) {
        this.context = context;
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public MemberSelectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new MemberSelectionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberSelectionAdapter.ViewHolder holder, int position) {
        Glide.with(context)
                .load(friendList.get(position).getImageURL())
                .into(holder.binding.ivImageFriend);
        holder.binding.tvNameFriend.setText(friendList.get(position).getUserName());
        holder.binding.tvNumberFriend.setText(friendList.get(position).getPhnNum());
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemUserBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemUserBinding.bind(itemView);
        }
    }
}
