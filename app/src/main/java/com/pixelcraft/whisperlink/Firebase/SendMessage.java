package com.pixelcraft.whisperlink.Firebase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.speech.SpeechRecognizer;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pixelcraft.whisperlink.Utils.Constans;
import com.pixelcraft.whisperlink.Utils.PermissionManager;
import com.pixelcraft.whisperlink.Utils.PreferencesManager;

import org.checkerframework.checker.units.qual.A;

import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class SendMessage {
    private PreferencesManager preferencesManager;
    // Message content to be sent
    String Message;
    // Receiver's ID
    String ReceiverID, receiverName;
    String receiverImage;
    // Sender's ID
    String SenderID;
    int chatUserCount;
    // Message read status, initialized to false
    Boolean messageReadStatus = false;
    // Firestore instance
    FirebaseFirestore db;

    // Constructor to initialize message, receiver, and sender information
    public SendMessage(Context mContext, String message, String receiverID, String receiverImage, String senderID, int chatUserCount) {
        this.Message = message;
        this.chatUserCount = chatUserCount;
        this.ReceiverID = receiverID;
        this.receiverImage = receiverImage;
        this.SenderID = senderID;
        preferencesManager = new PreferencesManager(mContext);
        db = FirebaseFirestore.getInstance();
        generateChatID();
    }

    // Callback interface for message sending status
    public interface MessageSendCallback {
        void onMessageSendSuccess();
        void onMessageSendFailed(Exception e);
    }

    // Method to generate a unique Chat ID based on Sender and Receiver IDs
    public String generateChatID() {
        String chatID;
        if (SenderID.compareTo(ReceiverID) < 0){
            chatID = SenderID.concat(ReceiverID);
            return chatID;
        }else {
            chatID = ReceiverID.concat(SenderID);
            return chatID;
        }
    }

    // Method to send the message to Firestore
    @SuppressLint("NotConstructor")
    public void SendMessage(MessageSendCallback sendCallback) {
        // Generate a unique message ID
        UUID messageID = UUID.randomUUID();
        // Create a HashMap to store message details
        HashMap<String, Object> MessageContent = new HashMap<>();
        MessageContent.put(Constans.KEY_MESSAGE, Message); // Store the message content
        MessageContent.put(Constans.KEY_RECEIVER_ID, ReceiverID); // Store the receiver's ID
        MessageContent.put(Constans.KEY_SENDER_ID, SenderID); // Store the sender's ID
        MessageContent.put(Constans.KEY_MESSAGE_READ_STATUS, messageReadStatus); // Store message read status
        MessageContent.put(Constans.KEY_MESSAGE_DATE, FieldValue.serverTimestamp()); // Store server timestamp
        //Create a HashMap to use user to chat
        HashMap<String, Object> ChatUsers = new HashMap<>();
        ChatUsers.put("user1",ReceiverID);
        ChatUsers.put("user2",SenderID);
        db.collection(Constans.KEY_COLLECTION_CHATS).document(generateChatID()).set(ChatUsers);
        // Save the message in Firestore under the 'Chats' collection with the generated Chat ID
        db.collection(Constans.KEY_COLLECTION_CHATS).document(generateChatID()).collection(Constans.KEY_COLLECTION_CHATS_MESSAGE).document(messageID.toString()).set(MessageContent)
                // On successful message send, invoke the success callback
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        addChatsUIDProfiles();
                        sendCallback.onMessageSendSuccess(); // Callback with success
                    }
                })
                // On message send failure, invoke the failure callback
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        sendCallback.onMessageSendFailed(e); // Callback with failure
                    }
                });
    }
    private void addChatsUIDProfiles(){
        HashMap<String,Object> data = new HashMap<>();
        data.put(Constans.KEY_RECEIVER_ID,ReceiverID);
        data.put(Constans.KEY_SENDER_ID, SenderID);
        data.put(Constans.KEY_COLLECTION_LAST_MESSAGE_IN_CHATS,Message);
        data.put(Constans.KEY_MESSAGE_DATE,FieldValue.serverTimestamp());
        data.put(Constans.KEY_SENDER_IMAGE,preferencesManager.getString(Constans.KEY_USER_IMAGE));
        data.put(Constans.KEY_SENDER_PHONE_NUMBER,preferencesManager.getString(Constans.KEY_USER_PHONE_NUMBER));
        db.collection(Constans.KEY_COLLECTION_USER).document(ReceiverID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    data.put(Constans.KEY_RECEIVER_PHONE_NUMBER,task.getResult().getString(Constans.KEY_USER_PHONE_NUMBER));
                    data.put(Constans.KEY_RECEIVER_IMAGE,task.getResult().getString(Constans.KEY_USER_IMAGE));
                    db.collection(Constans.KEY_COLLECTION_MY_CHAT).document(generateChatID()).set(data);
                }
            }
        });
    }

}
