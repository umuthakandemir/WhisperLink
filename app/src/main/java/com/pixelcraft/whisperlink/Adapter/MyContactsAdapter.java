package com.pixelcraft.whisperlink.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pixelcraft.whisperlink.Model.GetMyContactFirestore;
import com.pixelcraft.whisperlink.R;
import com.pixelcraft.whisperlink.Utils.Constans;
import com.pixelcraft.whisperlink.Utils.PreferencesManager;
import com.pixelcraft.whisperlink.databinding.ContactsrecyclerviewrowBinding;
import com.pixelcraft.whisperlink.activity.MessageActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyContactsAdapter extends RecyclerView.Adapter<MyContactsAdapter.MyContactsHolder> {
    ArrayList<GetMyContactFirestore> contactFirestores;
    public MyContactsAdapter(ArrayList<GetMyContactFirestore> contactFirestores) {
        this.contactFirestores = contactFirestores;
    }

    @NonNull
    @Override
    public MyContactsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ContactsrecyclerviewrowBinding binding = ContactsrecyclerviewrowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyContactsHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyContactsHolder holder, int position) {
        Intent dataTransfer = new Intent(holder.itemView.getContext(), MessageActivity.class);
        holder.binding.contactsRecyclerviewUserName.setText(contactFirestores.get(position).userName);
        holder.binding.userStatus.setText(contactFirestores.get(position).userStatus);
        if (contactFirestores.get(position).userPhoto!=""){
            dataTransfer.putExtra(Constans.KEY_USER_IMAGE,contactFirestores.get(position).userPhoto);
            Picasso.get().load(contactFirestores.get(position).userPhoto).into(holder.binding.contactsRecyclerviewUserImage);
        }else{
            holder.binding.contactsRecyclerviewUserImage.setImageResource(R.drawable.user);
            dataTransfer.putExtra(Constans.KEY_USER_IMAGE,"no photo");
        }
        if (contactFirestores.get(position).sourceType.matches("SIM"))
            holder.binding.contactRecyclerviewNumberSource.setImageResource(R.drawable.simcard);
        else if (contactFirestores.get(position).sourceType.matches("Google"))
            holder.binding.contactRecyclerviewNumberSource.setImageResource(R.drawable.google);
        else if(contactFirestores.get(position).sourceType.matches("Phone/Other"))
            holder.binding.contactRecyclerviewNumberSource.setImageResource(R.drawable.mobilephone);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesManager preferencesManager = new PreferencesManager(holder.itemView.getContext());
                preferencesManager.putString(Constans.KEY_RECEIVER_NAME,contactFirestores.get(position).userName);
                dataTransfer.putExtra(Constans.KEY_RECEIVER_NAME,contactFirestores.get(position).userName);
                dataTransfer.putExtra(Constans.KEY_RECEIVER_ID,contactFirestores.get(position).userUID);
                dataTransfer.putExtra(Constans.KEY_USER_STATUS,contactFirestores.get(position).userStatus);
                preferencesManager.putString(Constans.KEY_FROM,Constans.KEY_FROM_CONTACT_ACTIVITY);
                holder.itemView.getContext().startActivity(dataTransfer);
                ((Activity) holder.itemView.getContext()).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactFirestores.size();
    }

    public class MyContactsHolder extends RecyclerView.ViewHolder{
        ContactsrecyclerviewrowBinding binding;
        public MyContactsHolder(ContactsrecyclerviewrowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
