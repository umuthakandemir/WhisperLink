package com.pixelcraft.whisperlink.Utils;

import static com.pixelcraft.whisperlink.Utils.GetMyContactsFromPhoneContacts.formatterPhoneNumber;
import static com.pixelcraft.whisperlink.Utils.ShowToast.showToast;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pixelcraft.whisperlink.Firebase.Firebase_Storage_Manager;
import com.pixelcraft.whisperlink.R;
import com.pixelcraft.whisperlink.databinding.ActivitySigninBinding;
import com.pixelcraft.whisperlink.activity.MyChats;

import org.checkerframework.checker.units.qual.C;

import java.util.HashMap;

public class Firebase_SignIn {
    Context myContext;
    private ActivitySigninBinding binding;
    String email;
    String password;
    String nameSurname;
    String Phone;
    Uri userPhoto;
    String userprofileDownloadUri="";
    Progress_Dialog progressDialog;
    private PreferencesManager preferencesManager;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore firestore;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    public Firebase_SignIn(Context myContext, ActivitySigninBinding binding, String email, String password, String nameSurname, String Phone, Uri userPhoto) {
        this.myContext = myContext;
        this.binding = binding;
        this.email = email;
        this.password = password;
        this.nameSurname = nameSurname;
        this.Phone = Phone;
        this.userPhoto = userPhoto;
        progressDialog = new Progress_Dialog("Please Wait..",null,myContext);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
    }
    public void SignIn(){
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener((Activity) myContext, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        firestoreUserInformation();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener((Activity) myContext, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        showToast(myContext, Integer.parseInt(R.string.Fail+e.getLocalizedMessage()));
                    }
                });
    }
    private void putUserInfoToPreferences(){
        preferencesManager = new PreferencesManager(myContext);
        preferencesManager.putString(Constans.KEY_USER_EMAIL, email);
        preferencesManager.putString(Constans.KEY_USER_ID, mAuth.getUid());
        preferencesManager.putString(Constans.KEY_USER_NAME_SURNAME,nameSurname);
        preferencesManager.putString(Constans.KEY_USER_PHONE_NUMBER,Phone);
    }
    //Firebase_Storage_Manager class Download UriCallback
    private void firestoreUserInformation() {
        HashMap<String,Object> userInfo = new HashMap();
        userInfo.put(Constans.KEY_USER_EMAIL,email);
        userInfo.put(Constans.KEY_USER_NAME_SURNAME,nameSurname.toUpperCase());
        userInfo.put(Constans.KEY_USER_PHONE_NUMBER,formatterPhoneNumber(Phone));
        userInfo.put(Constans.KEY_USER_PASSWORD,password);
        userInfo.put(Constans.KEY_USER_SIGN_OUT_DATE, FieldValue.serverTimestamp());
        userInfo.put(Constans.KEY_USER_ID, mAuth.getUid());
        userInfo.put(Constans.KEY_USER_STATUS,"");
        if (userPhoto!=null){
            Firebase_Storage_Manager storageManager = new Firebase_Storage_Manager(myContext,userPhoto);
            storageManager.addPhototomStorage(new Firebase_Storage_Manager.UriCallback() {
                @Override
                public void onUriReceived(Uri uri) {
                    userInfo.put(Constans.KEY_USER_IMAGE,uri.toString());
                    infoAddFirebase(userInfo);
                }
            });
        }else{
            userInfo.put(Constans.KEY_USER_IMAGE,"");
            infoAddFirebase(userInfo);
        }
    }

    public void infoAddFirebase(HashMap<String, Object> userInfo){
        firestore.collection(Constans.KEY_COLLECTION_USER).document(mAuth.getUid()).set(userInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        binding.SignInButton.setEnabled(true);
                        putUserInfoToPreferences();
                        NavigationHelper.navigateTo(myContext, MyChats.class,true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.SignInButton.setEnabled(true);
                        Firebase_Delete_User deleteUser = new Firebase_Delete_User(myContext);
                        deleteUser.UserDelete(new Firebase_Delete_User.UserDeleteCallback() {
                            @Override
                            public void onDeleteComplete(Boolean b) {
                                if (b){
                                    Toast.makeText(myContext, R.string.Fail,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
    }
}
