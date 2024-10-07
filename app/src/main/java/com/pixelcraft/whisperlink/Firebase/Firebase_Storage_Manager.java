package com.pixelcraft.whisperlink.Firebase;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pixelcraft.whisperlink.R;
import com.pixelcraft.whisperlink.Utils.Constans;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class Firebase_Storage_Manager {
    Uri uriPhoto;
    Context myContext;
    String userPath;
    FirebaseUser mUser;
    FirebaseAuth mAuth;
    FirebaseStorage firebaseStorage;
    StorageReference mStorage;
    public interface UriCallback{
        void onUriReceived(Uri uri);
    }
    public Firebase_Storage_Manager(Context myContext, Uri uriPhoto){
        this.uriPhoto = uriPhoto;
        this.myContext = myContext;
        firebaseStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStorage = firebaseStorage.getReference();
    }
    public void addPhototomStorage(UriCallback callback){
        userPath = Constans.KEY_COLLECTION_USER+"/"+mAuth.getUid()+"/profilePhoto/userProfilePhoto.jpeg";
        mStorage.child(userPath).putFile(uriPhoto)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mStorage.child(userPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                callback.onUriReceived(uri);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(myContext, R.string.Fail+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
