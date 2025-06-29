package com.example.pastilleroapp;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ScheduleWorker extends Worker {

    public ScheduleWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String dateTimeStr = getInputData().getString("dateTime");
        if (dateTimeStr == null) {
            return Result.failure();
        }
        Long dateTime = Long.valueOf(dateTimeStr);
        Intent intent = new Intent(getApplicationContext(), MQTTForegroundService.class);
        intent.setAction("com.example.pastilleroapp.mqtt.ACTION_PUBLISH_MQTT");
        intent.putExtra("extra_topic", "/v1.6/devices/esp32/fecha");
        intent.putExtra("extra_message", dateTimeStr);

        // getApplicationContext().startService(intent);
        ContextCompat.startForegroundService(getApplicationContext(), intent);
        return Result.success();
    }
}