package com.tsproject.enchat.Adapter;

import android.app.Activity;
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

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Message> messageList;
    String chatID;
    int chatType;
    boolean toggle = false;
    Activity chatActivity;

    final int ITEM_SEND = 1;
    final int ITEM_RECEIVE = 2;

    public ChatAdapter(Context context, ArrayList<Message> messageList, String chatID, int chatType) {
        this.context = context;
        this.messageList = messageList;
        this.chatID = chatID;
        this.chatType = chatType;
    }

    public void setChatActivity(Activity chatActivity) {
        this.chatActivity = chatActivity;
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

        if (holder.getClass() == SendViewHolder.class) {
            SendViewHolder sendViewHolder = (SendViewHolder) holder;
            sendViewHolder.binding.tvMessageSend.setText(message.getMessage());
            sendViewHolder.binding.clSend.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    View v = chatActivity.getCurrentFocus();
                    v.clearFocus();
                    view.requestFocus();
                    toggle = !toggle;
                    if (toggle) {
                        sendViewHolder.binding.llExtra.setVisibility(View.VISIBLE);
                    } else {
                        sendViewHolder.binding.llExtra.setVisibility(View.GONE);
                    }
                    return false;
                }
            });
            sendViewHolder.binding.clSend.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    toggle = !toggle;
                    if (toggle) {
                        sendViewHolder.binding.llExtra.setVisibility(View.VISIBLE);
                    } else {
                        sendViewHolder.binding.llExtra.setVisibility(View.GONE);
                    }
                }
            });
            if (message.getReact() >= 0) {
                sendViewHolder.binding.ivReactSend.setImageResource(reacts[(int) message.getReact()]);
                sendViewHolder.binding.ivReactSend.setVisibility(View.VISIBLE);
            } else {
                sendViewHolder.binding.ivReactSend.setVisibility(View.GONE);
            }

            if (message.getMediaUrl() != null) {
                sendViewHolder.binding.ivMediaSend.setVisibility(View.VISIBLE);
                if (message.getMessageType().equals("GIF")) {
                    Glide.with(context)
                            .asGif()
                            .load(message.getMediaUrl())
                            .into(sendViewHolder.binding.ivMediaSend);
                } else {
                    Glide.with(context).load(message.getMediaUrl())
                            .placeholder(R.mipmap.ic_image_placeholder_foreground)
                            .into(sendViewHolder.binding.ivMediaSend);
                }
                if (message.getMessage().isEmpty()) {
                    sendViewHolder.binding.tvMessageSend.setVisibility(View.GONE);
                }
            }
            sendViewHolder.binding.ivDeleteText.setOnClickListener(view -> deleteTextOnClick(messageList.get(bpos).getMessageID()));
        } else {

            ReceiveViewHolder receiveViewHolder = (ReceiveViewHolder) holder;
            receiveViewHolder.binding.tvMessageRecieve.setText(message.getMessage());
            receiveViewHolder.binding.clReceive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggle = !toggle;
                    if (toggle) {
                        receiveViewHolder.binding.llExtra.setVisibility(View.VISIBLE);
                    } else {
                        receiveViewHolder.binding.llExtra.setVisibility(View.GONE);
                    }
                }
            });
            if (chatType == 1) {
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
            if (message.getReact() >= 0) {
                receiveViewHolder.binding.ivReactRecieve.setImageResource(reacts[(int) message.getReact()]);
                receiveViewHolder.binding.ivReactRecieve.setVisibility(View.VISIBLE);
            } else {
                receiveViewHolder.binding.ivReactRecieve.setVisibility(View.GONE);
            }

            if (message.getMediaUrl() != null) {
                receiveViewHolder.binding.ivMediaReceive.setVisibility(View.VISIBLE);
                if (message.getMessageType().equals("GIF")) {
                    Glide.with(context)
                            .asGif()
                            .load(message.getMediaUrl())
                            .into(receiveViewHolder.binding.ivMediaReceive);
                } else {
                    Glide.with(context).load(message.getMediaUrl())
                            .placeholder(R.mipmap.ic_image_placeholder_foreground)
                            .into(receiveViewHolder.binding.ivMediaReceive);
                }
                if (message.getMessage().isEmpty()) {
                    receiveViewHolder.binding.tvMessageRecieve.setVisibility(View.GONE);
                }
            }
            if (chatType != 1) {
                receiveViewHolder.binding.tvMessageRecieve.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popup.onTouch(v, event);
                        return false;
                    }
                });
                //.setOnClickListener(view -> deleteTextOnClick(messageList.get(bpos).getMessageID()));
            }
        }
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
