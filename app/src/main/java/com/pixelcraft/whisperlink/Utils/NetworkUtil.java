package com.pixelcraft.whisperlink.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;



public class NetworkUtil{

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
           NetworkInfo[] infos = cm.getAllNetworkInfo();
           if (infos!=null){
               for (int i=0;i<infos.length;i++){
                   if (infos[i].getState() == NetworkInfo.State.CONNECTED)
                       return true;
               }
           }
        }
        return false;
    }
}
