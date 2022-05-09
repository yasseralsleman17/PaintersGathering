package com.example.paintersgathering.Admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import com.bumptech.glide.Glide;
import com.example.paintersgathering.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ShowStores extends AppCompatActivity {

    GridLayout gridLayout;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;


    String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_stores);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        UID = fAuth.getCurrentUser().getUid();
        gridLayout = findViewById(R.id.gridLayout);

        get_all_stores();

    }


    void get_all_stores() {

        fStore.collection("Stores").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {

                            return;
                        } else {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (int i = 0; i < list.size(); i++) {

                                show_store(list.get(i));


                            }

                        }

                    }
                });


    }


    private void show_store(DocumentSnapshot documentSnapshot) {

        String id = documentSnapshot.getId();

        String link = documentSnapshot.getString("store_link");

        View view = getLayoutInflater().inflate(R.layout.store_card, null);
        ImageView store_image = view.findViewById(R.id.store_image);
        TextView store_name = view.findViewById(R.id.store_name);


        store_name.setText(documentSnapshot.getString("store_name"));


        storageReference.child("Stores/" + id).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .into(store_image);

                    }
                });


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(link);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });


        gridLayout.addView(view);
    }


}