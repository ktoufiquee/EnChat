package com.tsproject.enchat.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tsproject.enchat.Activity.MemberSelectionActivity;
import com.tsproject.enchat.Model.User;
import com.tsproject.enchat.R;
import com.tsproject.enchat.databinding.ItemUserBinding;

import java.util.ArrayList;

public class MemberSelectionAdapter extends RecyclerView.Adapter<MemberSelectionAdapter.ViewHolder> {

    Context context;
    ArrayList<User> friendList;
    TextView tvSelection;
    int count = 0;

    public MemberSelectionAdapter(Context context, ArrayList<User> friendList, TextView tvSelection) {
        this.context = context;
        this.friendList = friendList;
        this.tvSelection = tvSelection;
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
        holder.binding.mcvItemUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.binding.mcvItemUser.isChecked()) {
                    MemberSelectionActivity.selectedList.remove(friendList.get(holder.getBindingAdapterPosition()));
                    count--;
                } else {
                    MemberSelectionActivity.selectedList.add(friendList.get(holder.getBindingAdapterPosition()).getuID());
                    count++;
                }
                holder.binding.mcvItemUser.toggle();
                tvSelection.setText(count + " person selected");
            }
        });
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
