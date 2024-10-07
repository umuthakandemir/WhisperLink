package com.pixelcraft.whisperlink.activity;

import static com.pixelcraft.whisperlink.Utils.NavigationHelper.navigateTo;
import static com.pixelcraft.whisperlink.Utils.ShowToast.showToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import android.Manifest;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.common.eventbus.Subscribe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pixelcraft.whisperlink.Adapter.MyContactsAdapter;
import com.pixelcraft.whisperlink.Model.GetMyContactFirestore;
import com.pixelcraft.whisperlink.Model.MyContactsModel;
import com.pixelcraft.whisperlink.R;
import com.pixelcraft.whisperlink.Utils.Constans;
import com.pixelcraft.whisperlink.Utils.PermissionManager;
import com.pixelcraft.whisperlink.Utils.PreferencesManager;
import com.pixelcraft.whisperlink.Utils.Progress_Dialog;
import com.pixelcraft.whisperlink.databinding.ActivityContactsBinding;
import com.pixelcraft.whisperlink.Utils.GetMyContactsFromPhoneContacts;
import com.pixelcraft.whisperlink.Firebase.ContactFirestoreCompare;
import com.pixelcraft.whisperlink.roomdb.ContactDao;
import com.pixelcraft.whisperlink.roomdb.ContactDatabase;
import com.pixelcraft.whisperlink.roomdb.ContactEntity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ContactsActivity extends AppCompatActivity {
    private ActivityContactsBinding binding;
    MyContactsAdapter contactsAdapter;
    PermissionManager permissionManager;
    Progress_Dialog progressDialog;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    private PreferencesManager preferencesManager;
    GetMyContactsFromPhoneContacts getMyContactsFromPhoneContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.myToolbar);
        binding.ContactNavigateToMyChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(ContactsActivity.this,MyChats.class,true);
            }
        });
        preferencesManager = new PreferencesManager(ContactsActivity.this);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        String[] permissions = {Manifest.permission.READ_CONTACTS};
        progressDialog = new Progress_Dialog("Kişiler Yükleniyor..",null,this);
        permissionManager = new PermissionManager(this, binding.getRoot(), permissions, new PermissionManager.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                loadContact();
            }

            @Override
            public void onPermissionDenied() {
                Toast.makeText(ContactsActivity.this,R.string.permissionDenied,Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadContact(){
        progressDialog.show();
        getMyContactsFromPhoneContacts = new GetMyContactsFromPhoneContacts(binding.getRoot().getContext());
        getMyContactsFromPhoneContacts.getContact(new GetMyContactsFromPhoneContacts.ContactsCallback() {
            @Override
            public void onContactGet(ArrayList<MyContactsModel> contactsModels) {
                if (contactsModels.isEmpty()){
                    progressDialog.dismiss();
                    showToast(ContactsActivity.this, R.string.ContactsEmpty);
                }
                binding.contactsRecyclerView.setLayoutManager(new LinearLayoutManager(ContactsActivity.this));
                ContactFirestoreCompare.firestoreCompare(mAuth,contactsModels, db, new ContactFirestoreCompare.compareListCallback() {
                    @Override
                    public void onCallback(ArrayList<GetMyContactFirestore> contactFirestores) {
                        if (contactFirestores.isEmpty()){
                            progressDialog.dismiss();
                            binding.noContact.setVisibility(View.VISIBLE);
                            showToast(ContactsActivity.this, R.string.noOneUsesTheApp);
                        }
                        else {
                            binding.noContact.setVisibility(View.GONE);
                        // Veritabanına veriyi eklemeden önce kontrol ediyoruz
                        contactsAdapter = new MyContactsAdapter(contactFirestores);
                        binding.contactsRecyclerView.setAdapter(contactsAdapter);
                        contactsAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            loadContact();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contactmenu, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }
}
