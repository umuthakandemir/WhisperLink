package com.pixelcraft.whisperlink.Adapter;



import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.pixelcraft.whisperlink.Model.GetterMessage;
import com.pixelcraft.whisperlink.R;
import com.pixelcraft.whisperlink.Utils.Constans;
import com.pixelcraft.whisperlink.Utils.Firebase_Delete_Message;
import com.pixelcraft.whisperlink.Utils.PreferencesManager;
import com.pixelcraft.whisperlink.Utils.Progress_Dialog;
import com.pixelcraft.whisperlink.databinding.MessagesrecyclerviewrowBinding;
import com.pixelcraft.whisperlink.databinding.MessagesrecyclerviewrowsenderBinding;
import com.squareup.picasso.Picasso;

import org.checkerframework.common.subtyping.qual.Bottom;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<GetterMessage> messageArrayList;
    String userPhoto;
    String chatId;
    private PreferencesManager preferencesManager;
    public String username;
    FirebaseAuth mAuth;
    public MessagesAdapter(ArrayList<GetterMessage> messageArrayList, String userPhoto, String chatId) {
        this.messageArrayList = messageArrayList;
        this.userPhoto = userPhoto;
        this.chatId = chatId;
        this.mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public int getItemViewType(int position) {
        // Eğer mesajı gönderen kullanıcı ise, viewType 1 olsun, alıcı ise 0 olsun.
        if (messageArrayList.get(position).SenderID.matches(mAuth.getUid())) {
            return 1; // Gönderici mesajı
        } else {
            return 0; // Alıcı mesajı
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == 1) { // Gönderen mesajı
            MessagesrecyclerviewrowsenderBinding binding = MessagesrecyclerviewrowsenderBinding.inflate(inflater, parent, false);
            return new SenderMessagesHolder(binding);
        } else { // Alıcı mesajı
            preferencesManager = new PreferencesManager(parent.getContext());
            MessagesrecyclerviewrowBinding binding = MessagesrecyclerviewrowBinding.inflate(inflater, parent, false);
            return new ReceiverMessagesHolder(binding);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int view;
        GetterMessage getterMessage = messageArrayList.get(position);
        if (getterMessage.SenderID.matches(mAuth.getUid())){
            view = 1;
            ((SenderMessagesHolder) holder).binding.messageBodySender.setText(getterMessage.message);
            ((SenderMessagesHolder) holder).binding.dateMessagesSender.setText(getterMessage.date);
            if (getterMessage.messageReadStatus)
                ((SenderMessagesHolder) holder).binding.messageStatusMessages.setImageResource(R.drawable.read);
            else
                ((SenderMessagesHolder) holder).binding.messageStatusMessages.setImageResource(R.drawable.unread);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Progress_Dialog dialog = new Progress_Dialog("Please wiat. Message is delete..",null,holder.itemView.getContext());
                    dialog.show();
                    Firebase_Delete_Message.deleteMessage(chatId, messageArrayList.get(position).messageUID, new Firebase_Delete_Message.MessageDeleteCallback() {
                        @Override
                        public void onDeleteSuccess() {
                            dialog.dismiss();
                            Toast.makeText((view == 1)? ((SenderMessagesHolder) holder).itemView.getContext() :((ReceiverMessagesHolder) holder).itemView.getContext(),"Message is Deleted.",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onDeleteFailure(Exception e) {
                            dialog.dismiss();
                            Toast.makeText((view == 1)? ((SenderMessagesHolder) holder).itemView.getContext() : ((ReceiverMessagesHolder) holder).itemView.getContext(),"Message is not deleted. Faile:: "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    return true;
                }
            });
        }else{

            view = 0;
            ((ReceiverMessagesHolder) holder).binding.messageBody.setText(getterMessage.message);
            ((ReceiverMessagesHolder) holder).binding.dateMessages.setText(getterMessage.date);
            ((ReceiverMessagesHolder) holder).binding.userNameMessageRecyclerview.setText(preferencesManager.getString(Constans.KEY_RECEIVER_NAME));
            //no photo come to the from contactActivity
            if (position >0 && messageArrayList.get(position-1).SenderID.equals(getterMessage.SenderID) && getterMessage.date.substring(0,3).equals(messageArrayList.get(position-1).date.substring(0,3)) && !getterMessage.SenderID.equals(mAuth.getUid())){
                ((ReceiverMessagesHolder) holder).binding.imageCardViewMessages.setVisibility(View.INVISIBLE);
                ((ReceiverMessagesHolder) holder).binding.userImageMessages.setVisibility(View.INVISIBLE);
                ((ReceiverMessagesHolder) holder).binding.userNameMessageRecyclerview.setVisibility(View.GONE);
            }else {
                try {
                    ((ReceiverMessagesHolder) holder).binding.userNameMessageRecyclerview.setVisibility(View.VISIBLE);
                    ((ReceiverMessagesHolder) holder).binding.userImageMessages.setVisibility(View.VISIBLE);
                    ((ReceiverMessagesHolder) holder).binding.imageCardViewMessages.setVisibility(View.VISIBLE);
                    //((ReceiverMessagesHolder) holder).binding.userNameMessageRecyclerview.setText(setUserName());
                    if (!userPhoto.matches("no photo"))
                        Picasso.get().load(userPhoto).into(((ReceiverMessagesHolder) holder).binding.userImageMessages);
                    else
                        ((ReceiverMessagesHolder) holder).binding.userImageMessages.setImageResource(R.drawable.user);
                }
                catch (Exception e) {
                    Log.e("fail", e.getLocalizedMessage());
                }
            }

        }
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }
    public class ReceiverMessagesHolder extends RecyclerView.ViewHolder {
        MessagesrecyclerviewrowBinding binding;

        public ReceiverMessagesHolder(MessagesrecyclerviewrowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    public class SenderMessagesHolder extends RecyclerView.ViewHolder {

        MessagesrecyclerviewrowsenderBinding binding;

        public SenderMessagesHolder(MessagesrecyclerviewrowsenderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
