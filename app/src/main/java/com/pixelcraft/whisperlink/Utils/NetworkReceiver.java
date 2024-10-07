package com.pixelcraft.whisperlink.Utils;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.pixelcraft.whisperlink.R;
import com.pixelcraft.whisperlink.activity.NotNetwork;

public class NetworkReceiver extends BroadcastReceiver {
    Button retry_button;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!NetworkUtil.isConnected(context)){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View noNetwork_Layout = LayoutInflater.from(context).inflate(R.layout.no_network, null);
            builder.setView(noNetwork_Layout);

            retry_button = noNetwork_Layout.findViewById(R.id.retry);
            retry_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onReceive(context,intent);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.show();;
        }
    }
}
