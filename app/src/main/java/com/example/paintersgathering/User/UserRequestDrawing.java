package com.example.paintersgathering.User;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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

public class UserRequestDrawing extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText drawing_description;
    Spinner spinner;
    Button Request;
    ArrayList<String> arrayList = new ArrayList<String>();
    ArrayList<String> Painter_id_s = new ArrayList<String>();
    String[] items;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;

    TextView textview_choose;
    ImageView drawing_image;
    String painter_name, Painter_id, UID;
    Uri imageUri;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_request_drawing);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        UID = fAuth.getCurrentUser().getUid();
        drawing_image = findViewById(R.id.drawing_image);
        Request = findViewById(R.id.Request);
        spinner = findViewById(R.id.spinner);
        drawing_description = findViewById(R.id.drawing_description);

        fStore.collection("Users").document(UID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        name = documentSnapshot.getString("FullName");

                    }
                });


        fStore.collection("Users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) { return;
                        } else {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getString("Account_type").equals("painter")) {
                                    arrayList.add(list.get(i).getString("FullName"));
                                    Painter_id_s.add(list.get(i).getId()); }
                            }
                            items = new String[arrayList.size()];
                            for (int i = 0; i < arrayList.size(); i++) {
                                items[i] = arrayList.get(i);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(UserRequestDrawing.this, android.R.layout.simple_spinner_dropdown_item, items);
                            spinner.setAdapter(adapter);
                            spinner.setOnItemSelectedListener(UserRequestDrawing.this); }
                    }
                });


        textview_choose = findViewById(R.id.textview_choose);
        textview_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosepic();
            }
        });

        Request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String drawing_description_txt = drawing_description.getText().toString();
                DocumentReference request_ref = fStore.collection("Requesting").document();
                Map<String, Object> drqwing_data = new HashMap<>();
                drqwing_data.put("user_id", UID);
                drqwing_data.put("user_name", name);
                drqwing_data.put("Painter_id", Painter_id);
                drqwing_data.put("description", drawing_description_txt);
                drqwing_data.put("accept", false);
                uploadpic(request_ref.getId());
                request_ref.set(drqwing_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(UserRequestDrawing.this, "Drawing request send", Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    }
                });


            }
        });
    }


    private void choosepic() {

        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);

    }


    private void uploadpic(String id) {

        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading image . . . . ");
        pd.show();


        StorageReference ImagesRef = storageReference.child("Request/" + id);

        ImagesRef.putFile(imageUri)
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
                        Toast.makeText(getApplicationContext(), "Faild to upload image......     " + e, Toast.LENGTH_SHORT).show();

                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            drawing_image.setImageURI(imageUri);
            textview_choose.setText("change  another file");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        painter_name = items[position];
        Painter_id = Painter_id_s.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}