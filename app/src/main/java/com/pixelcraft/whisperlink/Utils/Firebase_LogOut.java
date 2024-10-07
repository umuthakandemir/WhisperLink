package com.pixelcraft.whisperlink.Utils;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;

public class Firebase_LogOut {
    Context myContext;
    FirebaseAuth mAuth;
    public Firebase_LogOut(Context myContext) {
        this.myContext = myContext;
        mAuth = FirebaseAuth.getInstance();
    }
    public void logOut()
    {
        mAuth.signOut();
    }
}
