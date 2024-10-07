package com.pixelcraft.whisperlink.Adapter;

import static com.pixelcraft.whisperlink.Utils.NavigationHelper.navigateTo;
import static com.pixelcraft.whisperlink.Utils.TimeFormatterHelp.FormatTime;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.pixelcraft.whisperlink.Model.GetChat;
import com.pixelcraft.whisperlink.R;
import com.pixelcraft.whisperlink.Utils.Constans;
import com.pixelcraft.whisperlink.Utils.PreferencesManager;
import com.pixelcraft.whisperlink.activity.MessageActivity;
import com.pixelcraft.whisperlink.databinding.MychatsrecyclerviewrowBinding;
import com.pixelcraft.whisperlink.roomdb.ContactDao;
import com.pixelcraft.whisperlink.roomdb.ContactDatabase;
import com.pixelcraft.whisperlink.roomdb.ContactEntity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.chatHolder> {
    ArrayList<GetChat> getChats;
    PreferencesManager preferencesManager;
    private List<ContactEntity> contacts;
    public ChatsAdapter(ArrayList<GetChat> getChats, List<ContactEntity> contacts) {
        this.getChats = getChats;
        this.contacts = contacts;
    }
    @NonNull
    @Override
    public chatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new chatHolder(
                MychatsrecyclerviewrowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull chatHolder holder, int position) {
        preferencesManager = new PreferencesManager(holder.itemView.getContext());
        Intent intent = new Intent(holder.itemView.getContext(), MessageActivity.class);
        holder.binding.messageRecyclerviewLastMessage.setText(getChats.get(position).lastMessage);
        holder.binding.messageRecyclerviewDate.setText(FormatTime(getChats.get(position).timestamp, "dd.MM.yy"));
        holder.binding.messageRecyclerviewUserName.setText(getChats.get(position).receiverName);
        if (getChats.get(position).receiverImage.equals("")){
            preferencesManager.putString(Constans.KEY_RECEIVER_IMAGE,"no photo");
            holder.binding.messageRecyclerviewUserImage.setImageResource(R.drawable.user);
        }else{
            preferencesManager.putString(Constans.KEY_RECEIVER_IMAGE,getChats.get(position).receiverImage);
            Picasso.get().load(getChats.get(position).receiverImage).into(holder.binding.messageRecyclerviewUserImage);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencesManager.putString(Constans.KEY_RECEIVER_NAME,getChats.get(position).receiverName);
                preferencesManager.putString(Constans.KEY_FROM,Constans.KEY_FROM_CHAT);
                if (getChats.get(position).receiverImage.equals("")){
                    preferencesManager.putString(Constans.KEY_RECEIVER_IMAGE,"no photo");
                }else{
                    preferencesManager.putString(Constans.KEY_RECEIVER_IMAGE,getChats.get(position).receiverImage);
                }
                if (getChats.get(position).senderID.matches(preferencesManager.getString(Constans.KEY_USER_ID))){
                    preferencesManager.putString(Constans.KEY_RECEIVER_ID,getChats.get(position).receiverID);
                }else{
                    preferencesManager.putString(Constans.KEY_RECEIVER_ID,getChats.get(position).senderID);}
                navigateTo(holder.itemView.getContext(), MessageActivity.class, false);
            }
        });
    }
    @Override
    public int getItemCount() {
        return getChats.size();
    }
    class chatHolder extends RecyclerView.ViewHolder{
        MychatsrecyclerviewrowBinding binding;
        public chatHolder(MychatsrecyclerviewrowBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
