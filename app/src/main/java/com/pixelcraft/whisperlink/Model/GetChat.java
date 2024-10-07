package com.pixelcraft.whisperlink.Model;

import com.google.firebase.Timestamp;

public class GetChat {
    public String chatID, lastMessage, receiverID, receiverName, receiverImage, senderID;
    public Timestamp timestamp;

    public GetChat(String chatID, String lastMessage, String receiverID, String receiverName, String receiverImage,String senderID, Timestamp timestamp) {
        this.chatID = chatID;
        this.lastMessage = lastMessage;
        this.receiverID = receiverID;
        this.receiverImage = receiverImage;
        this.receiverName = receiverName;
        this.senderID = senderID;
        this.timestamp = timestamp;
    }
}
