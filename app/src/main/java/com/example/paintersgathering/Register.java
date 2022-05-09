package com.example.paintersgathering;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.paintersgathering.Painter.PainterHomePage;
import com.example.paintersgathering.User.UserHomePage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {


    Button btn_painter_register;
    Button btn_user_register;


    FirebaseAuth fAuth;
    FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        btn_user_register = findViewById(R.id.btn_user_register);
        btn_painter_register = findViewById(R.id.btn_painter_register);
        btn_painter_register.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        selecttyoe("painter");

                                                    }
                                                }
        );

        btn_user_register.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {

                                                     selecttyoe("user");


                                                 }
                                             }
        );
    }

    private void selecttyoe(String Account_type) {


        FirebaseUser user = fAuth.getCurrentUser();


        DocumentReference club_ref = fStore.collection("Users").document(user.getUid());

        Map<String, Object> account_data = new HashMap<>();

        account_data.put("Account_type", Account_type);

        club_ref.update(account_data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                          @Override
                                          public void onSuccess(Void aVoid) {
                                              if (Account_type.equals("painter"))
                                                  startActivity(new Intent(getApplicationContext(), PainterHomePage.class));
                                              else
                                                  startActivity(new Intent(getApplicationContext(), UserHomePage.class));
                                          }
                                      }
                );

    }


}