package com.pixelcraft.whisperlink.Utils;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pixelcraft.whisperlink.Model.MyContactsModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class GetMyContactsFromPhoneContacts {
    private Context myContext;
    ArrayList<MyContactsModel> contactsModels;

    public GetMyContactsFromPhoneContacts(Context myContext) {
        this.myContext = myContext;
    }
    public void getContact(ContactsCallback contactsCallback){
        ArrayList<String> phoneNumbers = new ArrayList<>();
        ArrayList<String> userNames = new ArrayList<>();
        ArrayList<String> numberSources = new ArrayList<>();
        ContentResolver contentResolver = myContext.getContentResolver();
        // Data tablosunu kullanarak hem telefon numarasına hem de account type'a eriş
        Cursor cursor = contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                new String[]{
                        ContactsContract.Data.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.RawContacts.ACCOUNT_TYPE
                },
                ContactsContract.Data.MIMETYPE + " = ?",
                new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE},
                null);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                // İsim ve telefon numarasını al
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                String phoneNumber = formatterPhoneNumber(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                String accountType = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE));
                // Account type'e göre source belirle
                String source="";
                if (accountType != null) {
                        if (accountType.contains("sim")) {
                            source = "SIM";
                        } else if (accountType.contains("google")) {
                            source = "Google";
                        } else {
                            source = "Phone/Other";
                        }
                    }
                // İsim, telefon numarası ve kaynağı listelere ekle
                phoneNumbers.add(phoneNumber);
                userNames.add(name);
                numberSources.add(source);  // Account Type'ı baz alarak source ekle
            }
            cursor.close();
        }

        // İşlenmiş rehber bilgilerini geri döndür
        contactsCallback.onContactGet(processContacts(userNames, phoneNumbers, numberSources));
    }
    public static String formatterPhoneNumber(String phoneNumber){
        phoneNumber = phoneNumber.trim();
        phoneNumber = phoneNumber.replaceAll("[^\\d]", "");
        if (!phoneNumber.startsWith("9")){
            if (phoneNumber.startsWith("0"))
                phoneNumber = "9"+phoneNumber;
            else if (phoneNumber.startsWith("5"))
                phoneNumber = "90"+phoneNumber;
        }
        return "+" + phoneNumber;
    }

    public ArrayList<MyContactsModel> processContacts(ArrayList<String> names, ArrayList<String> phoneNumbers, ArrayList<String> numberSources) {
        if (names.size() != phoneNumbers.size()) {
            System.out.println("Error: İsimler ve telefon numaraları listelerinin boyutları aynı olmalı.");
        }
        else{

            // Benzersiz telefon numaraları ve isimler için HashSet
            Set<String> uniquePhoneNumbers = new HashSet<>();
            ArrayList<String> finalContacts = new ArrayList<>();

            // Telefon numaralarını ve isimleri işleme
            for (int i = 0; i < phoneNumbers.size(); i++) {
                String cleanedNumber = formatterPhoneNumber(phoneNumbers.get(i));

                // Eğer telefon numarası benzersizse ekle
                if (uniquePhoneNumbers.add(cleanedNumber)) {
                    finalContacts.add("name:" + names.get(i) + " phone:" + phoneNumbers.get(i) + " source:" + numberSources.get(i));
                }
            }

            contactsModels = new ArrayList<>();
            String name = null;
            String phone = null;
            String source = null;
            for (String contact : finalContacts) {
                name = contact.substring(contact.indexOf("name:") + "name:".length(), contact.indexOf("phone:"));
                phone = contact.substring(contact.indexOf("phone:") + "phone:".length(), contact.indexOf("source:"));
                source = contact.substring(contact.indexOf("source:") + "source:".length());
                contactsModels.add(new MyContactsModel(name, phone,null, source));
            }
        }
        return contactsModels;
    }

    public interface ContactsCallback{
        void onContactGet(ArrayList<MyContactsModel> contactsModels);
    }
}
