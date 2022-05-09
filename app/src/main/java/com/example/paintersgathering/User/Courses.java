package com.example.paintersgathering.User;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.paintersgathering.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Courses extends AppCompatActivity {

    LinearLayout linear_request_available, linear_request_Ended, linear_request_Joined;

    ScrollView parentScrollView, scrollview_available, scrollview_ended, scrollview_joined;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;


    String UID;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        UID = fAuth.getCurrentUser().getUid();


        linear_request_available = findViewById(R.id.linear_request_available);
        linear_request_Ended = findViewById(R.id.linear_request_Ended);
        linear_request_Joined = findViewById(R.id.linear_request_Joined);

        parentScrollView = findViewById(R.id.parentScrollView);
        scrollview_available = findViewById(R.id.scrollview_available);
        scrollview_ended = findViewById(R.id.scrollview_ended);
        scrollview_joined = findViewById(R.id.scrollview_joined);

        parentScrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                scrollview_ended.requestDisallowInterceptTouchEvent(false);
                scrollview_available.requestDisallowInterceptTouchEvent(false);
                scrollview_joined.requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        scrollview_available.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // Disallow the touch request for parent scroll on touch of  child view
                parentScrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        scrollview_ended.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // Disallow the touch request for parent scroll on touch of  child view
                parentScrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        scrollview_joined.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // Disallow the touch request for parent scroll on touch of  child view
                parentScrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });


        fStore.collection("Courses").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {

                            return;
                        } else {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (int i = 0; i < list.size(); i++) {

                                try {
                                    viewCourses(list.get(i));   } catch (ParseException e) { e.printStackTrace(); } }
                        } }});
    }

    private void viewCourses(DocumentSnapshot documentSnapshot) throws ParseException {
        String id = documentSnapshot.getId();

        View view = getLayoutInflater().inflate(R.layout.courses_card_info, null);

        ConstraintLayout constraintLayout = view.findViewById(R.id.constraintLayout);
        TextView course_subject = view.findViewById(R.id.course_subject);
        TextView course_date = view.findViewById(R.id.course_date);
        TextView course_time = view.findViewById(R.id.course_time);
        TextView course_price = view.findViewById(R.id.course_price);
        TextView course_seats = view.findViewById(R.id.course_seats);
        Button join_course = view.findViewById(R.id.join_course);
        course_subject.setText(documentSnapshot.getString("course_name"));
        course_date.setText(documentSnapshot.getString("course_date"));
        course_time.setText(documentSnapshot.getString("course_time"));
        course_price.setText(documentSnapshot.getString("course_price"));
        course_seats.setText(documentSnapshot.getString("course_seats"));

        join_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(Courses.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.payment);

                EditText card_number = dialog.findViewById(R.id.card_number);
                card_number.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int arg1, int arg2,
                                              int arg3) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String initial = s.toString();
                        String processed = initial.replaceAll("\\D", "");
                        processed = processed.replaceAll("(\\d{4})(?=\\d)", "$1 ");

                        if (!initial.equals(processed)) {
                            s.replace(0, initial.length(), processed);
                        }

                    }

                });
                EditText expiry = dialog.findViewById(R.id.expiry);
                EditText csv = dialog.findViewById(R.id.csv);
                Button pay = dialog.findViewById(R.id.pay);
                pay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String card_number_txt = card_number.getText().toString();
                        String expiry_txt = expiry.getText().toString();
                        String csv_txt = csv.getText().toString();

                        boolean flag = true;
                        if (card_number_txt.equals("")) {
                            flag = false;
                            card_number.setError("*");
                        }
                        if (expiry_txt.equals("")) {
                            flag = false;
                            expiry.setError("*");
                        }
                        if (csv_txt.equals("")) {
                            flag = false;
                            csv.setError("*");
                        }
                        if (!flag) return;


                        DocumentReference course = fStore.collection("Courses").document(id);
                        Map<String, Object> course_data = new HashMap<>();
                        course_data.put(UID, "joined");

                        course.update(course_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                finish();
                                startActivity(getIntent());

                            }
                        });


                    }
                });


                dialog.show();


            }
        });


        String dtStart = documentSnapshot.getString("course_date") + " " + documentSnapshot.getString("course_time");


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date strDate = sdf.parse(dtStart);
        if (System.currentTimeMillis() > strDate.getTime()) {
            constraintLayout.removeView(join_course);
            linear_request_Ended.addView(view);
        } else {

            if (documentSnapshot.contains(UID)) {
                constraintLayout.removeView(join_course);

                linear_request_Joined.addView(view);
            } else {
                linear_request_available.addView(view);
            }
        }

    }
}