package com.pixelcraft.whisperlink.activity;

import static com.pixelcraft.whisperlink.Firebase.ContactFirestoreCompare.firestoreCompare;
import static com.pixelcraft.whisperlink.Firebase.Load_Chats.loadChats;
import static com.pixelcraft.whisperlink.Utils.NavigationHelper.navigateTo;
import static com.pixelcraft.whisperlink.Utils.ShowToast.showToast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import android.Manifest;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pixelcraft.whisperlink.Adapter.ChatsAdapter;
import com.pixelcraft.whisperlink.Firebase.Load_Chats;
import com.pixelcraft.whisperlink.Model.GetChat;
import com.pixelcraft.whisperlink.Utils.Constans;
import com.pixelcraft.whisperlink.Utils.Firebase_LogOut;
import com.pixelcraft.whisperlink.R;
import com.pixelcraft.whisperlink.Utils.NavigationHelper;

import com.pixelcraft.whisperlink.Utils.NetworkReceiver;
import com.pixelcraft.whisperlink.Utils.NetworkUtil;
import com.pixelcraft.whisperlink.Utils.PermissionManager;
import com.pixelcraft.whisperlink.Utils.PreferencesManager;
import com.pixelcraft.whisperlink.databinding.ActivityMyChatsBinding;
import com.pixelcraft.whisperlink.roomdb.ContactDao;
import com.pixelcraft.whisperlink.roomdb.ContactDatabase;
import com.pixelcraft.whisperlink.roomdb.ContactEntity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MyChats extends AppCompatActivity {

    private ActivityMyChatsBinding binding;
    View view;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    PermissionManager permissionManager;
    PreferencesManager preferencesManager;
    ChatsAdapter chatsAdapter;
    FirebaseAuth mAuth;
    Firebase_LogOut logOut;
    FirebaseFirestore db;
    DocumentReference userRef;
    ContactDao contactDao;
    ContactDatabase contactDatabase;
    List<ContactEntity> contactEntities = new ArrayList<>();
    NetworkReceiver networkReceiver = new NetworkReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyChatsBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");
        preferencesManager = new PreferencesManager(MyChats.this);
        binding.MyChatUserName.setText(preferencesManager.getString(Constans.KEY_USER_NAME_SURNAME));
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        preferencesManager.putString(Constans.KEY_USER_ID,mAuth.getUid());
        userRef = db.document(Constans.KEY_COLLECTION_USER+"/"+mAuth.getUid());
        // Fetch profile photo
        getProfilePhoto();
        checkPermissions();
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.EXTRA_NO_CONNECTIVITY);
        registerReceiver(networkReceiver,filter);
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(networkReceiver);
        super.onDestroy();
    }

    private void loadMyChats() {
        loadChats(MyChats.this, db, new Load_Chats.chatCallback() {
            @Override
            public void onCallback(ArrayList<GetChat> chatArrayList) {
                if (chatArrayList.isEmpty())
                    binding.noMessage.setVisibility(View.VISIBLE);
                else {
                    binding.noMessage.setVisibility(View.GONE);
                    binding.myMessagesRecyclerview.setLayoutManager(new LinearLayoutManager(MyChats.this));
                    chatsAdapter = new ChatsAdapter(chatArrayList,contactEntities);
                    binding.myMessagesRecyclerview.setAdapter(chatsAdapter);
                    chatsAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void checkPermissions(){
        String[] permissions={
                Manifest.permission.READ_CONTACTS
        };
        PermissionManager permissionManager = new PermissionManager(MyChats.this, view, permissions, new PermissionManager.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                loadMyChats();
            }

            @Override
            public void onPermissionDenied() {
                showToast(MyChats.this, R.string.needPermission);
            }
        });
    }
    // Get the profile photo URI using Picasso
    private void getProfilePhoto() {
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String photoUri = document.getString(Constans.KEY_USER_IMAGE);
                        if (photoUri != null && !photoUri.isEmpty()) {
                            // If the photo URI exists
                            Log.d("Firestore", "Photo URI: " + photoUri);
                            // Here you can use the URI (e.g., load it into an ImageView)
                            preferencesManager.putString(Constans.KEY_USER_IMAGE,photoUri);
                            Picasso.get().load(photoUri).into(binding.ProfileImage);
                        } else {
                            // If there is no photo URI, it's empty
                            Log.d("Firestore", "No profile photo found.");
                            // Handle the empty value here
                            preferencesManager.putString(Constans.KEY_USER_IMAGE,"no photo");
                            binding.ProfileImage.setImageResource(R.drawable.user);
                        }
                    } else {
                        Log.d("Firestore", "No such user.");
                        navigateTo(MyChats.this,LoginActivity.class,true);
                    }
                } else {
                    // If task failed, return to the login screen
                  navigateTo(MyChats.this, LoginActivity.class,true);
                }
            }
        });
    }

    //create new message button click event
    public void createNewMessageClick(View view){
        navigateTo(MyChats.this, ContactsActivity.class, false);
    }

    // Create the menu object
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainpagemenu, menu);
        return true;
    }

    // Handle menu item selection
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logOut) {
            logOut = new Firebase_LogOut(MyChats.this);
            logOut.logOut();

            // Log the user out and navigate to the login screen
            navigateTo(MyChats.this, LoginActivity.class,true);
            return true;
        } else if (item.getItemId() == R.id.settings) {
            navigateTo(MyChats.this, SettingsActivity.class,false);
        }
        return super.onOptionsItemSelected(item);
    }

    // İzin sonucunu izin yöneticisine ilet
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private long backPressedTime = 0;
    private void backPressed() {
        getOnBackPressedDispatcher().addCallback(MyChats.this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Geri tuşuna basıldığı zamanı tutacak
                // Geri tuşu için özelleştirilmiş işlem
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    finish(); // İkinci kez basıldığında aktiviteyi kapat
                } else {
                    // İlk basıldığında Toast göster
                    Toast.makeText(MyChats.this, R.string.doubleClickSignout, Toast.LENGTH_SHORT).show();
                }
                // Son basılma zamanını güncelle
                backPressedTime = System.currentTimeMillis();
            }
        });
    }

}

