package com.pixelcraft.whisperlink.Firebase;

import static com.pixelcraft.whisperlink.Utils.TimeFormatterHelp.FormatTime;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.pixelcraft.whisperlink.Adapter.MessagesAdapter;
import com.pixelcraft.whisperlink.Model.GetterMessage;
import com.pixelcraft.whisperlink.R;
import com.pixelcraft.whisperlink.Utils.Constans;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class Firebase_Load_Message {

    // ID of the user receiving the message
    String receiverID;
    String userPhoto;

    // ID of the user sending the message
    String senderID;

    // Context - provides access to the application's current state and features
    Context myContext;

    // RecyclerView where messages will be displayed
    RecyclerView recyclerView;

    // Adapter used to display messages
    MessagesAdapter adapter;

    // ArrayList to hold messages
    ArrayList<GetterMessage> messageArrayList;

    // Firebase Firestore instance for database operations
    FirebaseFirestore db;

    // Firebase Authentication instance for user authentication
    FirebaseAuth mAuth;

    // Constructor - initializes the class with necessary parameters
    public Firebase_Load_Message(Context myContext, String senderID, String receiverID, String userPhoto, RecyclerView recyclerView) {
        this.myContext = myContext;  // Assigns the context
        this.receiverID = receiverID;  // Assigns the receiver user's ID
        this.recyclerView = recyclerView;  // Assigns the RecyclerView for displaying messages
        this.senderID = senderID;// Assigns the sender user's ID
        this.userPhoto = userPhoto;
        messageArrayList = new ArrayList<>();  // Initializes the list for messages
        db = FirebaseFirestore.getInstance();  // Initializes the Firestore database connection
        mAuth = FirebaseAuth.getInstance();  // Initializes Firebase authentication
        adapterInitials();  // Sets up the adapter and RecyclerView
        messageLoaderSnapshotListener();  // Sets up a listener for real-time message updates
    }

    // Method to initialize the RecyclerView adapter
    private void adapterInitials() {
        recyclerView.setLayoutManager(new LinearLayoutManager(myContext));  // Sets the layout manager
        adapter = new MessagesAdapter(messageArrayList, userPhoto,getterChatID());  // Initializes the adapter with the message list
        recyclerView.setAdapter(adapter);  // Sets the adapter for the RecyclerView
    }

    // Method to get the chat ID based on sender and receiver IDs
    private String getterChatID() {
        if (senderID.compareTo(receiverID) < 0) {
            return senderID.concat(receiverID);  // Concatenates IDs in lexicographical order
        } else
            return receiverID.concat(senderID);  // Concatenates IDs in lexicographical order
    }

    // Method to load messages with a snapshot listener for real-time updates
    private void messageLoaderSnapshotListener() {
        db.collection(Constans.KEY_COLLECTION_CHATS).document(getterChatID()).collection(Constans.KEY_COLLECTION_CHATS_MESSAGE).orderBy(Constans.KEY_MESSAGE_DATE, Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (!value.isEmpty()) {
                            ArrayList<GetterMessage> newArraylist = new ArrayList<>();
                            messageArrayList.clear();  // Clears the existing message list
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                Map<String, Object> snapshotMessage = snapshot.getData();  // Gets message data
                                String messageUID = snapshot.getId().toString();
                                String Message = (String) snapshotMessage.get(Constans.KEY_MESSAGE);  // Retrieves the message content;
                                String date = FormatTime(snapshot.getTimestamp(Constans.KEY_MESSAGE_DATE),"HH:mm");// Placeholder for the message date
                                Boolean messageReadStatus = (Boolean) snapshotMessage.get(Constans.KEY_MESSAGE_READ_STATUS);  // Retrieves read status
                                String receiverID = (String) snapshotMessage.get(Constans.KEY_RECEIVER_ID);  // Gets receiver ID
                                String senderID = (String) snapshotMessage.get(Constans.KEY_SENDER_ID);  // Gets sender ID
                                // Creates a new GetterMessage object with the retrieved data
                                newArraylist.add(new GetterMessage(messageUID, Message, date,senderID,messageReadStatus));
                            }

                            messageArrayList.clear();
                            messageArrayList.addAll(newArraylist);
                            adapter.notifyDataSetChanged();  // Notifies the adapter of data changes
                            recyclerView.scrollToPosition(adapter.getItemCount() - 1);  // Scrolls to the last message
                        }
                        if (error != null) {
                            Toast.makeText(myContext, R.string.Fail, Toast.LENGTH_SHORT).show();  // Displays an error message
                        }
                    }
                });
    }

}
