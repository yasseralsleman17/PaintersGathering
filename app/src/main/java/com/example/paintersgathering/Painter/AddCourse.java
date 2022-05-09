package com.example.paintersgathering.Painter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.paintersgathering.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddCourse extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    TextView course_date, course_time;
    EditText course_seats, course_price, course_name;
    Button create_course;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    String UID, painter_name, date="", time="", seats, price, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        UID = fAuth.getCurrentUser().getUid();
        fStore.collection("Users").document(UID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        painter_name = documentSnapshot.getString("FullName");

                    }
                });
        course_date = findViewById(R.id.course_date);
        course_time = findViewById(R.id.course_time);
        course_seats = findViewById(R.id.course_seats);
        course_price = findViewById(R.id.course_price);
        course_name = findViewById(R.id.course_name);
        create_course = findViewById(R.id.create_course);

        course_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        course_time.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
            }
        });

        create_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = true;
                seats = course_seats.getText().toString().trim();
                price = course_price.getText().toString().trim();
                name = course_name.getText().toString().trim();
                if (name.equals("")) {
                    course_name.setError("This field is required");  flag = false;
                }
                if (price.equals("")) {
                    course_price.setError("This field is required");  flag = false;
                }
                if (seats.equals("")) {
                    course_seats.setError("This field is required");   flag = false;
                }
                if (date.equals("") || time.equals("")) {   flag = false;
                    Toast.makeText(getApplicationContext(), "select date and time to add course", Toast.LENGTH_LONG).show();
                }
                if (!flag) { return;
                }

                DocumentReference course_ref = fStore.collection("Courses").document();
                Map<String, Object> course = new HashMap<>();

                course.put("painter_name", painter_name);
                course.put("painter_id", UID);
                course.put("course_date", date);
                course.put("course_time", time);
                course.put("course_seats", seats);
                course.put("course_price", price);
                course.put("course_name", name);

                course_ref.set(course).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "course added successfully", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });


            }
        });


    }


    public void showDatePickerDialog() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    this,
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showTimePickerDialog() {
        TimePickerDialog TimePickerDialog = new TimePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                DateFormat.is24HourFormat(getApplicationContext()));

        TimePickerDialog.show();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date = dayOfMonth + "/" + month + "/" + year;
        course_date.setText(date);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        time = hourOfDay + ":" + minute;
        course_time.setText(time);
    }


}