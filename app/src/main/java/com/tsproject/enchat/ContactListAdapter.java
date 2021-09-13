package com.tsproject.enchat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder>{
    private ArrayList<User> contactList = new ArrayList<>();
    private Context context;
    public ContactListAdapter()
    {

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

    @Override
    public void onBindViewHolder(@NonNull ContactListAdapter.ViewHolder holder, int position) {
            holder.tvContactName.setText(contactList.get(position).contactName);
            holder.tvContactNumber.setText(contactList.get(position).phnNum);
    }

    @Override
    public int getItemCount() {
        return contactList.size();

    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvContactName, tvContactNumber;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContactName = itemView.findViewById(R.id.tvContactName);
            tvContactNumber = itemView.findViewById(R.id.tvContactNumber);
        }
    }
}
