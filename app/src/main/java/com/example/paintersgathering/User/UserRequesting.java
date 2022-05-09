package com.example.paintersgathering.User;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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

public class UserRequesting extends AppCompatActivity {


    LinearLayout linear_request;
    TextView request_count;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;


    String UID;
    int request_num = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_requesting);


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        UID = fAuth.getCurrentUser().getUid();

        linear_request = findViewById(R.id.linear_request);

        request_count = findViewById(R.id.request_count);

        fStore.collection("Requesting").whereEqualTo("user_id", UID).whereEqualTo("accept", true).whereEqualTo("paid","No").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            request_count.setText("You have 0 Requests");

                            return;
                        } else {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            request_num = list.size();
                            request_count.setText("You have " + request_num + " Requests");
                            for (int i = 0; i < list.size(); i++) {

                                viewRequests(list.get(i), i + 1);



                            }

                        }

                    }
                });


    }

    private void viewRequests(DocumentSnapshot documentSnapshot, int i) {

        String id = documentSnapshot.getId();
        View view = getLayoutInflater().inflate(R.layout.user_request_card, null);
        TextView index = view.findViewById(R.id.index);
        TextView description = view.findViewById(R.id.description);
        TextView price = view.findViewById(R.id.price);
        ImageView request_image = view.findViewById(R.id.request_image);
        price.setText(documentSnapshot.getString("price"));
        index.setText(i + "-)");
        description.setText(documentSnapshot.getString("description"));
        Button pay=view.findViewById(R.id.pay);

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(UserRequesting.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.payment);
                EditText card_number = dialog.findViewById(R.id.card_number);
                card_number.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
                    }
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                        String initial = s.toString();
                        String processed = initial.replaceAll("\\D", "");
                        processed = processed.replaceAll("(\\d{4})(?=\\d)", "$1 ");
                        if (!initial.equals(processed)) {  s.replace(0, initial.length(), processed); }  } });
                EditText expiry = dialog.findViewById(R.id.expiry);
                EditText csv = dialog.findViewById(R.id.csv);
                Button pay = dialog.findViewById(R.id.pay);
                pay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String card_number_txt = card_number.getText().toString();
                        String expiry_txt = expiry.getText().toString();
                        String csv_txt = csv.getText().toString();
                        boolean flag = true;
                        if (card_number_txt.equals("")) {  flag = false; card_number.setError("*");
                        }
                        if (expiry_txt.equals("")) {  flag = false; expiry.setError("*");
                        }
                        if (csv_txt.equals("")) {  flag = false; csv.setError("*");
                        } if (!flag) return;
                        DocumentReference course = fStore.collection("Requesting").document(id);
                        Map<String, Object> course_data = new HashMap<>();
                        course_data.put("paid", "yes");
                        course.update(course_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                finish();  startActivity(getIntent()); }
                        });
                    }
                });


                dialog.show();


            }
        });

        storageReference.child("Request/" + id).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .into(request_image);

                    }
                });

        linear_request.addView(view);
    }
}
