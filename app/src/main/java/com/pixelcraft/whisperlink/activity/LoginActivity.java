package com.pixelcraft.whisperlink.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pixelcraft.whisperlink.R;
import com.pixelcraft.whisperlink.Utils.Firebase_LogIn;
import com.pixelcraft.whisperlink.Utils.NavigationHelper;
import com.pixelcraft.whisperlink.Utils.Progress_Dialog;
import com.pixelcraft.whisperlink.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    Progress_Dialog progressDialog;
    private ActivityLoginBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //leads to other activity <my_messages>
           NavigationHelper.navigateTo(this, MyChats.class,true);
        }
    }
    public void createAccount(View view){
        //leas SignIn activity to create account
        NavigationHelper.navigateTo(LoginActivity.this, SiginActivity.class,false);
    }
    public void loginClick(View view){
        //user authentication
        // Kullanıcı doğrulama
        String email = binding.emailLogIn.getText().toString().trim();
        String password = binding.passwordLogin.getText().toString().trim();

        // Email ve şifrenin boş olup olmadığını kontrol edin
        if (!email.isEmpty() && !password.isEmpty()) {
            binding.loginButton.setEnabled(false);
            Firebase_LogIn firebaseLogIn = new Firebase_LogIn(email, password, LoginActivity.this, binding);
            firebaseLogIn.LogIn();
        } else {
            Toast.makeText(this, R.string.BothPasswords, Toast.LENGTH_SHORT).show();
        }

    }

    //ForgetMyPassword
    public void forgetMyPasswordClick(View view){
        if (!binding.emailLogIn.getText().toString().matches("")){
            //currentUser.updatePassword("umut1.");
        }else
            Toast.makeText(LoginActivity.this,R.string.enteremail,Toast.LENGTH_SHORT).show();
    }
}