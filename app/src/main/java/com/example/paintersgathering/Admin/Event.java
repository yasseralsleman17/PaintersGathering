package com.example.paintersgathering.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

public class Event extends AppCompatActivity {

    EditText event_name,event_link,event_information;
    ImageButton add_event_time;
    TextView event_time;
    Button button_add_event;
    String time="";

    ImageView event_image;


    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;

    Uri imuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        event_name=findViewById(R.id.event_name);
        event_link=findViewById(R.id.event_link);
        event_information=findViewById(R.id.event_information);
        add_event_time=findViewById(R.id.add_event_time);
        event_image = findViewById(R.id.event_image);

        event_time=findViewById(R.id.event_time);
        button_add_event=findViewById(R.id.button_add_event);


        add_event_time.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
            }
        });

        event_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openfilechooser();
            }
        });


        button_add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String event_name_txt,event_link_txt,event_information_txt;
                event_name_txt = event_name.getText().toString().trim();
                event_link_txt = event_link.getText().toString().trim();
                event_information_txt = event_information.getText().toString().trim();
                boolean flag = true;
                if (event_name_txt.equals("")) {
                    event_name.setError("This field is required");
                    flag = false;
                }
               if (event_link_txt.equals("")) {
                   event_link.setError("This field is required");
                    flag = false;
                }
               if (event_information_txt.equals("")) {
                   event_information.setError("This field is required");
                    flag = false;
                }
                if (time.equals("")) {
                    Toast.makeText(getApplicationContext(), "choose event time", Toast.LENGTH_LONG).show();
                    flag = false;
                }
                if (imuri == null) {
                    Toast.makeText(getApplicationContext(), "You should choose an image", Toast.LENGTH_LONG).show();
                    flag = false;
                }

                if (!flag) return;
                DocumentReference event_ref = fStore.collection("Events").document();
                Map<String, Object> event = new HashMap<>();
                event.put("event_name", event_name_txt);
                event.put("event_link", event_link_txt);
                event.put("event_information", event_information_txt);
                event.put("event_time", time);
                uploadpic(event_ref.getId());
                event_ref.set(event).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Event added successfully", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

            }
        });



    }


    public void showTimePickerDialog() {
        TimePickerDialog TimePickerDialog = new TimePickerDialog(
                this,
                (timePicker, i, i1) -> onTimeSet(timePicker, i, i1)
                ,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                DateFormat.is24HourFormat(getApplicationContext()));

        TimePickerDialog.show();
    }


    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


        time = hourOfDay + ":" + minute;
        event_time.setText(time);


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
            event_image.setImageURI(imuri);
        }
    }

    private void uploadpic(String id) {

        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading image . . . . ");
        pd.show();


        StorageReference ImagesRef = storageReference.child("Events/" + id);

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
                        Toast.makeText(getApplicationContext(), "Failed to upload image......     " + e.toString(), Toast.LENGTH_SHORT).show();

                    }
                });
    }



}