package com.pixelcraft.whisperlink.Utils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class Firebase_Delete_Message {
    public interface MessageDeleteCallback{
        void onDeleteSuccess();
        void onDeleteFailure(Exception e);
    }
    public static void deleteMessage(String chatID, String messageUID, MessageDeleteCallback callback){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Chats").document(chatID).collection("Messages").document(messageUID).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        callback.onDeleteSuccess();
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onDeleteFailure(e);
                    }
                });
    }
}
