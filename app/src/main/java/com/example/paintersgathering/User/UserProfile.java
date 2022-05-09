package com.example.paintersgathering.User;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;

import com.example.paintersgathering.Admin.ShowEvents;
import com.example.paintersgathering.Admin.ShowStores;
import com.example.paintersgathering.FirstPage;
import com.example.paintersgathering.MainActivity;
import com.example.paintersgathering.Painter.AddCourse;
import com.example.paintersgathering.Painter.PainterHomePage;
import com.example.paintersgathering.Painter.PainterRequestDrawing;
import com.example.paintersgathering.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class UserProfile extends AppCompatActivity {



    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;


    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        drawerLayout = findViewById(R.id.user_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);


        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        navigationView = findViewById(R.id.user_nav);

        navigationView.setNavigationItemSelectedListener((item) -> {

            switch (item.getItemId()) {
                case R.id.main_page:
                    startActivity(new Intent(getApplicationContext(), UserHomePage.class));
                    break;

                case R.id.requesting:
                    startActivity(new Intent(getApplicationContext(), UserRequesting.class));
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



    }
}