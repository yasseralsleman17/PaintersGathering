package com.example.paintersgathering.Painter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class AddDrawing extends AppCompatActivity {


    String name, UID;


    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;

    EditText edit_price;
    Button choose_im, add_drawing;
    ImageView show_im;
    Uri imuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drawing);


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        UID = fAuth.getCurrentUser().getUid();


        choose_im = findViewById(R.id.choose_im);
        add_drawing = findViewById(R.id.add_drawing);
        edit_price = findViewById(R.id.edit_price);
        show_im = findViewById(R.id.show_im);


        choose_im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openfilechooser();
            }
        });
        show_im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openfilechooser();

            }
        });

        add_drawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String price = edit_price.getText().toString();
                boolean flag = true;
                if (price.equals("")) {
                    edit_price.setError("*you should add price");flag = false;
                }
                if (imuri == null) {
                    Toast.makeText(getApplicationContext(), "You should choose an image", Toast.LENGTH_LONG).show();flag = false;
                }
                if (!flag) { return;
                }
                DocumentReference drawing_ref = fStore.collection("Drawing").document();
                Map<String, Object> drqwing_data = new HashMap<>();
                drqwing_data.put("painter_id", UID);
                drqwing_data.put("drawing_price", price);
                drqwing_data.put("rating", 0);
                uploadpic(drawing_ref.getId());
                drawing_ref.set(drqwing_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        startActivity(new Intent(getApplicationContext(), PainterProfile.class));

                    }
                });
            }
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
            imuri = data.getData(); show_im.setImageURI(imuri);
        }
    }

    private void uploadpic(String id) {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading image . . . . ");
        pd.show();

        StorageReference ImagesRef = storageReference.child("drawing/" + id);

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