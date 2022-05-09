package com.example.paintersgathering;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterPage extends AppCompatActivity {


    Button reg_bt;

    EditText reg_full_name, reg_email, reg_password, reg_confirm_pass;
    String reg_full_name_tx, reg_email_tx, reg_password_tx, reg_confirm_pass_tx, reg_address_txt = "";

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    Spinner country_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        reg_full_name = findViewById(R.id.reg_full_name);
        reg_email = findViewById(R.id.reg_email);
        reg_password = findViewById(R.id.reg_password);
        reg_confirm_pass = findViewById(R.id.reg_confirm_pass);


        country_spinner = findViewById(R.id.country_spinner);

        Locale[] local = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<>();
        String country;
        for (Locale loc : local) {
            country = loc.getDisplayCountry();
            if (country.length() > 0 && !countries.contains(country))
                countries.add(country);

        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);

        reg_address_txt = countries.get(0);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countries);
        country_spinner.setAdapter(adapter);


        country_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                reg_address_txt = countries.get(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        reg_bt = findViewById(R.id.reg_bt);
        reg_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reg_full_name_tx = reg_full_name.getText().toString();
                reg_email_tx = reg_email.getText().toString();
                reg_password_tx = reg_password.getText().toString();
                reg_confirm_pass_tx = reg_confirm_pass.getText().toString();
                boolean flag = true;

                if (reg_full_name_tx.isEmpty()) {
                    reg_full_name.setError("This field is required");
                    flag = false;
                }
                if (reg_email_tx.isEmpty()) {
                    reg_email.setError("This field is required");
                    flag = false;
                }
                if (reg_password_tx.isEmpty()) {
                    reg_password.setError("This field is required");
                    flag = false;
                }
                if (reg_confirm_pass_tx.isEmpty()) {
                    reg_confirm_pass.setError("This field is required");
                    flag = false;
                }

                if (reg_password_tx.length() < 8) {
                    reg_password.setError("At least 8 characters");
                    flag = false;
                }
                if (!isValidPassword(reg_password_tx)) {
                    reg_password.setError("password must contain [a-zA-Z0-9] and less than 24 ");
                    flag = false;
                }
                if (!(reg_password_tx.equals(reg_confirm_pass_tx))) {
                    reg_password.setError("Passwords must match");
                    reg_confirm_pass.setError("Passwords must match");
                    flag = false;
                }
                if (reg_address_txt.isEmpty()) {
                    Toast.makeText(RegisterPage.this, "you must choose country", Toast.LENGTH_SHORT).show();
                    flag = false;
                }

                if (!flag) return;
                fAuth.createUserWithEmailAndPassword(reg_email_tx, reg_password_tx)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                FirebaseUser user = fAuth.getCurrentUser();
                                DocumentReference user_ref = fStore.collection("Users").document(user.getUid());
                                Map<String, Object> userdata = new HashMap<>();
                                userdata.put("FullName", reg_full_name_tx);
                                userdata.put("Email", reg_email_tx);
                                userdata.put("Password", reg_password_tx);
                                userdata.put("Address", reg_address_txt);

                                user_ref.set(userdata)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(), "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getApplicationContext(), Register.class));
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterPage.this, "Failed to Create Account,try again later", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private boolean isValidPassword(String reg_password_tx) {
        Pattern PASSWORD_PATTERN
                = Pattern.compile(
                "[a-zA-Z0-9]{8,24}");

        return !TextUtils.isEmpty(reg_password_tx) && PASSWORD_PATTERN.matcher(reg_password_tx).matches();
    }
}