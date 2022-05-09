package com.example.paintersgathering.Painter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.paintersgathering.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PainterRequestDrawing extends AppCompatActivity {


    LinearLayout linear_request;
    TextView request_count;

    LinearLayout linear_request2;
    TextView request_count2;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;


    String UID;
    int request_num=0;
    int request_num2=0;


    ScrollView parentScrollView, scrollview_1, scrollview_2;


    Uri imageUri;

String idrequest;
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painter_request_drawing);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        UID = fAuth.getCurrentUser().getUid();

        linear_request = findViewById(R.id.linear_request);
        linear_request2 = findViewById(R.id.linear_request2);

        request_count = findViewById(R.id.request_count);
        request_count2 = findViewById(R.id.request_count2);

        fStore.collection("Requesting").whereEqualTo("Painter_id", UID).whereEqualTo("accept",false).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            request_count.setText("You have 0 Requests");

                            return;
                        } else {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            request_num=list.size();
                            request_count.setText("You have " +request_num + " Requests");
                            for (int i = 0; i < list.size(); i++) {

                                viewRequests(list.get(i), i + 1);


                            }

                        }

                    }
                });







        fStore.collection("Requesting").whereEqualTo("Painter_id", UID).whereEqualTo("paid","yes").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            request_count2.setText("You have 0 picture to send");

                            return;
                        } else {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            request_num2=list.size();
                            request_count2.setText("You have " +request_num2 + " picture to send");
                            for (int i = 0; i < list.size(); i++) {
                                viewPictureToSend(list.get(i), i + 1);


                            }

                        }

                    }
                });




        parentScrollView = findViewById(R.id.scrollview_parent);
        scrollview_1 = findViewById(R.id.scrollview);
        scrollview_2 = findViewById(R.id.scrollview2);

        parentScrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                scrollview_2.requestDisallowInterceptTouchEvent(false);
                scrollview_1.requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        scrollview_1.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // Disallow the touch request for parent scroll on touch of  child view
                parentScrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        scrollview_2.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // Disallow the touch request for parent scroll on touch of  child view
                parentScrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });



    }

    private void viewRequests(DocumentSnapshot documentSnapshot, int i) {

        String id = documentSnapshot.getId();
        View view = getLayoutInflater().inflate(R.layout.painter_request_card, null);

        TextView index = view.findViewById(R.id.index);
        TextView user_name = view.findViewById(R.id.user_name);
        TextView description = view.findViewById(R.id.description);
        ImageView request_image = view.findViewById(R.id.request_image);
        Button accept = view.findViewById(R.id.accept);
        Button cancel = view.findViewById(R.id.cancel);

        index.setText(i + "-)");
        user_name.setText(documentSnapshot.getString("user_name"));
        description.setText(documentSnapshot.getString("description"));

        storageReference.child("Request/" + id).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .into(request_image);

                    }
                });


        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DocumentReference request_ref = fStore.collection("Requesting").document(id);

                final AlertDialog.Builder popDialog = new AlertDialog.Builder(PainterRequestDrawing.this);

                LinearLayout linearLayout = new LinearLayout(PainterRequestDrawing.this);
                EditText price = new EditText(PainterRequestDrawing.this);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                lp.setMargins(10, 10, 10, 10);
                price.setLayoutParams(lp);
                price.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

                linearLayout.addView(price);
                popDialog.setTitle("Add price ");
                popDialog.setView(linearLayout);


                popDialog.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String price_txt = price.getText().toString();
                                if (!price_txt.equals("")) {
                                    Map<String, Object> data = new HashMap<>();

                                    data.put("price", price_txt);
                                    data.put("paid", "No");
                                    data.put("accept", true);


                                    request_ref.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            linear_request.removeView(view);
                                            request_num--;
                                            request_count.setText("You have " +request_num + " Requests");

                                            dialog.dismiss();


                                        }
                                    });
                                }

                            }
                        }).setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                popDialog.create();
                popDialog.show();


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fStore.collection("Requesting").document(id).delete();
            }
        });


        linear_request.addView(view);
    }

    private void viewPictureToSend(DocumentSnapshot documentSnapshot, int i) {

        String id = documentSnapshot.getId();
        View view = getLayoutInflater().inflate(R.layout.painter_pictuer_tosend, null);

        TextView index = view.findViewById(R.id.index);
        TextView user_name = view.findViewById(R.id.user_name);
        TextView description = view.findViewById(R.id.description);
        ImageView request_image = view.findViewById(R.id.request_image);
        Button send = view.findViewById(R.id.send);

        index.setText(i + "-)");
        user_name.setText(documentSnapshot.getString("user_name"));
        description.setText(documentSnapshot.getString("description"));

        storageReference.child("Request/" + id).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext()).load(uri).into(request_image); }
                });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idrequest=id;          choosepic();
            }
        });



        linear_request2.addView(view);
    }


    private void choosepic() {

        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();


            ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Uploading image . . . . ");
            pd.show();


            Log.d("test test  ",idrequest);
            StorageReference ImagesRef = storageReference.child("Response/" + idrequest);
            ImagesRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            DocumentReference request_ref = fStore.collection("Requesting").document(idrequest);

                            Map<String, Object> drqwing_data = new HashMap<>();
                            drqwing_data.put("paid", "done");
                            request_ref.update(drqwing_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Snackbar.make(findViewById(android.R.id.content), "Image Uploaded.", Snackbar.LENGTH_LONG).show();
                                finish();
                                startActivity(getIntent());

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), "Faild to upload image......     " + e, Toast.LENGTH_SHORT).show();

                        }
                    });


        }
    }


}