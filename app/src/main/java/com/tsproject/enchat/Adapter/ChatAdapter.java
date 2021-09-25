package com.tsproject.enchat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.tsproject.enchat.Model.Message;
import com.tsproject.enchat.R;
import com.tsproject.enchat.databinding.ItemReceiveBinding;
import com.tsproject.enchat.databinding.ItemSendBinding;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Message> messageList;
    String chatID;

    final int ITEM_SEND = 1;
    final int ITEM_RECEIVE = 2;

    public ChatAdapter(Context context, ArrayList<Message> messageList, String chatID) {
        this.context = context;
        this.messageList = messageList;
        this.chatID = chatID;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_send, parent, false);
            return new SendViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_receive, parent, false);
            return new ReceiveViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        int reacts[] = new int[]{
                R.mipmap.ic_react_like_foreground,
                R.mipmap.ic_react_love_foreground,
                R.mipmap.ic_react_laugh_foreground,
                R.mipmap.ic_react_wow_foreground,
                R.mipmap.ic_react_sad_foreground,
                R.mipmap.ic_react_angry_foreground
        };

        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reacts)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if (holder.getClass() == SendViewHolder.class) {
                SendViewHolder sendViewHolder = (SendViewHolder) holder;
                sendViewHolder.binding.ivReactSend.setImageResource(reacts[pos]);
                sendViewHolder.binding.ivReactSend.setVisibility(View.VISIBLE);
            } else {
                ReceiveViewHolder receiveViewHolder = (ReceiveViewHolder) holder;
                receiveViewHolder.binding.ivReactRecieve.setImageResource(reacts[pos]);
            }
            //Log.d("TEST", pos + " " + chatID + " " + message.getMessageID());
            if (message.getReact() == pos) {
                message.setReact(-1);
            } else {
                message.setReact(pos);
            }
            FirebaseDatabase.getInstance().getReference().child("chat").child(chatID).child(message.getMessageID()).setValue(message);

            return true; // true is closing popup, false is requesting a new selection
        });


        if (holder.getClass() == SendViewHolder.class) {
            SendViewHolder sendViewHolder = (SendViewHolder) holder;
            sendViewHolder.binding.tvMessageSend.setText(message.getMessage());
            if (message.getReact() >= 0) {
                sendViewHolder.binding.ivReactSend.setImageResource(reacts[(int) message.getReact()]);
                sendViewHolder.binding.ivReactSend.setVisibility(View.VISIBLE);
            } else {
                sendViewHolder.binding.ivReactSend.setVisibility(View.GONE);
            }

            if(message.getMediaUrl() != null) {
                sendViewHolder.binding.ivMediaSend.setVisibility(View.VISIBLE);
                Glide.with(context).load(message.getMediaUrl())
                        .placeholder(R.mipmap.ic_image_placeholder_foreground)
                        .into(sendViewHolder.binding.ivMediaSend);
                if(message.getMessage().isEmpty()) {
                    sendViewHolder.binding.tvMessageSend.setVisibility(View.GONE);
                }
            }

//            sendViewHolder.binding.tvMessageSend.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    popup.onTouch(v, event);
//                    return false;
//                }
//            });
        } else {
            ReceiveViewHolder receiveViewHolder = (ReceiveViewHolder) holder;
            receiveViewHolder.binding.tvMessageRecieve.setText(message.getMessage());
            if (message.getReact() >= 0) {
                receiveViewHolder.binding.ivReactRecieve.setImageResource(reacts[(int) message.getReact()]);
                receiveViewHolder.binding.ivReactRecieve.setVisibility(View.VISIBLE);
            } else {
                receiveViewHolder.binding.ivReactRecieve.setVisibility(View.GONE);
            }

            if(message.getMediaUrl() != null) {
                receiveViewHolder.binding.ivMediaReceive.setVisibility(View.VISIBLE);
                Glide.with(context).load(message.getMediaUrl())
                        .placeholder(R.mipmap.ic_image_placeholder_foreground)
                        .into(receiveViewHolder.binding.ivMediaReceive);
                if(message.getMessage().isEmpty()) {
                    receiveViewHolder.binding.tvMessageRecieve.setVisibility(View.GONE);
                }
            }

            receiveViewHolder.binding.tvMessageRecieve.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });

        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (FirebaseAuth.getInstance().getUid().equals(message.getSenderId())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECEIVE;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class SendViewHolder extends RecyclerView.ViewHolder {
        ItemSendBinding binding;

        public SendViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSendBinding.bind(itemView);
        }
    }

    public class ReceiveViewHolder extends RecyclerView.ViewHolder {
        ItemReceiveBinding binding;

        public ReceiveViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemReceiveBinding.bind(itemView);
        }
    }
}
