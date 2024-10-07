package com.pixelcraft.whisperlink.Firebase;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pixelcraft.whisperlink.Model.GetMyContactFirestore;
import com.pixelcraft.whisperlink.Model.MyContactsModel;
import com.pixelcraft.whisperlink.Utils.Constans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ContactFirestoreCompare {

    public interface compareListCallback {
        void onCallback(ArrayList<GetMyContactFirestore> contactFirestores);
    }
    static int count = 0;
    public static void firestoreCompare(FirebaseAuth mAuth, ArrayList<MyContactsModel> contactsModels, FirebaseFirestore db, compareListCallback callback) {
        ArrayList<GetMyContactFirestore> contactFirestores = new ArrayList<>();
        Set<String> uniqueUIDs = new HashSet<>();  // Benzersiz kullanıcı UID'lerini tutmak için set

        for (MyContactsModel contactsModel : contactsModels) {
            db.collection(Constans.KEY_COLLECTION_USER)
                    .whereEqualTo(Constans.KEY_USER_PHONE_NUMBER, contactsModel.userPhone.trim())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                String userUID = snapshot.getString(Constans.KEY_USER_ID);  // Kullanıcı UID'sini al
                                String userStatus = snapshot.getString(Constans.KEY_USER_STATUS);
                                String userPhoto = snapshot.getString(Constans.KEY_USER_IMAGE);
                                String userPhone = snapshot.getString(Constans.KEY_USER_PHONE_NUMBER);

                                // Eğer UID benzersizse listeye ekle
                                if (!mAuth.getUid().equals(userUID) && uniqueUIDs.add(userUID)) {
                                    contactFirestores.add(new GetMyContactFirestore(userPhoto, contactsModel.userName, userStatus, contactsModel.sourceType, userUID));
                                }
                            }
                            // Son olarak listeyi sıralayıp geri döndür
                            Collections.sort(contactFirestores, (obj1, obj2) -> obj1.userName.compareTo(obj2.userName));
                            callback.onCallback(contactFirestores);
                        } else {
                            count ++;
                            if (count == contactsModels.size())
                                callback.onCallback(contactFirestores);
                            Log.d("Eşleşen kişi bulunamadı: ", contactsModel.userPhone);
                        }
                    });
        }
    }
}
