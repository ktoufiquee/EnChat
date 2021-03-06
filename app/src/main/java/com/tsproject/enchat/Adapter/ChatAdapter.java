package com.tsproject.enchat.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tsproject.enchat.Activity.ChatActivity;
import com.tsproject.enchat.Model.Message;
import com.tsproject.enchat.R;
import com.tsproject.enchat.databinding.ActivityChatBinding;
import com.tsproject.enchat.databinding.ItemReceiveBinding;
import com.tsproject.enchat.databinding.ItemSendBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChatAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Message> messageList;
    String chatID;
    int chatType;
    boolean toggle = false;


    final int ITEM_SEND = 1;
    final int ITEM_RECEIVE = 2;

    public ChatAdapter(Context context, ArrayList<Message> messageList, String chatID, int chatType) {
        this.context = context;
        this.messageList = messageList;
        this.chatID = chatID;
        this.chatType = chatType;
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
        int bpos = holder.getBindingAdapterPosition();

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
        String timeString = timeFormat.format(new Date(Long.parseLong(message.getTimestamp() + "")));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = dateFormat.format(new Date(Long.parseLong(message.getTimestamp() + "")));

        //Storing reaction images in react[] array;
        int reacts[] = new int[]{
                R.mipmap.ic_react_like_foreground,
                R.mipmap.ic_react_love_foreground,
                R.mipmap.ic_react_laugh_foreground,
                R.mipmap.ic_react_wow_foreground,
                R.mipmap.ic_react_sad_foreground,
                R.mipmap.ic_react_angry_foreground
        };

        //Initializing ReactionsConfig with reacts[] array
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reacts)
                .build();

        //Setting ReactionPopup Touch action to update the message with correct reaction
        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if (holder.getClass() == SendViewHolder.class) {
                SendViewHolder sendViewHolder = (SendViewHolder) holder;
                sendViewHolder.binding.ivReactSend.setImageResource(reacts[pos]);
                sendViewHolder.binding.ivReactSend.setVisibility(View.VISIBLE);
            } else {
                ReceiveViewHolder receiveViewHolder = (ReceiveViewHolder) holder;
                if (pos >= 0) {
                    receiveViewHolder.binding.ivReactRecieve.setImageResource(reacts[pos]);
                }
            }
            if (message.getReact() == pos) {
                message.setReact(-1);
            } else {
                message.setReact(pos);
            }
            FirebaseDatabase.getInstance().getReference().child("chat").child(chatID).child(message.getMessageID()).setValue(message);
            return true; // true is closing popup, false is requesting a new selection
        });

        //If the message is sent from current user
        if (holder.getClass() == SendViewHolder.class) {
            SendViewHolder sendViewHolder = (SendViewHolder) holder;
            sendViewHolder.binding.tvMessageSend.setText(message.getMessage());
            sendViewHolder.binding.tvTime.setText(timeString + "," + dateString);
            //If the view is clicked, show additional buttons
            sendViewHolder.binding.clSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggle = !toggle;
                    if (toggle) {
                        sendViewHolder.binding.llExtra.setVisibility(View.VISIBLE);
                        sendViewHolder.binding.tvTime.setVisibility(View.VISIBLE);
                    } else {
                        sendViewHolder.binding.llExtra.setVisibility(View.GONE);
                        sendViewHolder.binding.tvTime.setVisibility(View.GONE);
                    }
                }
            });

            //If react is not -1 then load the reaction
            if (message.getReact() >= 0) {
                sendViewHolder.binding.ivReactSend.setImageResource(reacts[(int) message.getReact()]);
                sendViewHolder.binding.ivReactSend.setVisibility(View.VISIBLE);
            } else {
                sendViewHolder.binding.ivReactSend.setVisibility(View.GONE);
            }

            //If mediaURL is not empty, then the message contains a media that needs to be shown.
            if (message.getMediaUrl() != null) {
                sendViewHolder.binding.ivMediaSend.setVisibility(View.VISIBLE);
                if (message.getMessageType() != null) {
                    if (message.getMessageType().equals("PIC")) {
                        Glide.with(context)
                                .load(message.getMediaUrl())
                                .placeholder(R.mipmap.ic_image_placeholder_foreground)
                                .into(sendViewHolder.binding.ivMediaSend);
                    } else {
                        Glide.with(context)
                                .asGif()
                                .load(message.getMediaUrl())
                                .into(sendViewHolder.binding.ivMediaSend);
                    }
                }
                if (message.getMessage().isEmpty()) {
                    sendViewHolder.binding.tvMessageSend.setVisibility(View.GONE);
                }
            }

            sendViewHolder.binding.ivDeleteText.setOnClickListener(view -> deleteTextOnClick(messageList.get(bpos).getMessageID()));
            sendViewHolder.binding.ivSaveText.setOnClickListener(view -> saveTextOnClick(messageList.get(bpos).getMessageID()));

            if(chatType == 2) {
                sendViewHolder.binding.ivSaveText.setVisibility(View.GONE);
            } else {
                sendViewHolder.binding.ivSaveText.setVisibility(View.VISIBLE);
            }
            sendViewHolder.binding.ivForwardText.setVisibility(View.GONE);
        }
        //If the message is sent from another user
        else {
            ReceiveViewHolder receiveViewHolder = (ReceiveViewHolder) holder;
            receiveViewHolder.binding.tvMessageRecieve.setText(message.getMessage());
            receiveViewHolder.binding.tvTime.setText(timeString + "," + dateString);
            //If the view is clicked, show additional buttons
            receiveViewHolder.binding.clReceive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggle = !toggle;
                    if (toggle) {
                        receiveViewHolder.binding.llExtra.setVisibility(View.VISIBLE);
                        receiveViewHolder.binding.tvTime.setVisibility(View.VISIBLE);
                    } else {
                        receiveViewHolder.binding.llExtra.setVisibility(View.GONE);
                        receiveViewHolder.binding.tvTime.setVisibility(View.GONE);
                    }
                }
            });

            //If the chat is group chat, then show the username on top of the message
            if (chatType == 1 || chatType == 2) {
                receiveViewHolder.binding.tvRecievedName.setVisibility(View.VISIBLE);
                String fID = messageList.get(receiveViewHolder.getBindingAdapterPosition()).getSenderId();
                FirebaseDatabase.getInstance().getReference().child("user").child(fID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        receiveViewHolder.binding.tvRecievedName.setText(snapshot.child("userName").getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            if (message.getMediaUrl() != null) {
                receiveViewHolder.binding.ivMediaReceive.setVisibility(View.VISIBLE);
                if (message.getMessageType() != null) {
                    if (message.getMessageType().equals("PIC")) {
                        Glide.with(context).load(message.getMediaUrl())
                                .placeholder(R.mipmap.ic_image_placeholder_foreground)
                                .into(receiveViewHolder.binding.ivMediaReceive);
                    } else {
                        Glide.with(context)
                                .asGif()
                                .load(message.getMediaUrl())
                                .into(receiveViewHolder.binding.ivMediaReceive);
                    }
                }
                if (message.getMessage().isEmpty()) {
                    receiveViewHolder.binding.tvMessageRecieve.setVisibility(View.GONE);
                }
            }

            //Reaction is disabled on group chat
            if (chatType != 1) {
                if (message.getReact() >= 0) {
                    receiveViewHolder.binding.ivReactRecieve.setImageResource(reacts[(int) message.getReact()]);
                    receiveViewHolder.binding.ivReactRecieve.setVisibility(View.VISIBLE);
                } else {
                    receiveViewHolder.binding.ivReactRecieve.setVisibility(View.GONE);
                }
            }

            //Load the reaction option on touch if the chat is not a group chat
            receiveViewHolder.binding.tvMessageRecieve.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (chatType != 1) {
                        popup.onTouch(v, event);
                    }
                    return false;
                }
            });


            receiveViewHolder.binding.ivSaveText.setOnClickListener(view -> saveTextOnClick(messageList.get(bpos).getMessageID()));
            if(chatType == 2) {
                receiveViewHolder.binding.ivSaveText.setVisibility(View.GONE);
                receiveViewHolder.binding.ivDeleteText.setVisibility(View.VISIBLE);
                receiveViewHolder.binding.ivDeleteText.setOnClickListener(view -> deleteFromSave(messageList.get(bpos).getMessageID()));
            } else {
                receiveViewHolder.binding.ivSaveText.setVisibility(View.VISIBLE);
                receiveViewHolder.binding.ivDeleteText.setVisibility(View.GONE);
            }
            receiveViewHolder.binding.ivForwardText.setVisibility(View.GONE);
        }
    }

    private void deleteFromSave(String messageID) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("chat")
                .child(FirebaseAuth.getInstance().getUid())
                .child(messageID)
                .removeValue();
    }

    private void saveTextOnClick(String messageID) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("chat")
                .child(chatID)
                .child(messageID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        FirebaseDatabase.getInstance()
                                .getReference()
                                .child("chat")
                                .child(FirebaseAuth.getInstance().getUid())
                                .child(messageID)
                                .setValue(snapshot.getValue());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void deleteTextOnClick(String messageID) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("chat")
                .child(chatID)
                .child(messageID)
                .removeValue();
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
