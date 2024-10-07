package com.pixelcraft.whisperlink.Utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Firebase_Delete_User {
    Context myContext;
    FirebaseUser mUser;
    public Firebase_Delete_User(Context myContext){
        this.myContext=myContext;
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }
    public interface UserDeleteCallback{
        void onDeleteComplete(Boolean b);
    }
    public void UserDelete(UserDeleteCallback callback){
        if (mUser != null){
            mUser.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete()){
                                callback.onDeleteComplete(true);
                            }else
                                callback.onDeleteComplete(false);
                        }
                    });
        }
    }
}
