package com.example.pastilleroapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView lvScheduleList;
    private TextView tvEmptyMessage;
    private FloatingActionButton btnAddSchedule;
    private Button btnViewHistory;
    private List<ScheduledTime> schedulesList;
    private List<String> stringList;
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lvScheduleList = findViewById(R.id.lvScheduleList);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        btnAddSchedule = findViewById(R.id.fabAddSchedule);
        btnViewHistory = findViewById(R.id.btnViewHistory);

        schedulesList = ScheduleStorage.load(this);
        stringList = new ArrayList<>();

        for (ScheduledTime item : schedulesList) {
            stringList.add(item.getDateTime());
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringList);
        lvScheduleList.setAdapter(adapter);

        ifSchedulesListEmpty();

        btnAddSchedule.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddScheduleActivity.class);
            startActivity(intent);
        });

        btnViewHistory.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });
    }

    private void ifSchedulesListEmpty() {
        if (stringList.isEmpty()) {
            tvEmptyMessage.setVisibility(View.VISIBLE);
            lvScheduleList.setVisibility(View.GONE);
        } else {
            tvEmptyMessage.setVisibility(View.GONE);
            lvScheduleList.setVisibility(View.VISIBLE);
        }
    }
}

