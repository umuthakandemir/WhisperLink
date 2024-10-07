package com.pixelcraft.whisperlink.activity;

import static com.pixelcraft.whisperlink.Utils.NavigationHelper.navigateTo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.common.graph.Network;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.pixelcraft.whisperlink.Firebase.Firebase_Load_Message;
import com.pixelcraft.whisperlink.Firebase.SendMessage;
import com.pixelcraft.whisperlink.R;
import com.pixelcraft.whisperlink.Utils.Constans;
import com.pixelcraft.whisperlink.Utils.NetworkUtil;
import com.pixelcraft.whisperlink.Utils.PermissionManager;
import com.pixelcraft.whisperlink.Utils.PreferencesManager;
import com.pixelcraft.whisperlink.databinding.ActivityMessageBinding;
import com.squareup.picasso.Picasso;

public class MessageActivity extends AppCompatActivity {

    // ID of the user to whom the message is being sent
    String receivedID="";
    String receiverImage = "";
    PreferencesManager preferencesManager;

    // Status indicating if the message was sent successfully
    Boolean messageSendStatus;

    // Firebase Authentication instance to manage the user's authentication state
    FirebaseAuth mAuth;

    // Firebase Firestore instance to interact with Firestore database
    FirebaseFirestore db;

    // Document reference pointing to the user's profile information document in Firestore
    DocumentReference mRef;

    // View binding for the activity layout, to access UI elements directly
    private ActivityMessageBinding binding;

    // Object for sending messages
    SendMessage sendMessage;

    // Object for loading messages from Firebase
    Firebase_Load_Message load_message;
    NetworkUtil networkUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.navigateToMyChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(MessageActivity.this,MyChats.class,true);
            }
        });
        // Initialize Firebase authentication instance
        mAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Firestore instance
        db = FirebaseFirestore.getInstance();
        preferencesManager = new PreferencesManager(this);
        receiverImage = preferencesManager.getString(Constans.KEY_RECEIVER_IMAGE);
        if (preferencesManager.getString(Constans.KEY_FROM).equals(Constans.KEY_FROM_CHAT)){
            binding.userNameMessages.setText(preferencesManager.getString(Constans.KEY_RECEIVER_NAME));
            if (receiverImage.equals("no photo")){
                binding.messageProfileImage.setImageResource(R.drawable.user);
            }else {
                Picasso.get().load(receiverImage).into(binding.messageProfileImage);
            }
                binding.userNameMessages.setText(preferencesManager.getString(Constans.KEY_RECEIVER_NAME));
                load_message = new Firebase_Load_Message(this,mAuth.getUid(),preferencesManager.getString(Constans.KEY_RECEIVER_ID),receiverImage,binding.messagesRecyclerview);
        }else if (preferencesManager.getString(Constans.KEY_FROM).equals(Constans.KEY_FROM_CONTACT_ACTIVITY)){
            String userPhoto = getDataFromIntent();
            binding.userNameMessages.setText(getIntent().getStringExtra(Constans.KEY_RECEIVER_NAME));
            load_message = new Firebase_Load_Message(this, mAuth.getUid(), receivedID, userPhoto, binding.messagesRecyclerview);
        }
      // updateReadMessage();
        // Initialize the object to load messages between users

    }
    private String getChatID() {
        String chatID;
        if (mAuth.getUid().compareTo(receivedID) < 0){
            chatID = mAuth.getUid().concat(receivedID);
            return chatID;
        }else {
            chatID = receivedID.concat(mAuth.getUid());
            return chatID;
        }
    }
    private void updateReadMessage(){
        db.collection("Chats").document(getChatID()).collection("Messages").whereEqualTo("messageReadStatus",false).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                 //   for (DocumentSnapshot snapshot:value.getDocuments()){
                       // db.collection("Chats").document(getChatID()).collection("Messages").document(snapshot.getId()).update("messageReadStatus",true);
                   // }
                }else
                    Toast.makeText(MessageActivity.this,R.string.Fail+error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getDataFromIntent() {
        // load the user's profile photo
        Intent intent = getIntent();
        try {
            if (!intent.getStringExtra(Constans.KEY_USER_IMAGE).matches("no photo"))
                Picasso.get().load(intent.getStringExtra(Constans.KEY_USER_IMAGE)).into(binding.messageProfileImage);
            else
                binding.messageProfileImage.setImageResource(R.drawable.user);
            receivedID = getIntent().getStringExtra(Constans.KEY_RECEIVER_ID);
        }catch (Exception e){
            Log.e("Failure:: ",e.getLocalizedMessage());
        }
        return intent.getStringExtra(Constans.KEY_USER_IMAGE);
    }
    // Method called when the user clicks the "Send Message" button
    public void sendMessageClick(View view) {
        String message = binding.messageEdittext.getText().toString().trim();
        if (!message.isEmpty()){
            if (preferencesManager.getString(Constans.KEY_FROM).equals(Constans.KEY_FROM_CHAT))
                // Create a new SendMessage object with message text, receiver ID, and sender ID
                sendMessage = new SendMessage(MessageActivity.this,message, preferencesManager.getString(Constans.KEY_RECEIVER_ID), receiverImage,mAuth.getUid(), 2);
            else
                // Create a new SendMessage object with message text, receiver ID, and sender ID
                sendMessage = new SendMessage(MessageActivity.this,message, receivedID,getDataFromIntent(), mAuth.getUid(), 2);

            // Clear the EditText field after sending the message
        binding.messageEdittext.setText("");

        // Send the message and check if it was sent successfully
        sendMessage.SendMessage(new SendMessage.MessageSendCallback() {
            @Override
            public void onMessageSendSuccess() {
                // Update the messageSendStatus to true if message was sent successfully
                messageSendStatus = true;
            }

            @Override
            public void onMessageSendFailed(Exception e) {
                // Update the messageSendStatus to false if message sending failed
                messageSendStatus = false;
            }
        });
        }else {
            Toast.makeText(MessageActivity.this,R.string.enterMessage,Toast.LENGTH_SHORT).show();
        }
    }
}
