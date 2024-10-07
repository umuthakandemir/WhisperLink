package com.pixelcraft.whisperlink.Firebase;

import static com.pixelcraft.whisperlink.Utils.TimeFormatterHelp.FormatTime;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;

import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.pixelcraft.whisperlink.Model.GetChat;
import com.pixelcraft.whisperlink.Utils.Constans;
import com.pixelcraft.whisperlink.Utils.PermissionManager;
import com.pixelcraft.whisperlink.Utils.PreferencesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Load_Chats {
    private PreferencesManager preferencesManager;
    private ArrayList<GetChat> getChats = new ArrayList<>();

    public static void loadChats(Context mContext, FirebaseFirestore db, chatCallback callback) {
        PreferencesManager preferencesManager = new PreferencesManager(mContext);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = preferencesManager.getString(Constans.KEY_USER_ID);
        ArrayList<GetChat> getChats = new ArrayList<>();

        // Gönderilen mesajlar için dinleyici
        db.collection(Constans.KEY_COLLECTION_MY_CHAT)
                .whereEqualTo(Constans.KEY_SENDER_ID, currentUserId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }

                        if (value != null) {
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                String chatID = snapshot.getId();
                                String lastMessage = snapshot.getString(Constans.KEY_COLLECTION_LAST_MESSAGE_IN_CHATS);
                                String receiverID = snapshot.getString(Constans.KEY_RECEIVER_ID);
                                String senderID = snapshot.getString(Constans.KEY_SENDER_ID);
                                String receiverImage = snapshot.getString(Constans.KEY_RECEIVER_IMAGE);
                                Timestamp timestamp = snapshot.getTimestamp(Constans.KEY_MESSAGE_DATE);
                                String phoneNumber = snapshot.getString(Constans.KEY_RECEIVER_PHONE_NUMBER);
                                String contactName = getContactNameFromContacts(mContext,phoneNumber);
                                // Aynı chatID'ye sahip bir mesaj varsa onu güncelle, yoksa ekle
                                boolean isUpdated = false;
                                for (GetChat chat : getChats) {
                                    if (chat.chatID.equals(chatID)) {
                                        chat.lastMessage = lastMessage;
                                        chat.timestamp = timestamp;
                                        isUpdated = true;
                                        break;
                                    }
                                }
                                if (!isUpdated) {
                                    getChats.add(new GetChat(chatID, lastMessage, receiverID, contactName, receiverImage, senderID, timestamp));
                                }
                            }
                            Collections.sort(getChats, new Comparator<GetChat>() {
                                @Override
                                public int compare(GetChat obj1, GetChat obj2) {
                                    if (obj1.timestamp == null && obj2.timestamp == null) {
                                        return 0;
                                    }
                                    if (obj1.timestamp == null) {
                                        return -1;
                                    }
                                    if (obj2.timestamp == null) {
                                        return 1;
                                    }
                                    // Son mesaj tarihine göre sıralama (en yeni önce)
                                    return obj2.timestamp.compareTo(obj1.timestamp);
                                }
                            });

                            callback.onCallback(getChats);
                        }
                    }
                });

        // Alınan mesajlar için dinleyici
        db.collection(Constans.KEY_COLLECTION_MY_CHAT)
                .whereEqualTo(Constans.KEY_RECEIVER_ID, currentUserId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null)
                            return;

                        if (value != null) {
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                String chatID = snapshot.getId();
                                String lastMessage = snapshot.getString(Constans.KEY_COLLECTION_LAST_MESSAGE_IN_CHATS);
                                String receiverID = snapshot.getString(Constans.KEY_RECEIVER_ID);
                                String senderID = snapshot.getString(Constans.KEY_SENDER_ID);
                                String receiverImage = snapshot.getString(Constans.KEY_SENDER_IMAGE);
                                Timestamp timestamp = snapshot.getTimestamp(Constans.KEY_MESSAGE_DATE);
                                String phoneNumber = snapshot.getString(Constans.KEY_SENDER_PHONE_NUMBER);
                                String contactName = getContactNameFromContacts(mContext,phoneNumber);
                                // Aynı chatID'ye sahip bir mesaj varsa onu güncelle, yoksa ekle
                                boolean isUpdated = false;
                                for (GetChat chat : getChats) {
                                    if (chat.chatID.equals(chatID)) {
                                        chat.lastMessage = lastMessage;
                                        chat.timestamp = timestamp;
                                        isUpdated = true;
                                        break;
                                    }
                                }
                                if (!isUpdated) {
                                    getChats.add(new GetChat(chatID, lastMessage, receiverID, contactName, receiverImage, senderID, timestamp));
                                }
                            }
                            Collections.sort(getChats, new Comparator<GetChat>() {
                                @Override
                                public int compare(GetChat obj1, GetChat obj2) {
                                    if (obj1.timestamp == null && obj2.timestamp == null) {
                                        return 0;
                                    }
                                    if (obj1.timestamp == null) {
                                        return -1;
                                    }
                                    if (obj2.timestamp == null) {
                                        return 1;
                                    }
                                    // Son mesaj tarihine göre sıralama (en yeni önce)
                                    return obj2.timestamp.compareTo(obj1.timestamp);
                                }
                            });
                            callback.onCallback(getChats);
                        }
                    }
                });
    }
    public static String getContactNameFromContacts(Context context,String phoneNumber){
        // Numaranın biçimini normalize et
        String normalizedNumber = PhoneNumberUtils.normalizeNumber(phoneNumber);
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        Cursor cursor = contentResolver.query(uri, projection, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                // Rehberdeki numaranın da biçimini normalize et
                String normalizedContactNumber = PhoneNumberUtils.normalizeNumber(contactNumber);

                // Normalize edilmiş numaralar eşleşiyorsa ismi döndür
                if (PhoneNumberUtils.compare(normalizedNumber, normalizedContactNumber)) {
                    cursor.close();
                    return contactName;
                }
            }
            cursor.close();
        }
        return normalizedNumber;  // Eğer isim bulunamazsa null döner
    }
    public interface chatCallback {
        void onCallback(ArrayList<GetChat> chatArrayList);
    }
}
