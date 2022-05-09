package com.example.paintersgathering.Painter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import com.bumptech.glide.Glide;
import com.example.paintersgathering.Admin.ShowEvents;
import com.example.paintersgathering.Admin.ShowStores;
import com.example.paintersgathering.R;
import com.example.paintersgathering.User.Courses;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PainterHomePage extends AppCompatActivity {


    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;

    TextView user_name, advertisement;
    String name, account_type;

    GridLayout Highest_rating, Advertisement;
    String Uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_painter_home_page);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Uid = fAuth.getCurrentUser().getUid();
        user_name = findViewById(R.id.user_name);

        fStore.collection("Users").document(Uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        name = documentSnapshot.getString("FullName");
                        account_type = documentSnapshot.getString("Account_type");

                        user_name.setText(name);
                    }
                });

        advertisement = findViewById(R.id.advertisement);
        Highest_rating = findViewById(R.id.Highest_rating);
        Advertisement = findViewById(R.id.Advertisement);



        user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (account_type.equals("painter")) {

                    startActivity(new Intent(getApplicationContext(), PainterProfile.class));

                }

            }
        });


        advertisement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                startActivity(new Intent(getApplicationContext(), ShowEvents.class));


            }
        });

        CollectionReference idsRef = fStore.collection("Drawing");
        Query query = idsRef.orderBy("rating", Query.Direction.DESCENDING);

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {

                            return;
                        } else {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (int i = 0; i < list.size(); i++) {

                                viewDrawing(list.get(i));

                            }
                        }

                    }
                });


        fStore.collection("Events").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {

                            return;
                        } else {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (int i = 0; i < list.size(); i++) {

                                viewEvents(list.get(i));


                            }


                        }

                    }
                });



    }

    private void viewDrawing(DocumentSnapshot documentSnapshot) {

        String drawing_id = documentSnapshot.getId();


        final boolean[] liked = {false};
        final long[] rating = {(long) documentSnapshot.get("rating")};
        if (documentSnapshot.contains(Uid)) {

            liked[0] = documentSnapshot.getBoolean(Uid);
        }

        View view = getLayoutInflater().inflate(R.layout.show_rated_drawing, null);


        TextView drawing_raiting = view.findViewById(R.id.drawing_rating);
        ImageView drawing_im = view.findViewById(R.id.drawing_im);

        drawing_raiting.setText(String.valueOf(rating[0]) + " LIKE");

        final Uri[] im_uri = new Uri[1];
        storageReference.child("drawing/" + drawing_id).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .into(drawing_im);
                        im_uri[0] = uri;

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });

        final boolean[] isDoubleClicked = {false};

        Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                //Actions when Single Clicked
                isDoubleClicked[0] = false;
            }
        };

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDoubleClicked[0]) {
                    //Actions when double Clicked
                    isDoubleClicked[0] = false;
                    //remove callbacks for Handlers
                    handler.removeCallbacks(r);
                    DocumentReference drawing_ref = fStore.collection("Drawing").document(drawing_id);
                    Map<String, Object> drqwing_data = new HashMap<>();
                    if (documentSnapshot.contains(Uid)) {


                        if (liked[0]) {
                            rating[0]--;
                            liked[0] = false;
                            drqwing_data.put(Uid, liked[0]);
                        } else {
                            rating[0]++;
                            liked[0] = true;
                            drqwing_data.put(Uid, liked[0]);

                        }
                        drqwing_data.put("rating", rating[0]);
                        drawing_ref.update(drqwing_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                drawing_raiting.setText(String.valueOf(rating[0]) + " LIKE");

                            }
                        });
                    } else {
                        rating[0]++;

                        drqwing_data.put("rating", rating[0]);

                        drqwing_data.put(Uid, true);

                        drawing_ref.update(drqwing_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                drawing_raiting.setText(String.valueOf(rating[0] + 1) + " LIKE");

                            }
                        });
                    }


                } else {
                    isDoubleClicked[0] = true;
                    handler.postDelayed(r, 300);
                }
            }
        });


        Highest_rating.addView(view);
    }

    private void viewEvents(DocumentSnapshot documentSnapshot) {

        String id = documentSnapshot.getId();

        String link = documentSnapshot.getString("event_link");

        View view = getLayoutInflater().inflate(R.layout.store_card, null);
        ImageView store_image = view.findViewById(R.id.store_image);
        TextView store_name = view.findViewById(R.id.store_name);


        store_name.setText(documentSnapshot.getString("event_name"));


        storageReference.child("Events/" + id).getDownloadUrl()
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


        Advertisement.addView(view);
    }

}