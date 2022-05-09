package com.example.paintersgathering;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.paintersgathering.Admin.AdminHomePage;
import com.example.paintersgathering.Painter.PainterHomePage;
import com.example.paintersgathering.User.UserHomePage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {




    FirebaseAuth fAuth;
    FirebaseFirestore fStore;


    Button et_reg;
    Button et_log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        et_log = findViewById(R.id.btn_sing_in);
        et_reg = findViewById(R.id.btn_register);
        FirebaseUser user = fAuth.getCurrentUser();
        if (user == null) {
            et_reg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), RegisterPage.class));
                }
            });

            et_log.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), Login.class));
                }
            });
        } else {

            DocumentReference user_ref = fStore.collection("Users").document(user.getUid());

            user_ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.getString("Account_type").equals("user")) {
                        finish();
                        startActivity(new Intent(getApplicationContext(), UserHomePage.class));
                    }
                    else  if (documentSnapshot.getString("Account_type").equals("painter")) {
                        finish();
                        startActivity(new Intent(getApplicationContext(),  PainterHomePage.class));
                    }
                    else  if (documentSnapshot.getString("Account_type").equals("admin")) {
                        finish();
                        startActivity(new Intent(getApplicationContext(), AdminHomePage.class));
                    }
                }
            });
        }

    }



}
