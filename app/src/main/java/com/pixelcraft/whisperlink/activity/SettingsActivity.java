package com.pixelcraft.whisperlink.activity;

import static com.pixelcraft.whisperlink.Utils.NavigationHelper.navigateTo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.pixelcraft.whisperlink.R;
import com.pixelcraft.whisperlink.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.SettingsNavigateToMyChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(SettingsActivity.this,MyChats.class,true);
            }
        });
    }
}