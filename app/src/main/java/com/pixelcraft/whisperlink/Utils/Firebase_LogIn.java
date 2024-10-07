package com.pixelcraft.whisperlink.Utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pixelcraft.whisperlink.R;
import com.pixelcraft.whisperlink.databinding.ActivityLoginBinding;
import com.pixelcraft.whisperlink.activity.MyChats;

public class Firebase_LogIn {
    String email;
    String password;
    Context myContext;
    private ActivityLoginBinding binding;
    private PreferencesManager preferencesManager;
    FirebaseAuth mAuth;
    Progress_Dialog progressDialog;

    public Firebase_LogIn(String email, String password, Context myContext, ActivityLoginBinding binding) {
        this.email = email;
        this.password = password;
        this.myContext = myContext;
        this.binding = binding;
        preferencesManager = new PreferencesManager(myContext);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new Progress_Dialog("Please Wait..",null,myContext);
    }
    private void setUserInfo(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constans.KEY_COLLECTION_USER).document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                progressDialog.dismiss();
                binding.loginButton.setEnabled(true);
                preferencesManager.putString(Constans.KEY_USER_ID,mAuth.getUid());
                preferencesManager.putString(Constans.KEY_USER_PHONE_NUMBER,task.getResult().getString(Constans.KEY_USER_PHONE_NUMBER));
                NavigationHelper.navigateTo(myContext, MyChats.class,true);
            }
        });
    }
    public void LogIn(){
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnFailureListener((Activity) myContext, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.loginButton.setEnabled(true);
                        progressDialog.dismiss();
                        Log.e("Firestore","Faile: "+e.getLocalizedMessage());
                        Toast.makeText(myContext, R.string.Fail+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener((Activity) myContext, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        setUserInfo();
                    }
                });
    }
}
