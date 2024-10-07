package com.pixelcraft.whisperlink.Utils;

import android.content.Context;
import android.widget.Toast;

public class ShowToast {
    public static void showToast(Context mContext, int message){
        Toast.makeText(mContext.getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
}
