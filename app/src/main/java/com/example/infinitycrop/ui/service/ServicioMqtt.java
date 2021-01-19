package com.example.infinitycrop.ui.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.comun.Mqtt;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

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
    private DatabaseReference databaseReference;
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

        //notificaciones

    }

    @Override
    public int onStartCommand(Intent intenc, int flags, int idArranque) {
        getId =intenc.getStringExtra("machine");
        databaseReference= FirebaseDatabase.getInstance().getReference();
        //lectura real-time database
        databaseReference.child("Mediciones general")
                .child(getId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String luz= snapshot.child("Luminosidad").getValue().toString();
                    String humedad=snapshot.child("MediaHumedad").getValue().toString();
                    String temper=snapshot.child("MediaTemperatura").getValue().toString();
                    final Map<String, Object> a = new HashMap<>();
                    a.put("Humedad", humedad);
                    a.put("Luminosidad", luz);
                    a.put("Temperatura", temper);
                    a.put("Fecha", new Timestamp(new Date()));
                    a.put("machineId", getId);
                    db.collection("Mediciones general").document(getId)
                            .set(a);

                    db.collection("Mediciones").add(a);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.child("Mediciones nivel 1")
                .child(getId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String humedadAmbiente= snapshot.child("HumedadAmbiente").getValue().toString();
                    String humedad=snapshot.child("Humedad").getValue().toString();
                    String temper=snapshot.child("TemperaturaAmbiente").getValue().toString();
                    String pres1=snapshot.child("Presencia1").getValue().toString();
                    String pres2=snapshot.child("Presencia2").getValue().toString();
                    String pres3=snapshot.child("Presencia3").getValue().toString();
                    final Map<String, Object> a = new HashMap<>();
                    a.put("Humedad", humedad);
                    a.put("Humedad Ambiente", humedadAmbiente);
                    a.put("Temperatura", temper);
                    a.put("Fecha", new Timestamp(new Date()));
                    a.put("machineId", getId);
                    db.collection("Mediciones planta 1").document(getId)
                            .set(a);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.child("Mediciones nivel 2")
                .child(getId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String humedadAmbiente= snapshot.child("HumedadAmbiente").getValue().toString();
                    String temper=snapshot.child("TemperaturaAmbiente").getValue().toString();
                    String pres1=snapshot.child("Presencia1").getValue().toString();
                    String pres2=snapshot.child("Presencia2").getValue().toString();
                    String pres3=snapshot.child("Presencia3").getValue().toString();
                    final Map<String, Object> a = new HashMap<>();
                    a.put("Humedad Ambiente", humedadAmbiente);
                    a.put("Temperatura", temper);
                    a.put("Fecha", new Timestamp(new Date()));
                    a.put("machineId", getId);
                    db.collection("Mediciones planta 2").document(getId)
                            .set(a);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
        /*String[] parts = payload.split("-");
        String part1 = parts[0];
        String part2 = parts[1];
        String part3 = parts[2];
        String part4 = parts[3];
        String part5 = parts[4];

        switch (topic) {
            case topicRoot + "lecturaDatos": {
                Map<String, Object> user = new HashMap<>();
                user.put("Humedad", part2);
                user.put("Humedad Ambiente", part3);
                user.put("Luminosidad", part4);
                user.put("Temperatura", part5);
                user.put("Fecha", new Timestamp(new Date()));
                user.put("machineId", part1);
                db.collection("Mediciones general").document(part1).set(user);
                break;
            }
            case topicRoot + "lecturaDatos1": {
                Map<String, Object> user = new HashMap<>();
                user.put("Humedad", part2);
                user.put("Humedad Ambiente", part3);
                user.put("Luminosidad", part4);
                user.put("Temperatura", part5);
                user.put("Fecha", new Timestamp(new Date()));
                user.put("machineId", part1);
                db.collection("Mediciones planta 1").document(part1).set(user);
                break;
            }
            case topicRoot + "lecturaDatos2": {
                Map<String, Object> user = new HashMap<>();
                user.put("Humedad", part2);
                user.put("Humedad Ambiente", part3);
                user.put("Luminosidad", part4);
                user.put("Temperatura", part5);
                user.put("Fecha", new Timestamp(new Date()));
                user.put("machineId", part1);
                db.collection("Mediciones planta 2").document(part1).set(user);
                break;
            }
        }*/

        db.collection("Mediciones general").document(getId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                //Temperatura
                String temperatura = value.getString("Temperatura");
                Double tempValue = Double.parseDouble(temperatura);
                if (tempValue > 7) {
                    final Map<String, Object> notificacionesTempAlerta = new HashMap<>();
                    notificacionesTempAlerta.put("Tipo", "1");
                    notificacionesTempAlerta.put("MachineID", getId);
                    notificacionesTempAlerta.put("Fecha", new Timestamp(new Date()));
                    notificacionesTempAlerta.put("Medicion", "temperatura");

                    db.collection("Notificaciones").add(notificacionesTempAlerta);
                }

                if (tempValue > 3 && tempValue < 6) {

                    final Map<String, Object> notificacionesTempAviso = new HashMap<>();
                    notificacionesTempAviso.put("Tipo", "2");
                    notificacionesTempAviso.put("MachineID", getId);
                    notificacionesTempAviso.put("Fecha", new Timestamp(new Date()));
                    notificacionesTempAviso.put("Medicion", "temperatura");

                    db.collection("Notificaciones").add(notificacionesTempAviso);
                }


                //Humedad
                String humedad = value.getString("Humedad");
                Double humedadValue = Double.parseDouble(humedad);
                if (humedadValue > 13) {
                    final Map<String, Object> notificacionesHumedadAlerta = new HashMap<>();
                    notificacionesHumedadAlerta.put("Tipo", "1");
                    notificacionesHumedadAlerta.put("MachineID", getId);
                    notificacionesHumedadAlerta.put("Fecha", new Timestamp(new Date()));
                    notificacionesHumedadAlerta.put("Medicion", "humedad");

                    db.collection("Notificaciones").add(notificacionesHumedadAlerta);
                }

                if (humedadValue > 5 && humedadValue < 8) {
                    final Map<String, Object> notificacionesHumedadAviso = new HashMap<>();
                    notificacionesHumedadAviso.put("Tipo", "2");
                    notificacionesHumedadAviso.put("MachineID", getId);
                    notificacionesHumedadAviso.put("Fecha", new Timestamp(new Date()));
                    notificacionesHumedadAviso.put("Medicion", "humedad");

                    db.collection("Notificaciones").add(notificacionesHumedadAviso);

                }


                //Humedad Ambiente
                String humedadAmb = value.getString("Humedad Ambiente");
                Double humedadAmbValue = Double.parseDouble(humedadAmb);
                if (humedadAmbValue > 13) {
                    final Map<String, Object> notificacionesHumedadAmbAlerta = new HashMap<>();
                    notificacionesHumedadAmbAlerta.put("Tipo", "1");
                    notificacionesHumedadAmbAlerta.put("MachineID", getId);
                    notificacionesHumedadAmbAlerta.put("Fecha", new Timestamp(new Date()));
                    notificacionesHumedadAmbAlerta.put("Medicion", "humedad ambiente");

                    db.collection("Notificaciones").add(notificacionesHumedadAmbAlerta);

                }
                if (humedadAmbValue > 5 && humedadAmbValue < 12) {
                    final Map<String, Object> notificacionesHumedadAmbAviso = new HashMap<>();
                    notificacionesHumedadAmbAviso.put("Tipo", "2");
                    notificacionesHumedadAmbAviso.put("MachineID", getId);
                    notificacionesHumedadAmbAviso.put("Fecha", new Timestamp(new Date()));
                    notificacionesHumedadAmbAviso.put("Medicion", "humedad ambiente");


                    db.collection("Notificaciones").add(notificacionesHumedadAmbAviso);
                }


                //Luminosidad
                String luminosidad = value.getString("Luminosidad");
                Double luminosidadValue = Double.parseDouble(luminosidad);

                if (luminosidadValue > 54) {
                    final Map<String, Object> notificacionesLuminosidadAlerta = new HashMap<>();
                    notificacionesLuminosidadAlerta.put("Tipo", "1");
                    notificacionesLuminosidadAlerta.put("MachineID", getId);
                    notificacionesLuminosidadAlerta.put("Fecha", new Timestamp(new Date()));
                    notificacionesLuminosidadAlerta.put("Medicion", "luminosidad");

                    db.collection("Notificaciones").add(notificacionesLuminosidadAlerta);
                }
                if (luminosidadValue > 36 && luminosidadValue < 47) {
                    final Map<String, Object> notificacionesLuminosidadAviso = new HashMap<>();
                    notificacionesLuminosidadAviso.put("Tipo", "2");
                    notificacionesLuminosidadAviso.put("MachineID", getId);
                    notificacionesLuminosidadAviso.put("Fecha", new Timestamp(new Date()));
                    notificacionesLuminosidadAviso.put("Medicion", "luminosidad");

                    db.collection("Notificaciones").add(notificacionesLuminosidadAviso);
                }
            }
        });
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}