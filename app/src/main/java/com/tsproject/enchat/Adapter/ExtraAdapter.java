package com.tsproject.enchat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.tsproject.enchat.Activity.MainActivity;
import com.tsproject.enchat.Model.Message;
import com.tsproject.enchat.R;
import com.tsproject.enchat.databinding.ItemGifBinding;

import java.util.ArrayList;
import java.util.Date;

public class ExtraAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<String> urlList;
    String chatID;

    public ExtraAdapter(Context context, ArrayList<String> urlList, String chatID) {
        this.context = context;
        this.urlList = urlList;
        this.chatID = chatID;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gif, parent, false);
        return new GIFViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GIFViewHolder gifHolder = (GIFViewHolder)holder;
        String urlExtra = urlList.get(position);
        Glide.with(context)
                .asGif()
                .load(urlExtra)
                .into(gifHolder.binding.ivExtra);

        //Sends a gif when clicked on it
        gifHolder.binding.ivExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = new Date();
                String messageID = FirebaseDatabase.getInstance().getReference().child("chat").child(chatID).push().getKey();
                Message message = new Message();
                message.setMessageType("GIF");
                message.setMediaUrl(urlList.get(gifHolder.getBindingAdapterPosition()));
                message.setTimestamp(date.getTime());
                message.setMessageID(messageID);
                message.setSenderId(FirebaseAuth.getInstance().getUid());
                FirebaseDatabase.getInstance().getReference().child("chat").child(chatID).child(messageID).setValue(message);
                FirebaseDatabase.getInstance().getReference().child("chat").child(chatID).child("lastMsg").setValue(MainActivity.Name + " sent a GIF.");
                FirebaseDatabase.getInstance().getReference().child("chat").child(chatID).child("lastTime").setValue(date.getTime());
            }
        });
    }

    @Override
    public int getItemCount() {
        return urlList.size();
    }

    public class GIFViewHolder extends RecyclerView.ViewHolder {
        ItemGifBinding binding;
        public GIFViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemGifBinding.bind(itemView);
        }
    }

}
