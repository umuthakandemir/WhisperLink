package com.pixelcraft.whisperlink.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.pixelcraft.whisperlink.R;
import com.pixelcraft.whisperlink.Utils.NetworkUtil;
import com.pixelcraft.whisperlink.databinding.ActivityNotNetworkBinding;

public class NotNetwork extends AppCompatActivity {
    private ActivityNotNetworkBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotNetworkBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // GIF dosyasının URL'sini veya yerel dosya yolunu belirtin
        //String gifUrl = "https://example.com/path/to/your.gif"; // URL kullanıyorsanız
        String gifPath = "android.resource://" + getPackageName() + "/" + R.drawable.nonetwork; // Yerel dosya kullanıyorsanız

        // Glide ile GIF'i yükleyin
        Glide.with(this)
                .load(gifPath) // veya gifPath
                .into(binding.nonetworkMage);

    }
}
