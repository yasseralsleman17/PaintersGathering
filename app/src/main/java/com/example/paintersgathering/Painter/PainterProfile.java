package com.example.paintersgathering.Painter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.gridlayout.widget.GridLayout;

import com.bumptech.glide.Glide;
import com.example.paintersgathering.Admin.ShowEvents;
import com.example.paintersgathering.Admin.ShowStores;
import com.example.paintersgathering.MainActivity;
import com.example.paintersgathering.R;
import com.example.paintersgathering.User.Courses;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PainterProfile extends AppCompatActivity {


    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;


    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;

    TextView rating;
    ImageView profile_image;
    Uri imageUri;
    ImageButton add_drawing;
    TextView user_name;

    String name, UID;
    GridLayout deawing_gride;
    Spinner spinner;
    String[] items = {"Cartoon drawing", "Pixel drawing", "Surreal drawing"};

    int rating_val=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painter_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        drawerLayout = findViewById(R.id.painter_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        rating=findViewById(R.id.rating);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(PainterProfile.this, android.R.layout.simple_spinner_item, items);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                DocumentReference painter_ref = fStore.collection("Users").document(UID);
                Map<String, Object> painter_data = new HashMap<>();
                painter_data.put("Painter type", items[i]);
                painter_ref.update(painter_data);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        navigationView = findViewById(R.id.painter_nav);

        navigationView.setNavigationItemSelectedListener((item) -> {

            switch (item.getItemId()) {
                case R.id.main_page:
                    startActivity(new Intent(getApplicationContext(), PainterHomePage.class));
                    break;
                case R.id.create_course:
                    startActivity(new Intent(getApplicationContext(), AddCourse.class));
                    break;
                case R.id.requesting:
                    startActivity(new Intent(getApplicationContext(), PainterRequestDrawing.class));
                    break;
                case R.id.event:
                    startActivity(new Intent(getApplicationContext(), ShowEvents.class));
                    break;
                case R.id.store:
                    startActivity(new Intent(getApplicationContext(), ShowStores.class));
                    break;
                case R.id.join_course:
                    startActivity(new Intent(getApplicationContext(), Courses.class));
                    break;
                case R.id.log_out:
                    fAuth.signOut();
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                    break;
            }
            return true;

        });

        user_name = findViewById(R.id.user_name);
        UID = fAuth.getCurrentUser().getUid();
        deawing_gride = findViewById(R.id.drawings);
        fStore.collection("Users").document(UID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        name = documentSnapshot.getString("FullName");

                        user_name.setText("  " + name + "  ");
                    }
                });


        profile_image = findViewById(R.id.profile_image);

        storageReference.child("images/" + UID).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .into(profile_image);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });


        fStore.collection("Drawing").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {

                            return;
                        } else {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();


                            for (int i = 0; i < list.size(); i++) {

                                if (list.get(i).getString("painter_id").equals(UID)) {
                                    double rate=list.get(i).getDouble("rating");

                                    rating_val+= (int) rate;

                                    viewDrawing(list.get(i));

                                }
                            }

                            rating.setText("rating "+rating_val);

                        }

                    }
                });

        profile_image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                choosepic();
                return false;
            }
        });

        add_drawing = findViewById(R.id.add_drawing);
        add_drawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), AddDrawing.class);
                i.putExtra("painter_id", UID);
                startActivity(i);

            }
        });
    }

    private void viewDrawing(DocumentSnapshot documentSnapshot) {

        String drawing_id = documentSnapshot.getId();

        View view = getLayoutInflater().inflate(R.layout.show_drawing_activity, null);


        TextView drawing_price = view.findViewById(R.id.drawing_price);
        ImageView drawing_im = view.findViewById(R.id.drawing_im);

        drawing_price.setText(documentSnapshot.getString("drawing_price") + " SR");
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
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showimage(im_uri[0], drawing_id);
            }
        });

        deawing_gride.addView(view);
    }

    private void showimage(Uri uri, String drawing_id) {


        AlertDialog.Builder alertadd = new AlertDialog.Builder(PainterProfile.this);
        final View view = getLayoutInflater().inflate(R.layout.imageview, null);
        ImageView dialog_imageview = view.findViewById(R.id.dialog_imageview);
        Glide.with(PainterProfile.this)
                .load(uri)
                .into(dialog_imageview);
        alertadd.setView(view);
        alertadd.setNeutralButton("delete", (dialogInterface, i) -> delete(drawing_id));
        alertadd.show();


    }

    private void delete(String drawing_id) {

        fStore.collection("Drawing").document(drawing_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                storageReference.child("drawing/" + drawing_id).delete();
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });


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

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profile_image.setImageURI(imageUri);

            uploadpic();
        }
    }


    private void uploadpic() {

        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading image . . . . ");
        pd.show();


        StorageReference ImagesRef = storageReference.child("images/" + UID);

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
                        Toast.makeText(getApplicationContext(), "Faild to upload image......     " + e.toString(), Toast.LENGTH_SHORT).show();

                    }
                });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}