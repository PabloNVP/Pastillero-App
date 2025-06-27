package com.example.pastilleroapp;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTForegroundService extends Service implements MqttCallback {
    private static final String CHANNEL_ID = "MQTTServiceChannel";
    private MqttClient mqttClient;

    private static final String BROKER = "tcp://industrial.api.ubidots.com:1883";
    private static final String CLIENT_ID = "android_client";
    public static final String USER="BBUS-ESPZX2ACUxkzX1imwO6uDf35YUa66Y";
    public static final String PASS="BBUS-ESPZX2ACUxkzX1imwO6uDf35YUa66Y";

    private static final String TOPIC_DATE = "/v1.6/devices/esp32/fecha";
    private static final String TOPIC_VOLUME = "/v1.6/devices/esp32/volume";

    public static final String ACTION_PUBLISH_MQTT = "com.example.pastilleroapp.mqtt.ACTION_PUBLISH_MQTT";
    public static final String EXTRA_TOPIC = "extra_topic";
    public static final String EXTRA_MESSAGE = "extra_message";

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void startMQTT() {
        try {
            MemoryPersistence persistence = new MemoryPersistence();

            mqttClient = new MqttClient(BROKER, CLIENT_ID, persistence);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName(USER);
            options.setPassword(PASS.toCharArray());

            mqttClient.setCallback(this);

            mqttClient.connect(options);
            mqttClient.subscribe(TOPIC_VOLUME);
            Log.i("MQTT", "Conexión establecida correctamente");
        } catch (MqttException e) {
            Log.e("MQTT", "Error al conectar: " + e.getMessage() + " Código: " + e.getReasonCode());
        }
    }

    private void createNotificationChannel() {
        NotificationChannel canal = new NotificationChannel(
            CHANNEL_ID,
        "Canal de Servicio MQTT",
            NotificationManager.IMPORTANCE_LOW
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(canal);
        }
    }

    private Notification createNotification(String msg) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Servicio MQTT")
            .setContentText(msg)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build();
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        startForeground(1, createNotification("Servicio MQTT activo."));

        startMQTT();

        if (intent != null && ACTION_PUBLISH_MQTT.equals(intent.getAction())) {
            String topic = intent.getStringExtra(EXTRA_TOPIC);
            String message = intent.getStringExtra(EXTRA_MESSAGE);
            publish(topic, message);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e("MQTT", "Servicio MQTT detenido.");
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
            }
        } catch ( MqttException e) {
            Log.e("MQTT", "Error al desconectar: " + e.getMessage());
        }
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.e("MQTT", "Conexión MQTT perdida.");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.d("MQTT", "Mensaje MQTT: " + message.toString());
    }

    private void publish(String topic, String message) {
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                MqttMessage mqttMessage = new MqttMessage(message.getBytes());
                mqttMessage.setQos(1);
                mqttClient.publish(topic, mqttMessage);
                Log.d("MQTT", "Publicado: " + message + " en " + topic);
            } catch (MqttException e) {
                Log.e("MQTT", "Error al publicar: " + e.getMessage());
            }
        } else {
            Log.e("MQTT", "Cliente no conectado, no se puede publicar.");
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
