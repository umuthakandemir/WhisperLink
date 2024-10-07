package com.pixelcraft.whisperlink.Model;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class GetterMessage {
    // The content of the message
    public String message;

    public String messageUID;
    // The date when the message was sent
    public String date;
    public String SenderID;

    // URL of the user's image (profile picture) associated with the message
    // Status indicating if the message has been read (true if read, false otherwise)
    public Boolean messageReadStatus;

    // Firestore instance for database interactions (not used in the constructor)
    FirebaseFirestore db;

    // Constructor to initialize the message details
    public GetterMessage(String messageUID, String message, String date, String SenderID, Boolean messageReadStatus) {
        this.message = message;  // Assigns the message content
        this.messageUID = messageUID;
        this.date = date;  // Assigns the date the message was sent
        this.messageReadStatus = messageReadStatus;  // Assigns the read status of the message
        this.SenderID = SenderID;
    }
}
