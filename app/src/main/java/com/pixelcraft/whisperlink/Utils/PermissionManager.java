package com.pixelcraft.whisperlink.Utils;// utils/PermissionManager.java
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import com.google.android.material.snackbar.Snackbar;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionManager {

    private final Activity activity;
    private View view;
    private final PermissionCallback callback;
    private final String[] permissions;
    private final int REQUEST_CODE = 100;

    // Constructor, aktiviteyi ve izinleri alıyor
    public PermissionManager(Activity activity, View view, String[] permissions, PermissionCallback callback) {
        this.activity = activity;
        this.view = view;
        this.permissions = permissions;
        this.callback = callback;
        checkAndRequestPermissions();
    }

    // İzinleri kontrol edip iste
    private void checkAndRequestPermissions() {
        boolean allGranted = true;

        // İzinleri kontrol et
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        // Tüm izinler verilmişse
        if (allGranted) {
            callback.onPermissionGranted();
        } else {
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE);
        }
    }

    // İzin sonucunu burada işliyoruz
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            boolean allGranted = true;

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                callback.onPermissionGranted();
            } else {
                // İzin reddedildiğinde Snackbar ile yeniden iste
                showPermissionSnackbar();
            }
        }
    }

    // İzin verilmezse Snackbar ile yeniden iste
    private void showPermissionSnackbar() {
        Snackbar.make(view, "Permission Needed!", Snackbar.LENGTH_INDEFINITE)
                .setAction("Give permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE);
                    }
                }).show();
        callback.onPermissionDenied();
    }

    // İzin callback interface'i
    public interface PermissionCallback {
        void onPermissionGranted();
        void onPermissionDenied();
    }
}
