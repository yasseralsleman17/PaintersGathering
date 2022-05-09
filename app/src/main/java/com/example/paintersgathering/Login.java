package com.example.paintersgathering;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.paintersgathering.Admin.AdminHomePage;
import com.example.paintersgathering.Painter.PainterHomePage;
import com.example.paintersgathering.User.UserHomePage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {


    EditText log_email, log_password;
    Button log_bt;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String email_tx, passwords_tx;

    TextView forget_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        log_email = findViewById(R.id.log_email);
        log_password = findViewById(R.id.log_password);

        log_bt = findViewById(R.id.log_bt);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        forget_password=findViewById(R.id.forget_password);
        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),ForgetPassword.class));
            }
        });

        log_bt.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                email_tx = log_email.getText().toString();
                passwords_tx = log_password.getText().toString();


                if (email_tx.isEmpty()) {
                    log_email.setError("Email is Required");
                    return;
                }
                if (passwords_tx.isEmpty()) {
                    log_password.setError("Password is Required");
                    return;
                }
                if (email_tx.equals("admin@gmail.com") && passwords_tx.equals("admin123")) {
                    startActivity(new Intent(getApplicationContext(), AdminHomePage.class));
                    return;
                }
                fAuth.signInWithEmailAndPassword(email_tx, passwords_tx)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(getApplicationContext(), "Logged in successfully", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = authResult.getUser();
                                DocumentReference user_ref = fStore.collection("Users").document(user.getUid());
                                user_ref.get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.getString("Account_type").equals("user")) {
                                                    startActivity(new Intent(getApplicationContext(), UserHomePage.class));
                                                }
                                                if (documentSnapshot.getString("Account_type").equals("painter")) {
                                                    startActivity(new Intent(getApplicationContext(), PainterHomePage.class));
                                                }
                                                if (documentSnapshot.getString("Account_type").equals("admin")) {
                                                    startActivity(new Intent(getApplicationContext(), AdminHomePage.class));
                                                }
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Check your Email and password and try again", Toast.LENGTH_SHORT).show();

                            }
                        });


            }
        });


    }
}