package com.example.infinitycrop.ui.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


import com.example.comun.Mqtt;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.comun.Mqtt.topicRoot;


public class ServicioMqtt extends Service implements MqttCallback {

    public static MqttClient client = null;
    private FirebaseFirestore db;
    private String getId;

    @Override public void onCreate(){
        Toast.makeText(this,"Conectando con tu maquina......", Toast.LENGTH_LONG).show();
        db = FirebaseFirestore.getInstance();
        try {
            client = new MqttClient(Mqtt.broker, Mqtt.clientId, new
                    MemoryPersistence());
        } catch (MqttException e) {
            e.printStackTrace();
        }
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setKeepAliveInterval(60);
        connOpts.setWill(topicRoot+"WillTopic", "App desconectada".getBytes(),Mqtt.qos, false);
        try {
            client.connect(connOpts);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        // Nos suscribimos al topic rfid
        try {
            Log.i(Mqtt.TAG, "Suscrito a " + topicRoot+"lecturaDatos");
            client.subscribe(topicRoot+"lecturaDatos", Mqtt.qos);
            client.setCallback(this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }
        try {
            Log.i(Mqtt.TAG, "Suscrito a " + topicRoot+"lecturaDatos1");
            client.subscribe(topicRoot+"lecturaDatos1", Mqtt.qos);
            client.setCallback(this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }

        try {
            Log.i(Mqtt.TAG, "Suscrito a " + topicRoot+"lecturaDatos2");
            client.subscribe(topicRoot+"lecturaDatos2", Mqtt.qos);
            client.setCallback(this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }
    }

    @Override
    public int onStartCommand(Intent intenc, int flags, int idArranque) {
        getId =intenc.getStringExtra("machine");

        return START_STICKY;
    }
    @Override public void onDestroy() {

    }
    @Override public IBinder onBind(Intent intencion) {
        return null;
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        String[] parts = payload.split("-");
        String part1 = parts[0];
        String part2 = parts[1];
        String part3 = parts[2];
        String part4 = parts[3];
        String part5 = parts[4];

        if(topic.equals(topicRoot+"lecturaDatos")){
            Map<String, Object> user = new HashMap<>();
            user.put("Humedad", part2);
            user.put("Humedad Ambiente", part3);
            user.put("Luminosidad", part4);
            user.put("Temperatura", part5);
            user.put("Fecha", new Timestamp(new Date()));
            user.put("machineId", part1);
            db.collection("Mediciones general").document(getId).set(user);
        }
        if(topic.equals(topicRoot+"lecturaDatos1")){
            Map<String, Object> user = new HashMap<>();
            user.put("Humedad", part2);
            user.put("Humedad Ambiente", part3);
            user.put("Luminosidad", part4);
            user.put("Temperatura", part5);
            user.put("Fecha", new Timestamp(new Date()));
            user.put("machineId", part1);
            db.collection("Mediciones planta 1").document(getId).set(user);
        }
        if(topic.equals(topicRoot+"lecturaDatos2")){
            Map<String, Object> user = new HashMap<>();
            user.put("Humedad", part2);
            user.put("Humedad Ambiente", part3);
            user.put("Luminosidad", part4);
            user.put("Temperatura", part5);
            user.put("Fecha", new Timestamp(new Date()));
            user.put("machineId", part1);
            db.collection("Mediciones planta 2").document(getId).set(user);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}