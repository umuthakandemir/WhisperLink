package com.pixelcraft.whisperlink.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.pixelcraft.whisperlink.R;
import com.pixelcraft.whisperlink.Utils.Firebase_SignIn;

import com.pixelcraft.whisperlink.Utils.PermissionManager;
import com.pixelcraft.whisperlink.databinding.ActivitySigninBinding;

public class SiginActivity extends AppCompatActivity {
    private ActivitySigninBinding binding;
    //for permission process;
    private PermissionManager permissionManager;
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<Intent> IntentGallery;
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        setContentView(view);
        resultLauncher();
        mAuth = FirebaseAuth.getInstance();
    }

    private void resultLauncher() {
        IntentGallery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if (o.getResultCode() == Activity.RESULT_OK){
                    Intent intentFromResult = o.getData();
                    if (intentFromResult!=null){
                        userPhoto = intentFromResult.getData();
                        binding.userPhoto.setImageURI(userPhoto);
                    }else
                        Toast.makeText(SiginActivity.this, R.string.Fail,Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(SiginActivity.this,R.string.Fail,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void SignInClick(View view){
        email = binding.emailSGnIn.getText().toString().trim();
        passsword = binding.passwordSGnIn.getText().toString().trim();
        nameSurname = binding.nameSurname.getText().toString();
        Phone = binding.phoneNumber.getText().toString();
        if (!email.isEmpty() && !passsword.isEmpty() && !nameSurname.isEmpty() && !Phone.isEmpty() ) {
            if (passsword.matches(binding.password2.getText().toString())) {
                binding.SignInButton.setEnabled(false);
                auth();
            }else
                Toast.makeText(SiginActivity.this, R.string.BothPasswords,Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(SiginActivity.this, R.string.requestInformation, Toast.LENGTH_SHORT).show();
        }
    }
    String email,passsword, nameSurname,Phone;
    // this function create users and save in firestore . <Users>
    private void auth(){
        Firebase_SignIn firebase_signIn = new Firebase_SignIn(SiginActivity.this, binding, email, passsword, nameSurname, Phone, userPhoto);
        firebase_signIn.SignIn();
    }
    //Check permission
    private void checkPermission(){
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions= new String[]{Manifest.permission.READ_MEDIA_IMAGES};
        }else {
            permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        }
        permissionManager = new PermissionManager(this,binding.getRoot(), permissions, new PermissionManager.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                // İzinler verildi
                Intent IntentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                IntentGallery.launch(IntentToGallery);
                binding.userPhoto.setImageURI(userPhoto);
            }

            @Override
            public void onPermissionDenied() {
                // İzinler reddedildi
                Toast.makeText(SiginActivity.this,R.string.needPermission,Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void selectProfilePhoto(View view){
        checkPermission();
    }
    // Give permission result to PermissionManager
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    //UserProfıle photo uri
    Uri userPhoto;
}