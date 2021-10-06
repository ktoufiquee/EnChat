package com.tsproject.enchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tsproject.enchat.Activity.ChatActivity;
import com.tsproject.enchat.Activity.MainActivity;
import com.tsproject.enchat.R;
import com.tsproject.enchat.Model.User;
import com.tsproject.enchat.databinding.ItemChatHeadsBinding;

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
        holder.binding.tvNameFriend.setText(recentList.get(position).getUserName());
        int bpos = holder.getBindingAdapterPosition();
        holder.binding.mcvItemUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("userID", FirebaseAuth.getInstance().getUid());
                intent.putExtra("friendID", recentList.get(bpos).getuID());
                intent.putExtra("chatID", recentList.get(bpos).getChatID());
                String contactName;
                if (recentList.get(holder.getBindingAdapterPosition()).getContactName() != null) {
                    contactName = recentList.get(bpos).getContactName();
                } else {
                    contactName = recentList.get(bpos).getUserName();
                }
                intent.putExtra("type", recentList.get(bpos).getType());
                intent.putExtra("friendName", contactName);
                context.startActivity(intent);
            }
        });
        holder.binding.ivArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long var;
                if (MainActivity.showArchive) {
                    var = 0;
                } else {
                    var = 1;
                }
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("chat")
                        .child(recentList.get(bpos).getChatID())
                        .child(FirebaseAuth.getInstance().getUid())
                        .setValue(var);
            }
        });
        FirebaseDatabase.getInstance().getReference().child("chat").child(recentList.get(bpos).getChatID()).child("lastMsg")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.binding.tvLastText.setText(snapshot.getValue().toString());
                        } else {
                            holder.binding.tvLastText.setText("Tap to start chatting with your friend.");
                            holder.binding.tvLastText.setTextColor(Color.parseColor("#003F63"));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        if (recentList.get(bpos).getType() == 0) {
            FirebaseDatabase.getInstance().getReference().child("user").child(recentList.get(bpos).getuID()).child("imageURL")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String imageURL = snapshot.getValue().toString();
                                if (imageURL == "No Image") {
                                    BitmapDrawable dp = getProfilePhoto(recentList.get(bpos).getUserName().charAt(0) + "");
                                    holder.binding.ivImageFriend.setImageDrawable(dp);
                                } else {
                                    Glide.with(context.getApplicationContext())
                                            .load(imageURL)
                                            .into(holder.binding.ivImageFriend);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        } else {
            FirebaseDatabase.getInstance().getReference().child("chat").child(recentList.get(bpos).getChatID()).child("imageURL")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String imageURL = snapshot.getValue().toString();
                                if (imageURL == "No Image") {
                                    BitmapDrawable dp = getProfilePhoto(recentList.get(bpos).getUserName().charAt(0) + "");
                                    holder.binding.ivImageFriend.setImageDrawable(dp);
                                } else {
                                    Glide.with(context.getApplicationContext())
                                            .load(imageURL)
                                            .into(holder.binding.ivImageFriend);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return recentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemChatHeadsBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemChatHeadsBinding.bind(itemView);
        }
    }

    private BitmapDrawable getProfilePhoto(String first) {
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);

        Paint circlePaint = new Paint();
        circlePaint.setColor(Color.GREEN);

        int w = 30;  // Width of profile picture
        int h = 30;  // Height of profile picture
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(w / 2, h / 2, w / 2, circlePaint);
        canvas.drawText(first, 0, 0, textPaint);
        return new BitmapDrawable(context.getResources(), String.valueOf(canvas));
    }
}
