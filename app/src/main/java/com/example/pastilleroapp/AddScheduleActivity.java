package com.example.pastilleroapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class AddScheduleActivity extends AppCompatActivity {
    TextView tvDateTime;
    Button btnPickDate, btnSave;
    String finalDateTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_schedule);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.schedule), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvDateTime = findViewById(R.id.tvDateTime);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnSave = findViewById(R.id.btnSave);

        this.setupListeners();
    }

    private void setupListeners() {
        btnPickDate.setOnClickListener(v -> pickDateTime());

        btnSave.setOnClickListener(v -> {
            if (finalDateTime.isEmpty()) {
                Toast.makeText(this, "Please select a date and time", Toast.LENGTH_SHORT).show();
            } else {
                ScheduledTime newTime = new ScheduledTime(finalDateTime);
                ScheduleStorage.add(this, newTime);
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                finish(); // Go back to MainActivity
            }
        });
    }

    // Open DatePicker then TimePicker
    void pickDateTime() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, day) -> {
            TimePickerDialog timePicker = new TimePickerDialog(this, (tpView, hour, minute) -> {
                int second = 0;
                finalDateTime = String.format("%04d-%02d-%02d %02d:%02d:%02d",
                        year, month + 1, day, hour, minute, second);
                tvDateTime.setText(finalDateTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

            timePicker.show();

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
        datePicker.show();
    }
}