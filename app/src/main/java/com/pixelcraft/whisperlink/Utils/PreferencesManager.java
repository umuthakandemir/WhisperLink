package com.pixelcraft.whisperlink.Utils;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferencesManager {
    private final SharedPreferences sharedPreferences;
    public PreferencesManager(Context mContext){
        this.sharedPreferences = mContext.getSharedPreferences(Constans.KEY_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public String getString(String key){
        return sharedPreferences.getString(key, null);
    }

    public void putString(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public Boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key, false);
    }

    public void putBoolean(String key, Boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public int getInteger(String key){
        return sharedPreferences.getInt(key, 0);
    }

    public void putInteger(String key, int value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }
}
