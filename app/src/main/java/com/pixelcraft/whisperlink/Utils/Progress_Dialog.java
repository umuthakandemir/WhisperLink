package com.pixelcraft.whisperlink.Utils;

import android.app.ProgressDialog;
import android.content.Context;

public class Progress_Dialog {
    String title,message;
    Context myContext;
    private ProgressDialog dialog;
    public Progress_Dialog(String title, String message, Context myContext) {
        this.title = title;
        this.message = message;
        this.myContext = myContext;
        dialog = new ProgressDialog(myContext);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
    }
    public void show(){
        if(dialog != null && !dialog.isShowing()){
            dialog.show();
        }
    }
    public void dismiss(){
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }
}
