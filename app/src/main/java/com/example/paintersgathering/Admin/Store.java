package com.example.paintersgathering.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.paintersgathering.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class Store extends AppCompatActivity {


    EditText store_account, store_link, store_name;

    Button button_add_store;

    ImageView store_image;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;

    String UID;


    Uri imuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        store_account = findViewById(R.id.store_account);
        store_link = findViewById(R.id.store_link);
        store_name = findViewById(R.id.store_name);
        store_image = findViewById(R.id.store_image);
        button_add_store = findViewById(R.id.button_add_store);

        store_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openfilechooser();
            }
        });


        button_add_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String store_name_txt, store_link_txt, store_account_txt;

                store_name_txt = store_name.getText().toString().trim();
                store_link_txt = store_link.getText().toString().trim();
                store_account_txt = store_account.getText().toString().trim();

                boolean flag = true;

                if (store_name_txt.equals("")) {
                    store_name.setError("This field is required");
                    flag = false;
                }
                if (store_link_txt.equals("")) {
                    store_link.setError("This field is required");
                    flag = false;
                }
                if (store_account_txt.equals("")) {
                    store_account.setError("This field is required");
                    flag = false;
                }
                if (imuri == null) {
                    Toast.makeText(getApplicationContext(), "You should choose an image", Toast.LENGTH_LONG).show();
                    flag = false;
                }

                if (!flag) return;

                DocumentReference store_ref = fStore.collection("Stores").document();

                Map<String, Object> store = new HashMap<>();

                store.put("store_name", store_name_txt);
                store.put("store_link", store_link_txt);
                store.put("store_account", store_account_txt);

                uploadpic(store_ref.getId());

                store_ref.set(store).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Store added successfully", Toast.LENGTH_LONG).show();
                        finish(); }}); }
        });
    }


    private void openfilechooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imuri = data.getData();
            store_image.setImageURI(imuri);
        }
    }

    private void uploadpic(String id) {

        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading image . . . . ");
        pd.show();
        StorageReference ImagesRef = storageReference.child("Stores/" + id);

        ImagesRef.putFile(imuri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Image Uploaded.", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Faild to upload image......     " + e.toString(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

}