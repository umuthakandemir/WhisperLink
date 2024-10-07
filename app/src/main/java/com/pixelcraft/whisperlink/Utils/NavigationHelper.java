package com.pixelcraft.whisperlink.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class NavigationHelper {
    public static void navigateTo(Context currentActivity, Class<?> targetActivity, boolean finishCurrent){
        Intent intent = new Intent(currentActivity, targetActivity);
        currentActivity.startActivity(intent);
        if (finishCurrent && currentActivity instanceof Activity){
            ((Activity) currentActivity).finish();
        }
    }
}
