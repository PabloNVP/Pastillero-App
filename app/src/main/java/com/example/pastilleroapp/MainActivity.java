package com.example.pastilleroapp;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "MainActivity";
    private ListView lvScheduleList;
    private TextView tvEmptyMessage;
    private FloatingActionButton btnAddSchedule;
    private Button btnViewHistory;
    private List<ScheduledTime> schedulesList;
    private List<String> stringList;
    private ArrayAdapter<String> adapter;

    private SensorManager sensorManager;
    private long lastShakeTime = 0;
    private static final int UMBRAL_SHAKE_THRESHOLD = 20;
    private static final int UMBRAL_SHAKE_TIMEOUT = 2000;

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

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

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

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),   SensorManager.SENSOR_DELAY_NORMAL);

        schedulesList = ScheduleStorage.load(this);
        stringList.clear();

        for (ScheduledTime item : schedulesList) {
            stringList.add(item.getDateTime());
        }

        adapter.notifyDataSetChanged();

        ifSchedulesListEmpty();
    }

    @Override
    protected void onPause(){
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        super.onPause();
    }

    @Override
    protected void onRestart(){
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),   SensorManager.SENSOR_DELAY_NORMAL);
        super.onRestart();
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        long ct = System.currentTimeMillis();

        if ((ct - lastShakeTime) > UMBRAL_SHAKE_TIMEOUT) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double acceleration = Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;

            if (acceleration > UMBRAL_SHAKE_THRESHOLD) {
                lastShakeTime = ct;

                Intent intent = new Intent(MainActivity.this, VolumeActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

