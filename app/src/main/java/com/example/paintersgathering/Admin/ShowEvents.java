package com.example.paintersgathering.Admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.paintersgathering.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowEvents extends AppCompatActivity {
    LinearLayout linear_you_join_event, linear_available_event;

    ScrollView parentScrollView, scrollview_available_event, scrollview_you_join_event;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;


    String UID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_events);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        UID = fAuth.getCurrentUser().getUid();


        linear_you_join_event = findViewById(R.id.linear_you_join_event);

        linear_available_event = findViewById(R.id.linear_available_event);

        parentScrollView = findViewById(R.id.parentScrollView);
        scrollview_available_event = findViewById(R.id.scrollview_available_event);
        scrollview_you_join_event = findViewById(R.id.scrollview_you_join_event);

        parentScrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                scrollview_you_join_event.requestDisallowInterceptTouchEvent(false);
                scrollview_available_event.requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        scrollview_available_event.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // Disallow the touch request for parent scroll on touch of  child view
                parentScrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        scrollview_you_join_event.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // Disallow the touch request for parent scroll on touch of  child view
                parentScrollView.requestDisallowInterceptTouchEvent(true);
                return false;
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
                                if (list.get(i).contains(UID))
                                    viewJoinEvents(list.get(i));
                                else
                                    viewAvailableEvents(list.get(i));


                            }


                        }

                    }
                });


    }


    private void viewJoinEvents(DocumentSnapshot documentSnapshot) {


        View view = getLayoutInflater().inflate(R.layout.event_you_join_card, null);

        String link = documentSnapshot.getString("event_link");

        TextView event_name = view.findViewById(R.id.event_name);


        event_name.setText(documentSnapshot.getString("event_name"));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(link);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


        linear_you_join_event.addView(view);
    }

    private void viewAvailableEvents(DocumentSnapshot documentSnapshot) {

        String id = documentSnapshot.getId();
        View view = getLayoutInflater().inflate(R.layout.event_available_card, null);
        TextView event_name = view.findViewById(R.id.event_name);
        Button join_event = view.findViewById(R.id.join_event);
        event_name.setText(documentSnapshot.getString("event_name"));

        join_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "you joined " + documentSnapshot.getString("event_name") + " Event", Toast.LENGTH_SHORT).show();

                DocumentReference event_ref = fStore.collection("Events").document(id);

                Map<String, Object> event = new HashMap<>();

                event.put(UID, "ok");

                event_ref.update(event).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    }
                });
            }
        });


        linear_available_event.addView(view);
    }


}